package com.goncharov.bdbot.keyboards;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
@Component
public class CopKeyboard {
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
        button2.setText("Список заданий");
        button2.setCallbackData("tasksc");
        rowInline2.add(button2);
        rowsInline.add(rowInline2);


        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}
