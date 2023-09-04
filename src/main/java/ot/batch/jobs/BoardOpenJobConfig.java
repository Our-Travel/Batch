package ot.batch.jobs;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ot.batch.repository.book.RecruitmentStatus;
import ot.batch.repository.book.TravelBoard;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class BoardOpenJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    public BoardOpenJobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, EntityManagerFactory entityManagerFactory){
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job boardOpenJob(){
        return this.jobBuilderFactory.get("boardOpenJob")
                .start(boardOpenStep())
                .build();
    }

    @Bean
    public Step boardOpenStep(){
        return this.stepBuilderFactory.get("boardOpenStep")
                .<TravelBoard, TravelBoard>chunk(50)
                .reader(boardOpenItemReader())
                .processor(boardOpenItemProcessor())
                .writer(boardOpenItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaCursorItemReader<TravelBoard> boardOpenItemReader(){
        return new JpaCursorItemReaderBuilder<TravelBoard>()
                .name("boardOpenItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select b from TravelBoard b where b.recruitmentStatus = :recruitmentStatus and b.recruitmentPeriodStart <= :now and b.deletedDate IS NULL")
                .parameterValues(Map.of("recruitmentStatus", RecruitmentStatus.UPCOMING, "now", LocalDateTime.now()))
                .build();
    }

    @Bean
    public ItemProcessor<TravelBoard, TravelBoard> boardOpenItemProcessor(){
        return travelBoard -> {
            travelBoard.updateRecruitmentStatus(RecruitmentStatus.OPEN);
            return travelBoard;
        };
    }

    @Bean
    public JpaItemWriter<TravelBoard> boardOpenItemWriter(){
        return new JpaItemWriterBuilder<TravelBoard>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
