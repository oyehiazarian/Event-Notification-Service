package de.bot_consumer.kafka;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService implements KafkaConsumerServiceInterface {

    public static String[] adminMessages;

    private static String usernameForKafka;


    @KafkaListener(topics = "events", groupId = "foo")
    public void listen(ConsumerRecord<String, String> record) {

            if (usernameForKafka.equals(record.key())) {
                String message = record.value();
                System.out.println("Received message with key 'admin': " + message);

                if (message != null && !message.isEmpty()) {

                    adminMessages[0] = message;
                }
            }
    }

    @Override
    public String getAllAdminMessages() {
        if (!adminMessages.isEmpty()) {
            return String.join("\n", adminMessages);  // Разделяем сообщения новой строкой
        } else {
            return "";  // Возвращаем пустую строку, если нет сообщений
        }
    }

    @Override
    public void getCurrentUsername(String username){
        usernameForKafka = username;

    }
}
