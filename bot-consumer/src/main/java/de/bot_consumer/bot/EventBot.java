package de.bot_consumer.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import de.bot_consumer.subscription.SubscriptionService;

@Component
public class EventBot extends TelegramLongPollingBot {

    private static final Logger LOG = LoggerFactory.getLogger(EventBot.class);

    private static final String START = "/start";
    private static final String STOP = "/stop";

    private final SubscriptionService subscriptions;
    private final String botUsername;

    public EventBot(@Value("${bot.token}") String botToken,
            @Value("${bot.username}") String botUsername,
            SubscriptionService subscriptions) {
        super(botToken);
        this.botUsername = botUsername;
        this.subscriptions = subscriptions;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        var message = update.getMessage().getText().trim();
        var chatId = update.getMessage().getChatId();

        switch (message) {
            case START -> start(chatId, update.getMessage().getChat().getUserName());
            case STOP -> stop(chatId);
            default -> sendMessage(chatId, "Unknown command. Use /start to subscribe or /stop to unsubscribe.");
        }
    }

    /**
     * The recipient is identified by the Telegram username of the chat itself and not by free text,
     * so nobody can subscribe to somebody else's events by typing a foreign name.
     */
    private void start(Long chatId, String telegramUsername) {
        if (telegramUsername == null || telegramUsername.isBlank()) {
            sendMessage(chatId, """
                    You have no Telegram username yet.
                    Set one in Telegram settings (Settings > Username) and send /start again.""");
            return;
        }
        subscriptions.subscribe(telegramUsername, chatId);
        sendMessage(chatId, """
                Hello @%s! You are now subscribed to your events.

                Make sure your username in the web app is the same as your Telegram username.
                Send /stop to unsubscribe.""".formatted(telegramUsername));
    }

    private void stop(Long chatId) {
        subscriptions.unsubscribe(chatId);
        sendMessage(chatId, "You are unsubscribed. Send /start to subscribe again.");
    }

    public void sendMessage(Long chatId, String text) {
        var sendMessage = new SendMessage(String.valueOf(chatId), text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Error with sending message to chat {}", chatId, e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
