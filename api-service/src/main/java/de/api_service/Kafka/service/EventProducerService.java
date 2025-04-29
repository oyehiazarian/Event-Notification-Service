package de.api_service.Kafka.service;

import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.api_service.model.Events;
import de.api_service.repository.EventsRepository;

@Service
public class EventProducerService {

    @Autowired
    private EventsRepository eventsRepository;


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "events";

    private Long lastId = 0L;

    @Transactional
    @Scheduled(fixedRate = 5000)
    public void sendNewEvents() throws JsonProcessingException {
        List<Events> events = eventsRepository.findByIdGreaterThan(lastId);


        for(Events event:events){
            Hibernate.initialize(event.getUserId());
            String json = convertEventToJson(event);
            kafkaTemplate.send(TOPIC, event.getId().toString(), json);
        }

        if (!events.isEmpty()) {
            lastId = events.get(events.size() - 1).getId();
        }
    }



    public String convertEventToJson(Events event) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String json = mapper.writeValueAsString(event);
        return json;

    }

}
