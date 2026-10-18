package de.api_service.outbox;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.api_service.dto.EventMessage;
import de.api_service.model.Event;
import de.api_service.repository.EventsRepository;
import de.api_service.repository.UserRepository;

/**
 * Transactional outbox: events are stored with {@code sent = false} and published here.
 * Because the flag lives in the database, a restart neither loses nor re-sends events.
 */
@Service
public class EventOutboxPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(EventOutboxPublisher.class);

    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public EventOutboxPublisher(EventsRepository eventsRepository, UserRepository userRepository,
            KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
            @Value("${app.kafka.topic}") String topic) {
        this.eventsRepository = eventsRepository;
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    @Scheduled(fixedDelayString = "${app.outbox.interval-ms:2000}")
    public void publishPending() {
        List<Event> pending = eventsRepository.findTop100BySentFalseOrderByIdAsc();

        for (Event event : pending) {
            if (!publish(event)) {
                return; // keep ordering; retry on the next tick
            }
        }
    }

    private boolean publish(Event event) {
        String username = event.getUserId() == null ? null
                : userRepository.findById(event.getUserId()).map(u -> u.getUsername()).orElse(null);

        if (username != null) {
            try {
                String json = objectMapper.writeValueAsString(new EventMessage(event.getId(), event.getTitle(),
                        event.getTopic(), event.getContent(), event.getDueDate(), username));
                kafkaTemplate.send(topic, username, json).get(10, TimeUnit.SECONDS);
            } catch (JsonProcessingException e) {
                LOG.error("Cannot serialize event {}, skipping it", event.getId(), e);
            } catch (ExecutionException | TimeoutException e) {
                LOG.warn("Kafka unavailable, event {} will be retried: {}", event.getId(), e.getMessage());
                return false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        } else {
            LOG.warn("Event {} has no recipient anymore, marking as sent", event.getId());
        }

        event.setSent(true);
        eventsRepository.save(event);
        return true;
    }
}
