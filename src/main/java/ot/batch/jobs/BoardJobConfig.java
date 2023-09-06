package ot.batch.jobs;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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
import java.time.LocalDate;
import java.util.Map;

@Configuration
public class BoardJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    public BoardJobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, EntityManagerFactory entityManagerFactory){
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job boardJob(){
        return this.jobBuilderFactory.get("boardJob")
                .start(boardOpenStep())
                .next(boardClosedStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    @JobScope
    public Step boardOpenStep(){
        return this.stepBuilderFactory.get("boardOpenStep")
                .<TravelBoard, TravelBoard>chunk(100)
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
                .parameterValues(Map.of("recruitmentStatus", RecruitmentStatus.UPCOMING, "now", LocalDate.now()))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<TravelBoard, TravelBoard> boardOpenItemProcessor(){
        return travelBoard -> {
            travelBoard.updateRecruitmentStatus(RecruitmentStatus.OPEN);
            return travelBoard;
        };
    }

    @Bean
    @StepScope
    public JpaItemWriter<TravelBoard> boardOpenItemWriter(){
        return new JpaItemWriterBuilder<TravelBoard>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    @JobScope
    public Step boardClosedStep(){
        return this.stepBuilderFactory.get("boardClosedStep")
                .<TravelBoard, TravelBoard>chunk(100)
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
                .parameterValues(Map.of("recruitmentStatus", RecruitmentStatus.OPEN, "personnel", RecruitmentStatus.FULL, "now", LocalDate.now()))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<TravelBoard, TravelBoard> boardClosedItemProcessor(){
        return travelBoard -> {
            travelBoard.updateRecruitmentStatus(RecruitmentStatus.CLOSED);
            return travelBoard;
        };
    }

    @Bean
    @StepScope
    public JpaItemWriter<TravelBoard> boardClosedItemWriter(){
        return new JpaItemWriterBuilder<TravelBoard>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
