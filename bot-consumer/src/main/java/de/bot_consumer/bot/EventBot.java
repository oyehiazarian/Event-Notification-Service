package de.bot_consumer.bot;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import de.bot_consumer.kafka.KafkaConsumerService;

@Component
public class EventBot extends TelegramLongPollingBot {

    private static final Logger LOG = LoggerFactory.getLogger(EventBot.class);

    private static final String START = "/start";

    @Autowired
    @Lazy
    private KafkaConsumerService kafkaConsumerService;

    private final Map<Long, String> waitingForInput = new HashMap<>();

    public EventBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();

        if (waitingForInput.containsKey(chatId)) {
            var command = waitingForInput.remove(chatId);
            handleUserInput(chatId, command, message);
            return;
        }

        switch (message) {
            case START -> {
                String userName = update.getMessage().getChat().getUserName();
                startCommand(chatId, userName);
                waitingForInput.put(chatId, "START");

            }

            default -> unknownCommand(chatId);

        }
    }

    private void handleUserInput(Long chatId, String command, String userInput) {
        switch (command) {
            case "START":
                sendMessage(chatId, "Thank you! Now listening to your events.");
                kafkaConsumerService.registerUser(userInput, chatId);
        }
    }

    private void startCommand(Long chatId, String userName) {
        var text = """
                Hello and Welcome to our bot,
                to continue enter your username 
                
                Then you will subscribe to your events list
                
                Best Regards from Team Sanya 
                """;
        var formattedText = String.format(text, userName);
        sendMessage(chatId, formattedText);
    }

    public void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Error with sending message" + e);
        }
    }

    private void unknownCommand(Long chatId) {
        var text = "Не удалось распознать команду!";
        sendMessage(chatId, text);
    }

    @Override
    public String getBotUsername() {
        return "event001_bot";
    }

}
