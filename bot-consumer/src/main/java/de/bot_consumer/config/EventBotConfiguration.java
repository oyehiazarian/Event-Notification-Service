package de.bot_consumer.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import de.bot_consumer.bot.EventBot;

@Configuration
@EnableCaching
@EnableScheduling
public class EventBotConfiguration {

    @Bean
    public TelegramBotsApi telegramBotsApi(EventBot eventBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(eventBot);
        return api;
    }
}
