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
public class BoardClosedJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    public BoardClosedJobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, EntityManagerFactory entityManagerFactory){
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job boardClosedJob(){
        return this.jobBuilderFactory.get("boardClosedJob")
                .start(boardClosedStep())
                .build();
    }

    @Bean
    public Step boardClosedStep(){
        return this.stepBuilderFactory.get("boardClosedStep")
                .<TravelBoard, TravelBoard>chunk(60)
                .reader(boardClosedItemReader())
                .processor(boardClosedItemProcessor())
                .writer(boardClosedItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaCursorItemReader<TravelBoard> boardClosedItemReader(){
        return new JpaCursorItemReaderBuilder<TravelBoard>()
                .name("boardClosedItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select b from TravelBoard b where (b.recruitmentStatus = :recruitmentStatus or b.recruitmentStatus = :personnel) and b.recruitmentPeriodEnd <= :now and b.deletedDate IS NULL")
                .parameterValues(Map.of("recruitmentStatus", RecruitmentStatus.OPEN, "personnel", RecruitmentStatus.FULL, "now", LocalDateTime.now()))
                .build();
    }

    @Bean
    public ItemProcessor<TravelBoard, TravelBoard> boardClosedItemProcessor(){
        return travelBoard -> {
            travelBoard.updateRecruitmentStatus(RecruitmentStatus.CLOSED);
            return travelBoard;
        };
    }

    @Bean
    public JpaItemWriter<TravelBoard> boardClosedItemWriter(){
        return new JpaItemWriterBuilder<TravelBoard>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
