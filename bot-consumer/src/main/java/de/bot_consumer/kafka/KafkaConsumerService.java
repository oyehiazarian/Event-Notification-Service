package de.bot_consumer.kafka;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.bot_consumer.bot.EventBot;

@Service
public class KafkaConsumerService implements KafkaConsumerServiceInterface {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final Map<String, Long> activeUsers = new ConcurrentHashMap<>();

    @Autowired
    private final EventBot eventBot;

    public KafkaConsumerService(EventBot eventBot) {
        this.eventBot = eventBot;

    }

    @KafkaListener(topics = "events", groupId = "foo")
    public void listen(ConsumerRecord<String, String> record) throws JsonProcessingException {
        String key = record.key();
        String value = convertEventToString(record.value());

        if (activeUsers.containsKey(key)) {
            Long chatId = activeUsers.get(key);
            LOG.info("Sending event to user [{}]: {}", key, value);
            eventBot.sendMessage(chatId, "New event:\n" +
                    value);
        }
    }

    @Override
    public void registerUser(String username, Long chatId) {
        activeUsers.put(username, chatId);
        LOG.info("User [{}] registered with chatId [{}]", username, chatId);
    }

    public String convertEventToString(String value) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        JsonNode node = mapper.readTree(value);
        JsonNode dueDateNode = node.get("due_date");

        int year = dueDateNode.get(0).asInt();
        int month = dueDateNode.get(1).asInt();
        int day = dueDateNode.get(2).asInt();
        int hour = dueDateNode.get(3).asInt();
        int minute = dueDateNode.get(4).asInt();

        String dueDate = String.format("%04d-%02d-%02d %02d:%02d", year, month, day, hour, minute);

        String formattedQuote = """
                Title: %s
                Topic: %s
                Content: %s
                Due Date: %s
                """.formatted(node.get("title").asText(), node.get("topic").asText(), node.get("content").asText(), dueDate);
        return formattedQuote;
    }
}
