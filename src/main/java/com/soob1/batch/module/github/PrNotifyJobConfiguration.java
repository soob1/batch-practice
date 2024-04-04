package com.soob1.batch.module.github;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PrNotifyJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final PrNotifyStepConfiguration prNotifyStepConfiguration;

    @Bean
    public Job prNotificationJob() {
        return jobBuilderFactory.get("prNotificationJob")
                .start(prNotifyStepConfiguration.getPrListStep())
                .next(prNotifyStepConfiguration.sendNotificationStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }
}
