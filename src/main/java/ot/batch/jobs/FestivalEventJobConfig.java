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
//import org.springframework.batch.item.database.JpaCursorItemReader;
//import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import ot.batch.repository.board.RecruitmentStatus;
//import ot.batch.repository.board.TravelBoard;
//import ot.batch.repository.travelInfo.TravelInfo;
//
//import javax.persistence.EntityManagerFactory;
//import java.time.LocalDate;
//import java.util.Map;
//
//@Configuration
//@RequiredArgsConstructor
//public class FestivalEventJobConfig {
//
//    private final JobBuilderFactory jobBuilderFactory;
//    private final StepBuilderFactory stepBuilderFactory;
//    private final EntityManagerFactory entityManagerFactory;
//
//    @Bean
//    public Job festivalEventJob(){
//        return this.jobBuilderFactory.get("festivalEventJob")
//                .start(festivalEventStep())
//                .incrementer(new RunIdIncrementer())
//                .build();
//    }
//
//    @Bean
//    @JobScope
//    public Step festivalEventStep(){
//        return this.stepBuilderFactory.get("festivalEventStep")
//                .<TravelInfo, TravelInfo>chunk(100)
//                .reader(festivalEventReader())
//                .processor(boardOpenItemProcessor())
//                .writer(boardOpenItemWriter())
//                .build();
//    }
//
//    @Bean
//    @StepScope
//    public JpaCursorItemReader<TravelInfo> festivalEventReader(){
//        return new JpaCursorItemReaderBuilder<TravelInfo>()
//                .name("festivalEventReader")
//                .entityManagerFactory(entityManagerFactory)
//                .queryString("select i from TravelInfo i where i.contentTypeId = :contentTypeId and b.recruitmentPeriodStart <= :now and b.deletedDate IS NULL")
//                .parameterValues(Map.of("contentTypeId", 15, "now", LocalDate.now()))
//                .build();
//    }
//}
