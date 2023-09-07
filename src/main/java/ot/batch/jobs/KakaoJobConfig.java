//package ot.batch.jobs;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.JobScope;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.item.support.ListItemReader;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import ot.batch.api.kakao.dto.DocumentDTO;
//import ot.batch.api.kakao.dto.KakaoApiResponseDTO;
//import ot.batch.api.kakao.service.KakaoCategorySearchService;
//import ot.batch.repository.travelInfo.TravelInfo;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.EntityTransaction;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//@RequiredArgsConstructor
//public class KakaoJobConfig {
//
//    private final JobBuilderFactory jobBuilderFactory;
//    private final StepBuilderFactory stepBuilderFactory;
//    private final EntityManagerFactory entityManagerFactory;
//    private final KakaoCategorySearchService kakaoCategorySearchService;
//
//
//    @Bean
//    public Job kakaoApiJob() {
//        return this.jobBuilderFactory.get("kakaoApiJob")
//                .start(kakaoApiStep())
//                .incrementer(new RunIdIncrementer())
//                .build();
//    }
//
//    @Bean
//    @JobScope
//    public Step kakaoApiStep() {
//        return this.stepBuilderFactory.get("kakaoApiStep")
//                .<KakaoApiResponseDTO, List<TravelInfo>>chunk(50)
//                .reader(kakaoApiItemReader())
//                .processor(kakaoApiItemProcessor())
//                .writer(kakaoApiItemWriter(entityManagerFactory))
//                .build();
//    }
//
//    @Bean
//    @StepScope
//    public ItemReader<KakaoApiResponseDTO> kakaoApiItemReader() {
//
//        List<KakaoApiResponseDTO> kakaoApiResults = fetchKakaoApiResults();
//
//        return new ListItemReader<>(kakaoApiResults);
//    }
//
//    private List<KakaoApiResponseDTO> fetchKakaoApiResults() {
//        String categoryGroupCode = "AD5";
//
//        // 위도와 경도의 리스트를 생성
//        List<double[]> coordinates = Arrays.asList(
//                new double[]{37.209048294851, 127.07402210784},
//                new double[]{37.465630592673, 126.71764789035},
//                new double[]{37.8846833328824, 127.716986362084},
//                new double[]{33.54324701322319, 126.65981715873815},
//                new double[]{37.485436103793, 126.92933229332}
//        );
//
//        List<KakaoApiResponseDTO> results = new ArrayList<>();
//
//        // 각 좌표에 대해 API 호출
//        for (double[] coordinate : coordinates) {
//            double latitude = coordinate[0];
//            double longitude = coordinate[1];
//            for (int i = 1; i <= 45; i++) {
//                KakaoApiResponseDTO response = kakaoCategorySearchService.requestCategorySearch(categoryGroupCode, longitude, latitude, i);
//                results.add(response);
//            }
//        }
//
//        return results;
//    }
//
//    @Bean
//    @StepScope
//    public ItemProcessor<KakaoApiResponseDTO, List<TravelInfo>> kakaoApiItemProcessor() {
//        return new ItemProcessor<KakaoApiResponseDTO, List<TravelInfo>>() {
//            @Override
//            public List<TravelInfo> process(KakaoApiResponseDTO kakaoApiResponseDTO) throws Exception {
//                List<TravelInfo> travelInfoList = new ArrayList<>();
//
//                if (kakaoApiResponseDTO != null && kakaoApiResponseDTO.getDocumentDTOList() != null) {
//                    for (DocumentDTO documentDTO : kakaoApiResponseDTO.getDocumentDTOList()) {
//                        TravelInfo travelInfoKakao = TravelInfo.of(documentDTO);
//                        travelInfoList.add(travelInfoKakao);
//                    }
//                }
//                return travelInfoList;
//            }
//        };
//    }
//
//    @Bean
//    @StepScope
//    public ItemWriter<List<TravelInfo>> kakaoApiItemWriter(EntityManagerFactory entityManagerFactory) {
//        return new ItemWriter<List<TravelInfo>>() {
//            @Override
//            public void write(List<? extends List<TravelInfo>> lists) throws Exception {
//                EntityManager entityManager = entityManagerFactory.createEntityManager();
//                EntityTransaction transaction = entityManager.getTransaction();
//
//                try {
//                    transaction.begin();
//
//                    for (List<TravelInfo> list : lists) {
//                        for (TravelInfo travelInfo : list) {
//                            Long placeId = travelInfo.getPlaceId();
//
//                            TravelInfo existingTravelInfo = entityManager.find(TravelInfo.class, placeId);
//
//                            if (existingTravelInfo == null) {
//                                entityManager.persist(travelInfo);
//                            }
//                        }
//                    }
//
//                    transaction.commit();
//                } catch (Exception e) {
//                    transaction.rollback();
//                    throw e;
//                } finally {
//                    entityManager.close();
//                }
//            }
//        };
//    }
//}
