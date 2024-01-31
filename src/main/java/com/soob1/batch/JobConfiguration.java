package com.soob1.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
                .start(step1())
                .next(step2())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet((StepContribution stepContribution, ChunkContext chunkContext) -> {
                    System.out.println("=======================");
                    System.out.println(" >> Hello Spring Batch");
                    System.out.println("=======================");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet((StepContribution stepContribution, ChunkContext chunkContext) -> {
                    System.out.println("=======================");
                    System.out.println(" >> step2 was executed");
                    System.out.println("=======================");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
