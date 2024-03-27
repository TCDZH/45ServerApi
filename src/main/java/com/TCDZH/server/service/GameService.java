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

    if(game.getJoinedPlayers().size() == game.getMaxPlayers()){
      startGame(game);
    }

    return new ResponseEntity<>(newPlayer.getPlayerNo(), HttpStatus.OK);
  }



  /**
   * This function triggers when a create game requet is recived from a client
   * It creates a game object, stores it in the mongo database and returns to the player the game id, so they can share it with
   * others so they can join the game
   * @param firstPlayer
   * @param gameSize
   * @return
   */
  public ResponseEntity<String> createGame(Player firstPlayer, int gameSize){
    Game newGame = Game.CreateNewGame(firstPlayer,gameSize);

    repo.save(newGame);

    return new ResponseEntity<>(newGame.get_id(),HttpStatus.OK);

  }

  private Game checkIfGameNull(String gameId){
    Game game = repo.findGameBy_id(gameId);
    if (game == null){
      throw new InvalidGameIdException("Game with Id " + gameId +  " does not exist");
    }
    return game;
  }

  /**
   * This function handles the webclient calls for broadcasting the various messages to the players.
   * @param path
   * @param players
   * @param body
   * @param <T>
   */
  public <T> void broadcastToPlayers(String path, ArrayList<Player> players, T body){
    for (Player player : players){
      client.post().uri(player.getPlayerAddr() + path)
          .bodyValue(body)
          .retrieve()
          .onStatus(HttpStatusCode:: is4xxClientError, clientResponse -> clientResponse.toEntity(Void.class)
              .map(entity -> new ClientErrorException(entity.getStatusCode().toString())))
          .onStatus(HttpStatus.INTERNAL_SERVER_ERROR:: equals, clientResponse -> clientResponse.toEntity(Void.class)
              .map(entity -> new ServiceException(entity.getStatusCode().toString())));
    }
  }

  /**
   * This function is triggered when an add card request is received from the player.
   * It calculates the power of that card in context, adds it to the server board, and then broadcasts that card to the players
   * so they can add it to their board to be shown to the players.
   *
   * This function is the entry point to all the other functions and is the only one that pulls tha game object from the database.
   * @param gameId
   * @param clientCard
   * @return
   */
  public ResponseEntity<Void> addCard(String gameId, ClientCard clientCard){
    Game game = checkIfGameNull(gameId);
    Card card = new Card(clientCard);
    card.setPower(game.getBoard().findPowerOfCard(card));
    game.addCardToBoard(card);
    repo.save(game);
    //Not sure if need to have retry spec, if doesnt go through first time, probs not gonna work on retrys
    broadcastToPlayers("/add-card",game.getJoinedPlayers(),card);

    if (game.getBoard().getPile().size() == game.getMaxPlayers()){
      endOfHand(game);
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * This function triggers when the current hand/trick has been won.
   * It broadcasts to the players that the hand is over and who won, signifying the start of the next hand.
   * @param game
   */
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

  /**
   * This function triggers when the game ends.
   * It broadcasts to all players that the game is over and who won the game.
   * @param game
   * @param winner
   */
  public void endOfGame(Game game,String winner){
    broadcastToPlayers("/end-game/" + winner, game.getJoinedPlayers(),"");
    repo.delete(game);
  }

  /**
   * This function triggers when all the players have joined (or (led?) player sends signal to start, not added).
   * It broadcasts to all players that the game has started and triggers the clients go switch to the game screen.
   * This brodcast contains the trump card for the first round of the game.
   */
  public void startGame(Game game){
    broadcastToPlayers("/start-game" + game.getBoard().getTrump().toString(),game.getJoinedPlayers(),"");
  }

  /**
   * This triggers at the end of the round, the players have used up all the cards in their hand.
   * This function sends out 5 new cards to each player so the game can continue.
   * @param game
   */
  public void endRound(Game game){
    //put trump card in path param
    for(Player player : game.getJoinedPlayers()){
      client.post().uri(player.getPlayerAddr() + "/end-round" + game.getBoard().getTrump().toString())
          .bodyValue(game.generateNewHand())
          .retrieve()
          .onStatus(HttpStatusCode:: is4xxClientError, clientResponse -> clientResponse.toEntity(Void.class)
          .map(entity -> new ClientErrorException(entity.getStatusCode().toString())))
          .onStatus(HttpStatus.INTERNAL_SERVER_ERROR:: equals, clientResponse -> clientResponse.toEntity(Void.class)
              .map(entity -> new ServiceException(entity.getStatusCode().toString())));
    }
  }
}
