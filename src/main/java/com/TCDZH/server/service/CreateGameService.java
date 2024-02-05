package com.TCDZH.server.service;

import com.TCDZH.server.models.Game;
import com.TCDZH.server.models.Player;
import com.TCDZH.server.repositories.GameRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateGameService {

    @Autowired
    private GameRepo repo;

    //Method returns the game id of the created game
    public String createGame(Player firstPlayer, int gameSize){
        Game newGame = Game.CreateNewGame(firstPlayer,gameSize);

        repo.save(newGame);

        System.out.println(newGame.getMaxPlayers());
        return String.valueOf(newGame.get_id());

    }
}
