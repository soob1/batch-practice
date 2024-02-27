package com.soob1.batch.notify;

import com.soob1.batch.notify.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SendNotificationJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private Integer chunkSize = 3;

    @Bean
    public Job sendNotificationJob() {
        // 이벤트 아이디를 jobParameter로 받아 해당 이벤트에 대한 알림 신청한 사람을 조회한다.
        // 알림 신청한 사람들에게 알림을 발송하고 상태를 업데이트한다.
        return jobBuilderFactory.get("sendNotificationJob")
                .start(sendNotificationStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step sendNotificationStep() {
        return stepBuilderFactory.get("sendNotificationStep")
                .<Notification, Notification>chunk(chunkSize)
                .reader(notificationReader(null))
                .processor(notificationSender())
                .writer(notificationWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Notification> notificationReader(@Value("#{jobParameters['eventId']}") Long eventId) {
        // https://jojoldu.tistory.com/132
        return new JpaPagingItemReaderBuilder<Notification>()
                .name("notificationUserReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT n FROM Notification n WHERE n.eventId = :eventId AND n.status != 'SENT' ORDER BY n.id ASC")
                .parameterValues(Map.of("eventId", eventId))
                .build();
    }

    @Bean
    public ItemProcessor<Notification, Notification> notificationSender() {
        return notification -> {
            try {
                // 알림 발송 로직
                System.out.println("사용자" + notification.getUserId() + "에게 알림을 발송했습니다.");
                notification.sendSuccess();
                return notification;
            } catch (Exception e) {
                notification.sendFail();
                return null;
            }
        };
    }

    @Bean
    public JpaItemWriter<Notification> notificationWriter() {
        return new JpaItemWriterBuilder<Notification>()
                .usePersist(false)
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
