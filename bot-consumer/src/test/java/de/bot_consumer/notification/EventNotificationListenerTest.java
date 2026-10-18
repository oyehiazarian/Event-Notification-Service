package de.bot_consumer.notification;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.bot_consumer.bot.EventBot;
import de.bot_consumer.subscription.SubscriptionService;

class EventNotificationListenerTest {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final EventBot bot = mock(EventBot.class);
    private final SubscriptionService subscriptions = mock(SubscriptionService.class);
    private final EventNotificationListener listener = new EventNotificationListener(bot, subscriptions, mapper);

    private String payload() throws JsonProcessingException {
        return mapper.writeValueAsString(new EventMessage(1L, "Meeting", null, "Bring laptop",
                LocalDateTime.of(2030, 5, 17, 9, 30), "alice"));
    }

    @Test
    void sendsFormattedMessageToSubscribedUser() throws Exception {
        when(subscriptions.findChatId("alice")).thenReturn(Optional.of(42L));

        listener.listen(new ConsumerRecord<>("events", 0, 0L, "alice", payload()));

        verify(bot).sendMessage(org.mockito.ArgumentMatchers.eq(42L), contains("Due Date: 2030-05-17 09:30"));
    }

    @Test
    void handlesMissingOptionalFields() {
        String text = EventNotificationListener.format(new EventMessage(1L, "T", null, null, null, "alice"));

        assertTrue(text.contains("Topic: -"));
        assertTrue(text.contains("Due Date: -"));
    }

    @Test
    void ignoresUsersWithoutSubscription() throws Exception {
        when(subscriptions.findChatId("bob")).thenReturn(Optional.empty());

        listener.listen(new ConsumerRecord<>("events", 0, 0L, "bob", payload()));

        verify(bot, never()).sendMessage(anyLong(), anyString());
    }
}
