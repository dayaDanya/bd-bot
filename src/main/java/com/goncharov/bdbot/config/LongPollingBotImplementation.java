package com.goncharov.bdbot.config;


import com.goncharov.bdbot.exceptions.IdAlreadyUsedException;
import com.goncharov.bdbot.exceptions.WrongIdException;
import com.goncharov.bdbot.services.GameService;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Getter
@Component
public class LongPollingBotImplementation extends TelegramLongPollingBot {
    //todo зарефакторить конструктор и инициализацию, а так же ломбок
    private Message requestMessage = new Message();
    private final SendMessage response = new SendMessage();

    private final String botUsername;
    private final String botToken;

    private final GameService gameService;

    public LongPollingBotImplementation(
            TelegramBotsApi telegramBotsApi,
            @Value("${telegram-bot.name}") String botUsername,
            @Value("${telegram-bot.token}") String botToken, GameService gameService) throws TelegramApiException {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.gameService = gameService;

        telegramBotsApi.registerBot(this);
    }

    /**
     * Этот метод вызывается при получении обновлений через метод GetUpdates.
     *
     * @param request Получено обновление
     */
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update request) {
        requestMessage = request.getMessage();
        response.setChatId(requestMessage.getChatId().toString());

        if (request.hasMessage() && requestMessage.hasText())
            try {
                if (requestMessage.getText().equals("/start"))
                    defaultMsg(response, "Напиши номер с карточки");
                else {
                    User user = requestMessage.getFrom();
                    var role = gameService.addUsername(Integer.parseInt(requestMessage.getText()), user.getUserName());
                    defaultMsg(response, role.toString());
                }
            } catch (RuntimeException e) {
                defaultMsg(response, e.getMessage());
            }
    }


    /**
     * Шаблонный метод отправки сообщения пользователю
     *
     * @param response - метод обработки сообщения
     * @param msg      - сообщение
     */
    private void defaultMsg(SendMessage response, String msg) throws TelegramApiException {
        response.setText(msg);
        execute(response);
    }


}

