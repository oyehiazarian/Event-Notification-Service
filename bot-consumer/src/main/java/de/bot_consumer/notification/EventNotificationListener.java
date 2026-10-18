package de.bot_consumer.notification;

import java.time.format.DateTimeFormatter;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.bot_consumer.bot.EventBot;
import de.bot_consumer.subscription.SubscriptionService;

@Service
public class EventNotificationListener {

    private static final Logger LOG = LoggerFactory.getLogger(EventNotificationListener.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final EventBot eventBot;
    private final SubscriptionService subscriptions;
    private final ObjectMapper objectMapper;

    public EventNotificationListener(EventBot eventBot, SubscriptionService subscriptions, ObjectMapper objectMapper) {
        this.eventBot = eventBot;
        this.subscriptions = subscriptions;
        this.objectMapper = objectMapper;
    }

    /**
     * A malformed payload throws and, after the retries configured in {@code KafkaErrorHandlingConfig},
     * ends up on the dead-letter topic instead of blocking the partition.
     */
    @KafkaListener(topics = "${app.kafka.topic}")
    public void listen(ConsumerRecord<String, String> record) throws JsonProcessingException {
        EventMessage event = objectMapper.readValue(record.value(), EventMessage.class);

        subscriptions.findChatId(record.key()).ifPresentOrElse(chatId -> {
            LOG.info("Sending event {} to user [{}]", event.id(), record.key());
            eventBot.sendMessage(chatId, "New event:\n" + format(event));
        }, () -> LOG.debug("User [{}] is not subscribed, skipping event {}", record.key(), event.id()));
    }

    static String format(EventMessage event) {
        return """
                Title: %s
                Topic: %s
                Content: %s
                Due Date: %s
                """.formatted(
                valueOrDash(event.title()),
                valueOrDash(event.topic()),
                valueOrDash(event.content()),
                event.dueDate() == null ? "-" : event.dueDate().format(DATE_FORMAT));
    }

    private static String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
