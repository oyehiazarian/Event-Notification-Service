package de.bot_consumer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/** Consumer settings live in application.properties; this adds retries and a dead-letter topic. */
@Configuration
public class KafkaErrorHandlingConfig {

    @Bean
    public CommonErrorHandler kafkaErrorHandler(KafkaTemplate<Object, Object> template,
            @Value("${app.kafka.topic}") String topic) {
        // Always use partition 0 of the DLT, it is created with a single partition below.
        var recoverer = new DeadLetterPublishingRecoverer(template,
                (record, ex) -> new org.apache.kafka.common.TopicPartition(topic + ".DLT", 0));
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2));
    }

    @Bean
    public NewTopic eventsTopic(@Value("${app.kafka.topic}") String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic eventsDeadLetterTopic(@Value("${app.kafka.topic}") String topic) {
        return TopicBuilder.name(topic + ".DLT").partitions(1).replicas(1).build();
    }
}
