package com.goncharov.bdbot.services;


import com.goncharov.bdbot.exceptions.IdAlreadyUsedException;
import com.goncharov.bdbot.exceptions.WrongIdException;
import com.goncharov.bdbot.models.Role;
import com.goncharov.bdbot.repositories.PlayerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {
    private final PlayerRepo playerRepo;
    //todo добавить логгирование
    public Role addUsername(int id, String username){
        var playerList = playerRepo.findAll();
        try {
            var player = playerList.get(id);
            if (player.getUsername() != null){
                throw new IdAlreadyUsedException(id);
            } else {
                playerRepo.addPlayerUsername(id, username);
                return playerRepo.findById(id).getRole();
            }
        }
        catch (IndexOutOfBoundsException e) {
            throw new WrongIdException(id);
        }
    }

}
