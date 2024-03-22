package com.goncharov.bdbot.config;


import com.goncharov.bdbot.keyboards.MafiaKeyboard;
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
        if (request.hasMessage() && requestMessage.hasText()) {
            response.setChatId(requestMessage.getChatId().toString());
            try {
                if (requestMessage.getText().equals("/start")) {
                    defaultMsg(response, "Напиши номер с карточки");
                } else if (requestMessage.getText().equals("/game")) {
                     User user = requestMessage.getFrom();
                    System.out.println(user.getUserName());
                    response.setReplyMarkup(gameService.getButtons(user.getUserName()));
                    defaultMsg(response, gameService.getInfo(user.getUserName()));
                } else {
                    User user = requestMessage.getFrom();
                    var responseString = gameService
                            .addUsername(requestMessage.getText(), user.getUserName());
                    response.setReplyMarkup(MafiaKeyboard.mafiaKeyboard());
                    defaultMsg(response, responseString);
                }
            } catch (RuntimeException e) {
                defaultMsg(response, e.getMessage());
            }
        } else if (request.hasCallbackQuery()) {
            var callbackQuery = request.getCallbackQuery();
            response.setChatId(callbackQuery.getMessage().getChatId());
            String call_data = request.getCallbackQuery().getData();
            if (call_data.equals("role")) {
                //todo в юзер записывается сам бот, падает исключение
                User user = callbackQuery.getFrom();
                System.out.println(user.getUserName());
                response.setReplyMarkup(gameService.getButtons(user.getUserName()));
                defaultMsg(response, gameService.fromRoleToString(user.getUserName()));
            } else if (call_data.equals("victims")) {
                User user = callbackQuery.getFrom();
                System.out.println(user.getUserName());
                response.setReplyMarkup(gameService.getButtons(user.getUserName()));
                defaultMsg(response, gameService.getInfo(user.getUserName()));
            }
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

