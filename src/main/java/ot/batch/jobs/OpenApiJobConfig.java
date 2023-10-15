package ot.batch.jobs;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import ot.batch.api.open.dto.OpenApiCommonDto;
import ot.batch.api.open.dto.OpenApiInfoDto;
import ot.batch.api.open.service.OpenApiService;
import ot.batch.repository.travelInfo.TravelInfo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Configuration
@RequiredArgsConstructor
public class OpenApiJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final OpenApiService openApiService;
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Bean
    public Job openApiJob() {
        return this.jobBuilderFactory.get("openApiJob")
                .start(openApiInfoStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    @JobScope
    public Step openApiInfoStep() {
        return this.stepBuilderFactory.get("openApiInfoStep")
                .<OpenApiInfoDto, List<TravelInfo>>chunk(50)
                .reader(openApiInfoReader())
                .processor(openApiInfoProcessor())
                .writer(openApiInfoWriter(entityManagerFactory))
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<OpenApiInfoDto> openApiInfoReader() {

        List<OpenApiInfoDto> openResults = fetchOpenApiResults();

        return new ListItemReader<>(openResults);
    }

    private List<OpenApiInfoDto> fetchOpenApiResults() {
        List<OpenApiInfoDto> results = new ArrayList<>();

        /*
        관광 정보 API 호출
         */
        int[] areaCodeAry = {1, 2, 3, 4, 5, 6, 7, 8, 31, 32};
        int[] contentTypeIdAry = {12, 14, 25, 28};
        for(int areaCode : areaCodeAry){
            for(int contentTypeId : contentTypeIdAry){
                int areaBasedMaxPage = openApiService.areaBasedMaxPage(contentTypeId, areaCode);
                for(int i=1; i<=areaBasedMaxPage; i++){
                    results.addAll(openApiService.requestAreaBased(contentTypeId, areaCode, i));
                }

            }
        }
        /*
        행사 정보 API 호출
         */
        LocalDate now = LocalDate.now();
        int daysInMonth = now.lengthOfMonth();
        String[] daysArray = new String[daysInMonth];

        for (int i = 0; i < daysInMonth; i++) {
            LocalDate date = now.withDayOfMonth(i+1);
            daysArray[i] = date.format(formatter);
        }
        for(String eventStartDate : daysArray){
            int festivalMaxPage = openApiService.festivalMaxPage(eventStartDate);
            for(int i=1; i<=festivalMaxPage; i++){
                results.addAll(openApiService.requestFestival(i, eventStartDate));
            }
        }
        /*
        숙소 정보 API 호출
         */
        int stayMaxPage = openApiService.stayMaxPage();
        for(int i=1; i<=stayMaxPage; i++){
            results.addAll(openApiService.requestStay(i));
        }

        return results;
    }

    @Bean
    @StepScope
    public ItemProcessor<OpenApiInfoDto, List<TravelInfo>> openApiInfoProcessor() {
        return new ItemProcessor<OpenApiInfoDto, List<TravelInfo>>() {
            @Override
            public List<TravelInfo> process(OpenApiInfoDto openApiInfoDto) throws Exception {
                List<TravelInfo> travelInfoList = new ArrayList<>();

                if (!ObjectUtils.isEmpty(openApiInfoDto)) {
                    TravelInfo travelInfoKakao = TravelInfo.of(openApiInfoDto);
                    travelInfoList.add(travelInfoKakao);
                }
                List<TravelInfo> updatedTravelInfos = travelInfoList.parallelStream()
                        .map(travelInfo -> {
                            OpenApiCommonDto apiCommonDto = openApiService.requestCommon(travelInfo.getContentId(), travelInfo.getContentTypeId());
                            if (apiCommonDto != null) {
                                travelInfo.updateHomePage(apiCommonDto.getHomePage());
                                travelInfo.updateOverView(apiCommonDto.getOverView());
                                travelInfo.updateTelName(apiCommonDto.getTelName());
                            }
                            return travelInfo;
                        })
                        .toList();
                return updatedTravelInfos;
            }
        };
    }

    @Bean
    @StepScope
    public ItemWriter<List<TravelInfo>> openApiInfoWriter(EntityManagerFactory entityManagerFactory) {
        return new ItemWriter<List<TravelInfo>>() {
            @Override
            public void write(List<? extends List<TravelInfo>> lists) throws Exception {
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                EntityTransaction transaction = entityManager.getTransaction();

                try {
                    transaction.begin();

                    for (List<TravelInfo> list : lists) {
                        for (TravelInfo travelInfo : list) {
                            int contentId = travelInfo.getContentId();
                            int contentTypeId = travelInfo.getContentTypeId();

                            TypedQuery<Long> contentIdQuery = entityManager.createQuery(
                                    "SELECT COUNT(t) FROM TravelInfo t WHERE t.contentId = :contentId",
                                    Long.class
                            );
                            contentIdQuery.setParameter("contentId", contentId);

                            Long count = contentIdQuery.getSingleResult();
                            if (count == 0) {
                                TypedQuery<TravelInfo> query = entityManager.createQuery(
                                        "SELECT t FROM TravelInfo t WHERE t.contentId = :contentId AND t.contentTypeId = :contentTypeId AND t.deletedDate IS NOT NULL",
                                        TravelInfo.class
                                );
                                query.setParameter("contentId", contentId);
                                query.setParameter("contentTypeId", contentTypeId);

                                List<TravelInfo> existingTravelInfos = query.getResultList();

                                if (existingTravelInfos.isEmpty()) {
                                    entityManager.persist(travelInfo);
                                }
                            }
                        }
                    }

                    transaction.commit();
                } catch (Exception e) {
                    transaction.rollback();
                    throw e;
                } finally {
                    entityManager.close();
                }
            }
        };
    }

}
