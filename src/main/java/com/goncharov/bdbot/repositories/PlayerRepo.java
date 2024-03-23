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
    private Map<Integer, List<String>> victims;
    @Autowired
    private Queue<String> citizens;

    public Map<Integer, Player> findAll() {
        return players;
    }

    public Player findById(int id) {
        return players.get(id);
    }

    public void deleteFromPlayers(int id) {
        players.remove(id);
    }

    public void deleteFromCitizens(String username) {
        citizens.remove(username);
    }

    public void deleteFromVictims(int mafiaId, String username) {
        var curVictims = victims.get(mafiaId);
        curVictims.remove(username);
    }

    public Optional<Player> findByUsername(String username) {
        return players.values().stream()
                .filter(p -> p.getUsername() != null && p.getUsername().equals(username))
                .findFirst();
    }

    public List<String> getVictims(int id) {
        return victims.getOrDefault(id, Collections.emptyList());
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
        if (citizens.size() == 10) {
            setCitizensToMafia();
        }
    }

    //todo здесь
    public List<String> findVictimsByUsername(String username) throws RuntimeException {
        return victims.get(findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Тебе пока что некого убивать")).getId());
    }

    //todo здесь
    private void setCitizensToMafia() {
        var mafiaId = 1;
        var curList = new ArrayList<String>();
        while (!citizens.isEmpty()) {
            victims.get(mafiaId).add(citizens.poll());
            mafiaId++;
            if (mafiaId >= 4){
                mafiaId = 1;
            }

//            curList.add(citizens.poll());
//            if (curList.size() == 3 || curList.size() == 2) {
//                //todo проинициализировать все списки
//                victims.put(mafiaId, curList);
//                System.out.println("Выдали мафии");
//                curList = new ArrayList<String>();
//                mafiaId++;
//            }
        }
    }

    @PostConstruct
    private void initializeList() {
        int id = 1;
        for (int i = 0; i < 4; i++) {
            players.put(id,
                    Player.builder()
                            .id(id++)
                            .role(Role.MAFIA)
                            .build()
            );
        }
        for (int i = 0; i < 4; i++) {
            players.put(id,
                    Player.builder()
                            .id(id++)
                            .role(Role.COP)
                            .build()
            );
        }
        for (int i = 0; i < 10; i++) {
            players.put(id,
                    Player.builder()
                            .id(id++)
                            .role(Role.CITIZEN)
                            .build()
            );
        }
        for (int i = 1; i <=4; i++){
            victims.put(i, new ArrayList<>());
        }
        System.out.println(id);
    }

    public void transferVictims(int id) {
        System.out.println("мы трансферим жертв");
        //удалить из всех игроков, перераспределить
        var killedMafiaVictims = victims.get(id);
        for(var cur : killedMafiaVictims) {
            Integer keyOfShortestList = victims.entrySet().stream()
                    .min(Comparator.comparingInt(entry -> entry.getValue().size()))
                    .map(Map.Entry::getKey)
                    .orElse(null);
            victims.get(keyOfShortestList).add(cur);
            killedMafiaVictims.remove(cur);
        }
        victims.remove(id);
    }

}
