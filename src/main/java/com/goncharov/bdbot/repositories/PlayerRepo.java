package com.goncharov.bdbot.repositories;

import com.goncharov.bdbot.models.Player;
import com.goncharov.bdbot.models.Role;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PlayerRepo {
    @Autowired
    private Map<Integer, Player> players;
    @Autowired
    private Map<Integer, String> victims;
    @Autowired
    private Queue<String> citizens;

    public Map<Integer, Player> findAll() {
        return players;
    }

    public Player findById(int id) {
        return players.get(id);
    }

    public Optional<Player> findByUsername(String username) {
        return players.values().stream()
                .filter(p -> p.getUsername() != null && p.getUsername().equals(username))
                .findFirst();
    }

    public String getVictims(int id){
        return victims.getOrDefault(id, "not found");
    }


    public void addPlayerUsername(int id, String username) {
        Player player = players.get(id);
        player.setUsername(username);
        if (player.getRole() == Role.CITIZEN) {
            addToCitizens(player);
        }
        System.out.println(players.toString());
    }

    private void addToCitizens(Player player) {
        System.out.println("Добавили в горожане " + player.getId());
        citizens.add(player.getUsername());
        if (citizens.size() == 12) {
            setCitizensToMafia();
        }
    }

    public String[] findVictimsByUsername(String username) throws RuntimeException{
        var victimsString =  victims.get(findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Тебе пока, что некого убивать")).getId());
        return victimsString.split("\n");
    }

    private void setCitizensToMafia() {
        var mafiaId = 4;
        var curList = new ArrayList<String>();
        while (!citizens.isEmpty()){
            curList.add(citizens.poll());
            if (curList.size() == 3){
                var vicString = new StringBuilder();
                curList.forEach(s -> vicString.append(s).append("\n"));
                victims.put(mafiaId, vicString.toString());
                System.out.println("Выдали мафии");
                curList = new ArrayList<String>();
                mafiaId++;
            }
        }
    }

    @PostConstruct
    private void initializeList() {
        int id = 1;
        for (int i = 0; i < 3; i++) {
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
