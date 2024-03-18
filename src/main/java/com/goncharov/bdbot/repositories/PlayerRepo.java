package com.goncharov.bdbot.repositories;

import com.goncharov.bdbot.models.Player;
import com.goncharov.bdbot.models.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlayerRepo {
    @Autowired
    private List<Player> playerList;

    public List<Player> findAll(){
        return playerList;
    }

    public Player findById(int id) {
        return playerList.get(id);
    }


    public void addPlayerUsername(int id, String username){
        playerList.get(id).setUsername(username);
        System.out.println(playerList.get(id));
    }

    @PostConstruct
    private void initializeList() {
        int id = 1;
        for (int i = 0; i < 4; i++) {
            playerList.add(
                    Player.builder()
                            .id(id++)
                            .role(Role.COP)
                            .build()
            );
        }
        for (int i = 0; i < 4; i++) {
            playerList.add(
                    Player.builder()
                            .id(id++)
                            .role(Role.MAFIA)
                            .build()
            );
        }
        for (int i = 0; i < 12; i++) {
            playerList.add(
                    Player.builder()
                            .id(id++)
                            .role(Role.CITIZEN)
                            .build()
            );
        }
        System.out.println(id);
    }
}
