package de.bot_consumer.subscription;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Maps a (lower-cased) username to the Telegram chat that should receive their notifications. */
@Entity
@Table(name = "telegram_subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TelegramSubscription {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;
}
