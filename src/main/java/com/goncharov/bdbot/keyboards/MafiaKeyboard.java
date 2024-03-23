package com.goncharov.bdbot.keyboards;

import com.goncharov.bdbot.repositories.PlayerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
@Component
@RequiredArgsConstructor
public class MafiaKeyboard {

    private final PlayerRepo playerRepo;

    public InlineKeyboardMarkup basicKeyboard(){
        // Создаем кнопки
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // Первая кнопка
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        var button1 = new InlineKeyboardButton();
        button1.setText("Моя роль");
        button1.setCallbackData("role");
        rowInline1.add(button1);
        rowsInline.add(rowInline1);

        // Вторая кнопка
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        var button2 = new InlineKeyboardButton();
        button2.setText("Жертвы");
        button2.setCallbackData("victims");
        rowInline2.add(button2);
        rowsInline.add(rowInline2);

        // Третья кнопка
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        var button3 = new InlineKeyboardButton();
        button3.setText("Убить...");
        button3.setCallbackData("kill");
        rowInline3.add(button3);
        rowsInline.add(rowInline3);

        // Третья кнопка
        List<InlineKeyboardButton> rowInline4 = new ArrayList<>();
        var button4 = new InlineKeyboardButton();
        button4.setText("Меня раскрыли");
        button4.setCallbackData("caught");
        rowInline4.add(button4);
        rowsInline.add(rowInline4);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    public InlineKeyboardMarkup beingCaughtKeyboard() {

        // Создаем кнопки
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // Первая кнопка
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        var button1 = new InlineKeyboardButton();
        button1.setText("Да");
        button1.setCallbackData("yes");
        rowInline1.add(button1);
        rowsInline.add(rowInline1);

        // Вторая кнопка
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        var button2 = new InlineKeyboardButton();
        button2.setText("Я просто нажал кнопочку...");
        button2.setCallbackData("no");
        rowInline2.add(button2);
        rowsInline.add(rowInline2);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
    public InlineKeyboardMarkup toKillKeyboard(String username) throws RuntimeException {

        var victims = playerRepo.findVictimsByUsername(username);
        // Создаем кнопки
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (int i = 0; i < victims.size(); i++){
        // Кнопка с юзернеймом
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var button = new InlineKeyboardButton();
        button.setText(victims.get(i));
        button.setCallbackData("username:" + victims.get(i));
        rowInline.add(button);
        rowsInline.add(rowInline);
    }
        markupInline.setKeyboard(rowsInline);
        //todo доделать что при убийстве юзера ему плохо
        //todo добавить выдачу юзеров другим мафиям при выбывании мафии
        //todo сделать кнопки всем остальным
        return markupInline;
    }
}
