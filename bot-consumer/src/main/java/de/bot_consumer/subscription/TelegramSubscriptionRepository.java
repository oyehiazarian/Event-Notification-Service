package de.bot_consumer.subscription;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramSubscriptionRepository extends JpaRepository<TelegramSubscription, String> {

    void deleteByChatId(Long chatId);
}
