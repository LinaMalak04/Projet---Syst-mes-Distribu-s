package org.example.chatbotservice.telegram;

import org.example.chatbotservice.agents.BankAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@ConditionalOnExpression("!'${telegram.bot.token:}'.isEmpty()")
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private static final Logger log = LoggerFactory.getLogger(TelegramBot.class);
    private static final int MAX_MESSAGE_LENGTH = 4096;

    private final String botToken;
    private final TelegramClient telegramClient;
    private final BankAgent bankAgent;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken, BankAgent bankAgent) {
        this.botToken = botToken;
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.bankAgent = bankAgent;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        String answer;
        if (text.equals("/start")) {
            answer = "Bonjour, je suis l'assistant E-Bank. Posez-moi une question sur les clients, "
                    + "les comptes ou les opérations, par exemple : quels sont les comptes de Lina ?";
        } else {
            sendTyping(chatId);
            try {
                // One conversation memory per Telegram chat
                answer = bankAgent.ask("telegram-" + chatId, text);
            } catch (Exception e) {
                log.error("Agent error", e);
                answer = "Désolé, une erreur est survenue. Réessayez dans un instant.";
            }
        }
        send(chatId, answer);
    }

    private void sendTyping(long chatId) {
        try {
            telegramClient.execute(SendChatAction.builder().chatId(chatId).action(ActionType.TYPING.toString()).build());
        } catch (TelegramApiException e) {
            log.warn("Cannot send the typing action", e);
        }
    }

    private void send(long chatId, String text) {
        if (text == null || text.isBlank()) text = "Je n'ai pas de réponse.";
        if (text.length() > MAX_MESSAGE_LENGTH) text = text.substring(0, MAX_MESSAGE_LENGTH);
        try {
            telegramClient.execute(SendMessage.builder().chatId(chatId).text(text).build());
        } catch (TelegramApiException e) {
            log.error("Cannot send the message", e);
        }
    }
}
