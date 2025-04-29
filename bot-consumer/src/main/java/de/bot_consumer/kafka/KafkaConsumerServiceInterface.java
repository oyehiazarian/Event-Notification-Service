package de.bot_consumer.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface KafkaConsumerServiceInterface {
    void listen(ConsumerRecord<String, String> record);

    String getAllAdminMessages();

    void getCurrentUsername(String username);
}
