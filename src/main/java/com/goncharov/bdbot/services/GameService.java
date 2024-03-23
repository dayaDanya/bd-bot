package com.goncharov.bdbot.services;


import com.goncharov.bdbot.exceptions.*;
import com.goncharov.bdbot.keyboards.CitizenKeyboard;
import com.goncharov.bdbot.keyboards.CopKeyboard;
import com.goncharov.bdbot.keyboards.MafiaKeyboard;
import com.goncharov.bdbot.models.Role;
import com.goncharov.bdbot.repositories.PlayerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class GameService {
    private static String forMafia = "Твоя роль: Мафия\nТвоя задача убить жителей и забрать их карточку, только делай это скрытно иначе тебя может посадить комиссар.\n";

    private static String forCitizen = "Твоя роль: Мирный житель\nТы чисто бегунок. Делай задания и отмечай в таблице суетись и тд и тп, я проверю потом." +
            " Не попадись мафии и комиссару. Первый убьет тебя, а второй откатит задание, так что все нужно делать скрытно.\n";

    private static String forCop = "Твоя роль: Комисcар\nУ тебя две задачи:\n" +
            "1. Ловить мафию. Сделать это несложно, нужно быть внимательным " +
            "и увидеть как убитый житель отдает свою карточку мафии. " +
            "В этот момент ты можешь вывести мафию из игры.\n" +
            "2. Мешать жителям выполнять задания, чтобы успеть посадить всю мафию прежде чем мирные жители выполнят все задания.\n";

    private static String tasks = "Google-таблица: \nhttps://clck.ru/39chWA";
    private final PlayerRepo playerRepo;

    private final MafiaKeyboard mafiaKeyboard;

    private final CitizenKeyboard citizenKeyboard;

    private final CopKeyboard copKeyboard;

    public InlineKeyboardMarkup getButtons(String username) {
        var cur = playerRepo.findByUsername(username).orElseThrow(UsernameNotFoundException::new);
        if (cur.getRole() == Role.COP) {
            return copKeyboard.basicKeyboard();
        } else if (cur.getRole() == Role.MAFIA) {
            return mafiaKeyboard.basicKeyboard();
        } else if (cur.getRole() == Role.CITIZEN) {
            return citizenKeyboard.basicKeyboard();
        }
        return null;
    }

    public String getInfo(String username) {
        var cur = playerRepo.findByUsername(username).orElseThrow(UsernameNotFoundException::new);
        if (cur.getRole() == Role.COP) {
            return "Задания горожан: ";
        } else if (cur.getRole() == Role.MAFIA) {
            return getForMafia(username);
        } else if (cur.getRole() == Role.CITIZEN) {
            return tasks;
        }
        return "хм";
    }


    public String addUsername(String message, String username) {
        int id;
        try {
            id = Integer.parseInt(message);
        } catch (NumberFormatException e) {
            throw new WrongMessageException(message);
        }
        var playerList = playerRepo.findAll();
        try {
            var player = playerList.get(id);
            if (player == null) {
                throw new WrongIdException(id);
            } else if (player.getUsername() != null) {
                throw new IdAlreadyUsedException(id);
            } else if (playerRepo.findByUsername(username).isPresent())
                throw new UsernameAlreadyInUseException();
            else {
                playerRepo.addPlayerUsername(id, username);
                return fromRoleToString(username);
            }
        } catch (IndexOutOfBoundsException e) {
            throw new WrongIdException(id);
        }
    }

    public String getForMafia(String username) {
        var curMafia = playerRepo.findByUsername(username);
        var victims = playerRepo.getVictims(curMafia.get().getId());
        if (victims.isEmpty()) {
            return "Убивать некого...";
        }
        var vicStringBuilder = new StringBuilder();
        victims.forEach(s -> vicStringBuilder.append(s).append("\n"));
        return "Твой список целей на сегодня: " + vicStringBuilder.toString();
    }

    public void killCitizen(String mafiaUsername, String username) {
        var playerToKill = playerRepo.findByUsername(username).get();
        var mafia = playerRepo.findByUsername(mafiaUsername);
        playerRepo.deleteFromPlayers(playerToKill.getId());
        playerRepo.deleteFromCitizens(playerToKill.getUsername());
        playerRepo.deleteFromVictims(mafia.get().getId(), playerToKill.getUsername());
    }

    public void catchMafia(String username){
        var mafia = playerRepo.findByUsername(username);
        playerRepo.transferVictims(mafia.get().getId());
        playerRepo.deleteFromPlayers(mafia.get().getId());
    }


    public String fromRoleToString(String username) {
        var role = playerRepo.findByUsername(username).get().getRole();
        if (role == Role.COP) {
            return forCop;
        }
        if (role == Role.MAFIA) {
            return forMafia;
        }
        if (role == Role.CITIZEN) {
            return forCitizen;
        }
        return "кто ты?";
    }

}
