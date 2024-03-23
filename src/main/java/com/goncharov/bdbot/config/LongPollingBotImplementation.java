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
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

@Getter
@Component
public class LongPollingBotImplementation extends TelegramLongPollingBot {
    //todo зарефакторить конструктор и инициализацию, а так же ломбок
    private Message requestMessage = new Message();
    private final SendMessage response = new SendMessage();

    private final String botUsername;
    private final String botToken;

    private final GameService gameService;

    private final MafiaKeyboard mafiaKeyboard;

    private final SendPhoto sendPhotoRequest = new SendPhoto();

    private InputFile photo = new InputFile(new File("C:\\Users\\danil\\Desktop\\boot projects\\bd-bot\\src\\main\\java\\com\\goncharov\\bdbot\\imagejpg.jpg"));

    public LongPollingBotImplementation(
            TelegramBotsApi telegramBotsApi,
            @Value("${telegram-bot.name}") String botUsername,
            @Value("${telegram-bot.token}") String botToken, GameService gameService, MafiaKeyboard mafiaKeyboard) throws TelegramApiException {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.gameService = gameService;
        this.mafiaKeyboard = mafiaKeyboard;

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
                    response.setReplyMarkup(gameService.getButtons(user.getUserName()));
                    defaultMsg(response, gameService.getInfo(user.getUserName()));
                } else {
                    User user = requestMessage.getFrom();
                    var responseString = gameService
                            .addUsername(requestMessage.getText(), user.getUserName());
                    response.setReplyMarkup(gameService.getButtons(user.getUserName()));
                    defaultMsg(response, responseString);
                }
            } catch (RuntimeException e) {
                User user = requestMessage.getFrom();
                defaultMsg(response, e.getMessage());
                response.setReplyMarkup(gameService.getButtons(user.getUserName()));
            }
        } else if (request.hasCallbackQuery()) {
            var callbackQuery = request.getCallbackQuery();
            response.setChatId(callbackQuery.getMessage().getChatId());
            String call_data = callbackQuery.getData();
            try {
                if (call_data.equals("role")) {
                    User user = callbackQuery.getFrom();
                    response.setReplyMarkup(gameService.getButtons(user.getUserName()));
                    defaultMsg(response, gameService.fromRoleToString(user.getUserName()));
                }
                else if (call_data.equals("tasksc")) {
                    User user = callbackQuery.getFrom();
                    SendPhoto sendPhotoRequest = new SendPhoto();
                    sendPhotoRequest.setChatId(callbackQuery.getMessage().getChatId());
                    response.setReplyMarkup(gameService.getButtons(user.getUserName()));
                    sendPhotoRequest.setPhoto(photo);
                    execute(sendPhotoRequest);
                }
                else if (call_data.equals("tasks")) {
                    User user = callbackQuery.getFrom();
                    SendPhoto sendPhotoRequest = new SendPhoto();
                    sendPhotoRequest.setChatId(callbackQuery.getMessage().getChatId());
                    response.setReplyMarkup(gameService.getButtons(user.getUserName()));
                    sendPhotoRequest.setPhoto(photo);
                    execute(sendPhotoRequest);
                    defaultMsg(response, gameService.getInfo(user.getUserName()));
                } else if (call_data.equals("victims")) {
                    User user = callbackQuery.getFrom();
                    response.setReplyMarkup(gameService.getButtons(user.getUserName()));
                    defaultMsg(response, gameService.getInfo(user.getUserName()));
                } else if (call_data.equals("kill")) {
                    User user = callbackQuery.getFrom();
                    response.setReplyMarkup(mafiaKeyboard.toKillKeyboard(user.getUserName()));
                    defaultMsg(response, "Кого ты убил?");
                } else if (call_data.contains("username")) {
                    User user = callbackQuery.getFrom();
                    var usernameToKill = call_data.substring(9);
                    gameService.killCitizen(user.getUserName(), usernameToKill);
                    response.setReplyMarkup(mafiaKeyboard.basicKeyboard());
                    defaultMsg(response, "Минус один...");
                } else if (call_data.equals("caught")) {
                    response.setReplyMarkup(mafiaKeyboard.beingCaughtKeyboard());
                    defaultMsg(response, "Тебя точно поймали?");
                } else if (call_data.equals("no")) {
                    response.setReplyMarkup(mafiaKeyboard.basicKeyboard());
                    defaultMsg(response, "Ну ладно))\nПродолжай играть");
                } else if (call_data.equals("yes")) {
                    User user = callbackQuery.getFrom();
                    gameService.catchMafia(user.getUserName());
                    defaultMsg(response, "К сожалению для тебя игра окончена ;(");
                }
            } catch (RuntimeException e) {
                defaultMsg(response, e.getMessage());
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

