package com.TCDZH.server.models;

import com.TCDZH.api.server.domain.ClientCard.SuitEnum;
import java.util.ArrayList;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("GameObjects")
public class Game {

  @Id
  private String _id;

  private ArrayList<Player> joinedPlayers;

  private int maxPlayers;

  private ArrayList<Card> deck;

  private int turnNo;

  private Board board;

  private int handCount;



  public Game(String _id, ArrayList<Player> joinedPlayers, int maxPlayers, ArrayList<Card> deck, int turnNo, Board board,
      int handCount) {
    this._id = _id;
    this.joinedPlayers = joinedPlayers;
    this.maxPlayers = maxPlayers;
    this.deck = deck;
    this.turnNo = turnNo;
    this.board = board;
    this.handCount = handCount;
  }

  public static Game CreateNewGame(Player firstPlayer, int maxPlayers) {

    ArrayList<Player> joinedPlayers = new ArrayList<>();
    joinedPlayers.add(firstPlayer);
    ArrayList<Card> deck = generateDeck();
    Board board = new Board(deck.remove(0));

    Game game = new Game(UUID.randomUUID().toString(),joinedPlayers,maxPlayers,deck,0,board,0);
    return game;
  }

  public static ArrayList<Card> generateDeck(){
    ArrayList<Card> deck = new ArrayList<>();
    for (SuitEnum value : SuitEnum.values()){
      for (int i = 0; i < 13; i++) {
        deck.add(new Card(value,i));
      }
    }
    return deck;
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

  public ArrayList<Card> generateNewHand(){
    ArrayList<Card> newHand = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      newHand.add(deck.remove(0));
    }
    return newHand;
  }
}
