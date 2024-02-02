package com.TCDZH.server.service;

import com.TCDZH.server.models.Game;
import com.TCDZH.server.models.Player;
import com.TCDZH.server.repositories.GameRepo;
import org.springframework.stereotype.Service;

@Service
public class CreateGameService {

    GameRepo repo;


    //Method returns the game id of the created game
    public String createGame(Player firstPlayer, int gameSize){
        Game newGame = new Game(firstPlayer,gameSize);

        repo.save(newGame);

        return String.valueOf(newGame.getGameId());

    }
}
