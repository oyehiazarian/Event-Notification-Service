package de.bot_consumer.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface KafkaConsumerServiceInterface {
    void listen(ConsumerRecord<String, String> record) throws JsonProcessingException;

    void registerUser(String username, Long chatId);

}
