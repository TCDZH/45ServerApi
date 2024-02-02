package com.TCDZH.server.models;

import com.TCDZH.api.server.domain.ClientCard.SuitEnum;
import java.util.ArrayList;

public class Board {

  private ArrayList<Card> pile;

  private SuitEnum trump;

  private Card topCard;

  private int findWinner(){

    return 0; //The Id of the player who won, stored on winning card
  }

  private void addCard(Card card){
    pile.add(card);
  }

  public Board(Card topCard) {
    this.trump = topCard.getSuit();
    this.topCard = topCard;
    this.pile = new ArrayList<Card>();
  }
}
