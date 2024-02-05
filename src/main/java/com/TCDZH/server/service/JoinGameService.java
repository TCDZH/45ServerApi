package com.TCDZH.server.service;


import com.TCDZH.server.models.Game;
import com.TCDZH.server.models.Player;
import com.TCDZH.server.repositories.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class JoinGameService {

  @Autowired
  private GameRepo repo;

  public ResponseEntity<Integer> joinGame(Player newPlayer, String gameId){
    Game game = repo.findGameBy_id(gameId);

    if (game != null){
      newPlayer.setPlayerNo(game.getJoinedPlayers().size() + 1);
      game.addPlayer(newPlayer);
      repo.save(game);
      return new ResponseEntity<>(newPlayer.getPlayerNo(), HttpStatus.OK);
    }else{
      //TODO: Create an exception, throw it, also create REH to catch said exception
      System.out.println("null");
      return null;
    }



  }

}
