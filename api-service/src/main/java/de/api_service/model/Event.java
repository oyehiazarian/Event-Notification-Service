package de.api_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "topic")
    private String topic;

    @Column(name = "content")
    private String content;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    /** Recipient of the notification. */
    @Column(name = "user_id")
    private Long userId;

    /** False until the event has been published to Kafka (transactional outbox). */
    @Column(name = "sent", nullable = false)
    private boolean sent;
}
