package com.TCDZH.server.models;

import com.TCDZH.api.server.domain.SuitEnum;
import com.TCDZH.server.enums.CardPowerBlackEnum;
import com.TCDZH.server.enums.CardPowerRedEnum;
import java.util.ArrayList;
import lombok.Data;

@Data
public class Board {

  private ArrayList<Card> pile;

  private SuitEnum trump;

  private Card ledCard;

  public int findWinner(){
    Card winningCard = pile.get(0);
    for (Card card : pile){
      if (card.getPower() > winningCard.getPower()){
        winningCard = card;
      }
    }
    return winningCard.getPlayer(); //The Id of the player who won, stored on winning card
  }

  //Multipliers are multiples of 17, one more than the range greatest value possible in the lower tier
  public int findPowerOfCard(Card card){
    int power;
    int multiplier = 1;
    //fucking ace of hearts being an auto trump
    if (card.getSuit() == trump || (card.getSuit() == SuitEnum.HEART && card.getNumber() ==0)){
      multiplier = 25;
    } else if (card.getSuit() == ledCard.getSuit()) {
      multiplier = 14;
    }
    if (card.getSuit() == SuitEnum.DIAMOND ||card.getSuit() == SuitEnum.HEART){
      power = CardPowerRedEnum.getPower(card,multiplier==25) + multiplier;
    }else{
      power = CardPowerBlackEnum.getPower(card,multiplier==25) + multiplier;
    }
    return power;
  }

  public void resetBoard(){
    this.pile.clear();
  }

  public void setTrump(Card card){this.trump = card.getSuit();}

  //The led card thing might not be neccassary, just look at first card in pile? might cause issues tho
  public void addCard(Card card){
    if(this.pile.isEmpty()){
      this.setLedCard(card);
    }
    pile.add(card);
  }

  public Board() {
    this.pile = new ArrayList<Card>();
  }
}
