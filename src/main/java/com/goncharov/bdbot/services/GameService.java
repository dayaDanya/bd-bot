package com.goncharov.bdbot.services;


import com.goncharov.bdbot.exceptions.*;
import com.goncharov.bdbot.models.Player;
import com.goncharov.bdbot.models.Role;
import com.goncharov.bdbot.repositories.PlayerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {
    private static String forMafia = "Твоя роль: Мафия\nТвоя задача убить жителей и забрать их карточку, только делай это скрытно иначе тебя может посадить комиссар.\n" +
            "Специально для тебя: можешь узнать свои цели на сегодняшний вечер по команде /game";

    private static String forCitizen = "Твоя роль: Мирный житель\nТы чисто бегунок. Делай задания и отмечай в таблице суетись и тд и тп, я проверю потом." +
            " Не попадись мафии и комиссару. Первый убьет тебя, а второй откатит задание, так что все нужно делать скрытно.\n" +
            "Кстати, список заданий мирных жителей можешь получить по команде /game";

    private static String forCop = "Твоя роль: Комисcар\nУ тебя две задачи:\n" +
            "1. Ловить мафию. Сделать это несложно, нужно быть внимательным " +
            "и увидеть как убитый житель отдает свою карточку мафии. " +
            "В этот момент ты можешь вывести мафию из игры.\n" +
            "2. Мешать жителям выполнять задания, чтобы успеть посадить всю мафию прежде чем мирные жители выполнят все задания.\n" +
            "Кстати, список заданий мирных жителей можешь получить по команде /game";

    private static String tasks = "Список заданий:\n" +
            "1. На каждом бокале завязана черная ленточка, найди за стойкой бара розовую ленточку и завяжи ее на ножку одного любого бокала вместо черной\n" +
            "2. Поменяй у двух игроков, вне зависимости от роли, любой элемент одежды (аксессуары тоже считаются)\n" +
            "3. Нарисуй черным маркером сердечко на ножке деревянного мольберта\n" +
            "4. Надень на кого-нибудь коричневый браслет из маленьких камушков, но прежде отыщи его в зоне гардероба (на плечиках)\n" +
            "5. На фотографии полароид с именинницей поставь ей рожки незаметно от всех и вклей такую фотографию в блокнот, оформив полноценно страничку\n" +
            "6. Нарисуй на канцелярском стикере смайлик и приклей его на экран ноута\n" +
            "7. Повесь скрепку, которая лежит на столе с блокнотом, на пиджак любому мальчику\n" +
            "8. Поменяй местами крышки у фанты и колы\n" +
            "9. Поставь 3 шпажки в один сэндвич/бургер\n" +
            "10. Сделай бумажный самолетик и оставь его на видном месте на столе второго этажа\n" +
            "11. Наклей стикер с обезьянкой на обратную сторону белого холста\n" +
            "12. Поставь в очередь реальный, существующий трек, в котором встречается имя именинницы (любой, который знаешь или какой сможешь найти)\n" +
            "13. Повесь розовую ленточку на кран в баре \n" +
            "14. Тебе нужно вырезать из кусочка пиццы сердечко и положить его обратно в коробку\n" +
            "15. Напиши на салфетке имя именинницы и незаметно положи в карман другому игроку\n" +
            "16. Напиши (придумай) милое пожелание имениннице в стихотворной форме на пустой страничке в блокноте\n" +
            "17. Развяжи незаметно/ «случайно» шнурки любому игроку, вне зависимости от роли\n" +
            "18. Возьми на столе с блокнотом стикер (наклейку) и положи его любому игроку под прозрачный чехол телефона\n" +
            "19. Завяжи розовую ленточку на микрофон\n" +
            "20. Нарисуй именинницу на внутренней части крышки коробки из-под пиццы\n" +
            "21. Напиши на стикере твою историю знакомства с именинницей и приклей его на зеркало с лампочками\n" +
            "22. Нарисуй на стикере знак зодиака именинницы  и приклей на стенку настольного футбола\n" +
            "23. Найди чистый холст в комнате с настольным футболом и старательно нарисуй полноценный красивый портрет себя с именинницей \n" +
            "24. Переименуй плейлист тусовки на ноутбуке";
    private final PlayerRepo playerRepo;


    public String getInfo(String username) {
        var cur = playerRepo.findByUsername(username).orElseThrow(UsernameNotFoundException::new);
        if (cur.getRole() == Role.COP) {
            return tasks;
        } else if (cur.getRole() == Role.MAFIA) {
            return forMafia;
        } else if (cur.getRole() == Role.CITIZEN) {
            return tasks;
        }
        return "хм";
    }

    public String addUsername(String message, String username) {
        int id;
        try {
             id = Integer.parseInt(message);
        } catch (NumberFormatException e){
            throw new WrongMessageException(message);
        }
        var playerList = playerRepo.findAll();
        try {
            var player = playerList.get(id);
            if (player.getUsername() != null) {
                throw new IdAlreadyUsedException(id);
            } else if(playerRepo.findByUsername(username).isPresent())
                throw new UsernameAlreadyInUseException();
            else {
                playerRepo.addPlayerUsername(id, username);
                return fromRoleToString(playerRepo.findById(id).getRole());
            }
        } catch (IndexOutOfBoundsException e) {
            throw new WrongIdException(id);
        }
    }

    public String fromRoleToString(Role role) {
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
