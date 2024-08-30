package com.TCDZH.server.models;

import com.TCDZH.api.server.domain.SuitEnum;
import com.TCDZH.server.service.GameService;
import java.util.ArrayList;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("GameObjects")
@NoArgsConstructor
public class Game {

  @Id
  private String _id;

  private ArrayList<Player> joinedPlayers;

  private int maxPlayers;

  private int turnNo;

  private Board board;

  private int handCount;

  public Game(String _id, ArrayList<Player> joinedPlayers, int maxPlayers, int turnNo, Board board,
      int handCount) {
    this._id = _id;
    this.joinedPlayers = joinedPlayers;
    this.maxPlayers = maxPlayers;
    this.turnNo = turnNo;
    this.board = board;
    this.handCount = handCount;
  }

  public static Game CreateNewGame(Player firstPlayer, int maxPlayers) {

    ArrayList<Player> joinedPlayers = new ArrayList<>();
    joinedPlayers.add(firstPlayer);
    Board board = new Board();

    Game game = new Game(UUID.randomUUID().toString(),joinedPlayers,maxPlayers,0,board,0);
    return game;
  }

  public void addPlayer(Player player){
    this.joinedPlayers.add(player);
  }


  public void addCardToBoard(Card card){

    this.board.addCard(card);
  }

  public void incrementHandCount(){
    this.handCount += 1;
  }

  public int calculateHandWinner(){
    return this.board.findWinner();
  }

  public boolean incrementScore(int player){
    return this.joinedPlayers.get(player).winHand();
  }

  public void resetBoard(){
    this.board.resetBoard();
  }


}
