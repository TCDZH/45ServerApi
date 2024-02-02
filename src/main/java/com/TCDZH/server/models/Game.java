package com.TCDZH.server.models;

import com.TCDZH.api.server.domain.ClientCard.SuitEnum;
import java.util.ArrayList;

import lombok.Data;
import nonapi.io.github.classgraph.json.Id;

@Data
public class Game {

  @Id
  private int gameId;

  private ArrayList<Player> joinedPlayers;

  private int maxPlayers;

  private ArrayList<Card> deck;

  private int turnNo = 0;

  private Board board;

  private int handCount = 0;

  public Game(Player firstPlayer, int maxPlayers) {
    this.maxPlayers = maxPlayers;
    this.joinedPlayers = new ArrayList<>();
    this.joinedPlayers.add(firstPlayer);
    this.deck = generateDeck();
    this.board = new Board(deck.remove(0));
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
