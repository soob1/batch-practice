package com.soob1.batch.notify.domain;


import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;
}
