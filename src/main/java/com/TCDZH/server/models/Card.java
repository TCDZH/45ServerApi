package com.TCDZH.server.models;
import com.TCDZH.api.server.domain.ClientCard;
import com.TCDZH.api.server.domain.ClientCard.SuitEnum;
import lombok.Data;
import org.springframework.data.annotation.PersistenceCreator;

@Data
public class Card {

  private SuitEnum suit;

  private int number;

  private int power;

  private int player;

  //TODO: Move this function to the client side, called when card is played, 3 different tiers of power, Trump, led, nothing, a full set of power above the other
  public int calcPower(){
    return 0;
  }


  @PersistenceCreator
  public Card(SuitEnum suit, int number, int power, int player) {
    this.suit = suit;
    this.number = number;
    this.power = power;
    this.player = player;
  }

  public Card(SuitEnum suit, int number) {
    this.suit = suit;
    this.number = number; //J:10, Q:11, K:12, A:0
    //No player in this constructor, player numbers are assigned when distributed
    //this constructor just for starting deck
  }


  public Card (ClientCard card){
    this.suit = card.getSuit();
    this.number = card.getNumber();
    this.power = card.getPower();
    this.player = card.getPlayer();
  }
}
