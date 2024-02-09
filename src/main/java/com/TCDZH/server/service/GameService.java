package com.TCDZH.server.service;


import com.TCDZH.api.server.domain.ClientCard;
import com.TCDZH.server.exceptions.ClientErrorException;
import com.TCDZH.server.exceptions.InvalidGameIdException;
import com.TCDZH.server.exceptions.ServiceException;
import com.TCDZH.server.models.Card;
import com.TCDZH.server.models.Game;
import com.TCDZH.server.models.Player;
import com.TCDZH.server.repositories.GameRepo;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GameService {

  WebClient client;

  public GameService() {
    this.client = WebClient.builder().build();
  }

  @Autowired
  private GameRepo repo;

  public ResponseEntity<Integer> joinGame(Player newPlayer, String gameId) {
    Game game = checkIfGameNull(gameId);

    newPlayer.setPlayerNo(game.getJoinedPlayers().size() + 1);
    game.addPlayer(newPlayer);
    repo.save(game);

    return new ResponseEntity<>(newPlayer.getPlayerNo(), HttpStatus.OK);

  }

  public ResponseEntity<String> createGame(Player firstPlayer, int gameSize){
    Game newGame = Game.CreateNewGame(firstPlayer,gameSize);

    repo.save(newGame);

    return new ResponseEntity<>(newGame.get_id(),HttpStatus.OK);

  }

  public Game checkIfGameNull(String gameId){
    Game game = repo.findGameBy_id(gameId);
    if (game == null){
      throw new InvalidGameIdException("Game with Id " + gameId +  " does not exist");
    }
    return game;
  }

  public <T> void broadcastToPlayers(String path, ArrayList<Player> players, T body){
    for (Player player : players){
      client.post().uri(player.getPlayerAddr() + path)
          .bodyValue(body)
          .retrieve()
          .onStatus(HttpStatusCode:: is4xxClientError, clientResponse -> clientResponse.toEntity(Void.class)
              .map(entity -> new ClientErrorException(entity.getStatusCode().toString())))
          .onStatus(HttpStatus.INTERNAL_SERVER_ERROR:: equals, clientResponse -> clientResponse.toEntity(Void.class)
              .map(entity -> new ServiceException(entity.getStatusCode().toString())))
          .toEntity(Void.class)
          .block();
    }
  }


  public ResponseEntity<Void> addCard(String gameId, ClientCard card){
    Game game = checkIfGameNull(gameId);
    game.addCardToBoard(new Card(card));
    repo.save(game);
    //Not sure if need to have retry spec, if doesnt go through first time, probs not gonna work on retrys
    broadcastToPlayers("/add-card",game.getJoinedPlayers(),card);

    if (game.getBoard().getPile().size() == game.getMaxPlayers()){
      endOfHand(game);
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  public void endOfHand(Game game){
    int winner = game.calculateHandWinner();
    boolean winGame = game.incrementScore(winner);

    if (winGame){
      endOfGame(game,String.valueOf(winner));
      return;
    }

    game.incrementHandCount();
    game.resetBoard();
    repo.save(game);

    broadcastToPlayers("/end-hand/" + winner,game.getJoinedPlayers(),"");

    //can also be checked if total of scores is multiple of 30 but too much effort atm, may do later
    if (game.getHandCount() == 5){
      endRound(game);
    }

  }

  public void endOfGame(Game game,String winner){
    broadcastToPlayers("/end-game/" + winner, game.getJoinedPlayers(),"");
    repo.delete(game);
  }

  public void endRound(Game game){
    for(Player player : game.getJoinedPlayers()){
      client.post().uri(player.getPlayerAddr() + "/end-round")
          .bodyValue(game.generateNewHand())
          .retrieve()
          .onStatus(HttpStatusCode:: is4xxClientError, clientResponse -> clientResponse.toEntity(Void.class)
          .map(entity -> new ClientErrorException(entity.getStatusCode().toString())))
          .onStatus(HttpStatus.INTERNAL_SERVER_ERROR:: equals, clientResponse -> clientResponse.toEntity(Void.class)
              .map(entity -> new ServiceException(entity.getStatusCode().toString())))
          .toEntity(Void.class)
          .block();
    }
  }
}
