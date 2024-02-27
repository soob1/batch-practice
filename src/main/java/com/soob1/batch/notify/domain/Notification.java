package com.soob1.batch.notify.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notification")
@DynamicUpdate
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "sent_datetime", nullable = false)
    private LocalDateTime sentDateTime;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDateTime;

    public void sendSuccess() {
        this.status = Status.SENT;
        this.sentDateTime = LocalDateTime.now();
    }

    public void sendFail() {
        this.status = Status.FAILED;
    }
}
