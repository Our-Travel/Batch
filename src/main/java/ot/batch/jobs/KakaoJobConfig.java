package ot.batch.jobs;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
@RequiredArgsConstructor
public class KakaoJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job kakaoApiJob(){
        return this.jobBuilderFactory.get("kakaoApiJob")
                .start(kakaoApiStep())
                .build();
    }

    @Bean
    public Step kakaoApiStep(){
        return this.stepBuilderFactory.get("kakaoApiStep")
                .<Pass>
    }
}
