package com.goncharov.bdbot.repositories;

import com.goncharov.bdbot.models.Player;
import com.goncharov.bdbot.models.Role;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PlayerRepo {
    @Autowired
    private Map<Integer, Player> players;

    private Map<Integer, Player> citizens;

    public Map<Integer, Player> findAll(){
        return players;
    }

    public Player findById(int id) {
        return players.get(id);
    }

    public Optional<Player> findByUsername(String username){
        return players.values().stream()
                .filter(p -> p .getUsername()!= null && p.getUsername().equals(username))
                .findFirst();
    }


    public void addPlayerUsername(int id, String username){
        Player player = players.get(id);
        player.setUsername(username);
        if (player.getRole() == Role.CITIZEN) {
            citizens.put(id, player);
        }
        System.out.println(players.toString());
    }

    @PostConstruct
    private void initializeList() {
        int id = 1;
        for (int i = 0; i < 4; i++) {
            players.put(id,
                    Player.builder()
                            .id(id++)
                            .role(Role.COP)
                            .build()
            );
        }
        for (int i = 0; i < 4; i++) {
            players.put(id,
                    Player.builder()
                            .id(id++)
                            .role(Role.MAFIA)
                            .build()
            );
        }
        for (int i = 0; i < 12; i++) {
            players.put(id,
                    Player.builder()
                            .id(id++)
                            .role(Role.CITIZEN)
                            .build()
            );
        }
        System.out.println(id);
    }
}
