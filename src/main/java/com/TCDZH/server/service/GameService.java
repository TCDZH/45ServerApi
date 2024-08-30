package com.TCDZH.server.service;


import com.TCDZH.api.server.domain.ClientCard;
import com.TCDZH.api.server.domain.SuitEnum;
import com.TCDZH.server.exceptions.ClientErrorException;
import com.TCDZH.server.exceptions.InvalidGameIdException;
import com.TCDZH.server.exceptions.ServiceException;
import com.TCDZH.server.models.Board;
import com.TCDZH.server.models.Card;
import com.TCDZH.server.models.Game;
import com.TCDZH.server.models.Player;
import com.TCDZH.server.repositories.GameRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Random;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import springfox.documentation.spring.web.json.Json;

@Service
@Data
public class GameService {

  WebClient client;

  private ArrayList<Card> deck;

  public GameService() {
    this.client = WebClient.builder().build();
  }

  @Autowired
  private GameRepo repo;

  //Broadcast to joined players that a new player has joined? More of a QOL change
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
   * This function triggers when a create game request is received from a client
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

  /**
   * This function triggers when all the players have joined (or (lead?) player sends signal to start, not added).
   * It broadcasts to all players that the game has started and triggers the clients go switch to the game screen.
   * This brodcast contains the trump card for the first round of the game and the initial hand the players start with.
   */
  public void startGame(Game game) {
    deck = generateDeck();
    game.getBoard().setTrump(deck.remove(new Random().nextInt(51)));

    for(Player player : game.getJoinedPlayers()){
      client.post().uri(player.getPlayerAddr() + "/start-game/" + game.getBoard().getTrump().toString()
              + "/" + game.getJoinedPlayers().size())
          .bodyValue(generateNewHand())
          .retrieve()
          .onStatus(HttpStatusCode:: is4xxClientError, clientResponse -> clientResponse.toEntity(Void.class)
              .map(entity -> new ClientErrorException(entity.getStatusCode().toString())))
          .onStatus(HttpStatus.INTERNAL_SERVER_ERROR:: equals, clientResponse -> clientResponse.toEntity(Void.class)
              .map(entity -> new ServiceException(entity.getStatusCode().toString())));
    }
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
   * so they can add it to their board to be shown on the GUI.
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

    //Not sure if need to have retry spec, if doesnt go through first time, probs not gonna work on retrys
    broadcastToPlayers("/add-card",game.getJoinedPlayers(),card);

    if (game.getBoard().getPile().size() == game.getMaxPlayers()){
      endOfHand(game);
    }

    repo.save(game);
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
   * This triggers at the end of the round, the players have used up all the cards in their hand, puts new trump on the board
   * This function sends out 5 new cards to each player so the game can continue.
   * @param game
   */
  public void endRound(Game game){
    //put trump card in path param
    this.deck = generateDeck();// resetting the deck after each round, the reset deck never gets saved but then again it didnt really need to

    game.getBoard().setTrump(deck.remove(new Random().nextInt(51)));

    for(Player player : game.getJoinedPlayers()){
      client.post().uri(player.getPlayerAddr() + "/end-round/" + game.getBoard().getTrump().toString())
          .bodyValue(generateNewHand())
          .retrieve()
          .onStatus(HttpStatusCode:: is4xxClientError, clientResponse -> clientResponse.toEntity(Void.class)
          .map(entity -> new ClientErrorException(entity.getStatusCode().toString())))
          .onStatus(HttpStatus.INTERNAL_SERVER_ERROR:: equals, clientResponse -> clientResponse.toEntity(Void.class)
              .map(entity -> new ServiceException(entity.getStatusCode().toString())));
    }
  }

  public ArrayList<Card> generateNewHand()  {
    ArrayList<Card> newHand = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      newHand.add(deck.remove(0));
    }
    try {
      System.out.println(new ObjectMapper().writeValueAsString(newHand));
    }catch (JsonProcessingException ignore){

    }
    return newHand;
  }

  public ArrayList<Card> generateDeck(){
    ArrayList<Card> deck = new ArrayList<>();
    for (SuitEnum value : SuitEnum.values()){
      for (int i = 0; i < 13; i++) {
        deck.add(new Card(value,i));
      }
    }
    return deck;
  }
}
