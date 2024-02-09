package com.TCDZH.server.models;

import com.TCDZH.api.server.domain.ClientCard.SuitEnum;
import java.util.ArrayList;
import lombok.Data;

@Data
public class Board {

  private ArrayList<Card> pile;

  private SuitEnum trump;

  private Card topCard;

  //TODO: do this one after card power, shouldn't be too bad
  public int findWinner(){

    return 0; //The Id of the player who won, stored on winning card
  }

  public void resetBoard(){
    this.pile.clear();
  }

  public void addCard(Card card){
    pile.add(card);
  }

  public Board(Card topCard) {
    this.trump = topCard.getSuit();
    this.topCard = topCard;
    this.pile = new ArrayList<Card>();
  }
}
