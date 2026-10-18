package de.bot_consumer.subscription;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionService {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionService.class);

    private final TelegramSubscriptionRepository repository;

    public SubscriptionService(TelegramSubscriptionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void subscribe(String username, Long chatId) {
        String key = normalize(username);
        repository.save(new TelegramSubscription(key, chatId));
        LOG.info("User [{}] subscribed with chatId [{}]", key, chatId);
    }

    @Transactional
    public void unsubscribe(Long chatId) {
        repository.deleteByChatId(chatId);
    }

    @Transactional(readOnly = true)
    public Optional<Long> findChatId(String username) {
        return repository.findById(normalize(username)).map(TelegramSubscription::getChatId);
    }

    public static String normalize(String username) {
        String value = username.trim();
        if (value.startsWith("@")) {
            value = value.substring(1);
        }
        return value.toLowerCase();
    }
}
