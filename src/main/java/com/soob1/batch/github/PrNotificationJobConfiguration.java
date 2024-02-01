package com.soob1.batch.github;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PrNotificationJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final PrNotificationStepConfiguration prNotificationStepConfiguration;

    @Bean
    public Job prNotificationJob() {
        return jobBuilderFactory.get("prNotificationJob")
                .start(prNotificationStepConfiguration.getRepoStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }
}
