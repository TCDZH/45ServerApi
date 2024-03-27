package com.TCDZH.server.enums;


import com.TCDZH.api.server.domain.SuitEnum;
import com.TCDZH.server.exceptions.ServiceException;
import com.TCDZH.server.models.Card;
import jakarta.servlet.ServletException;

//Power 15 is always reserved for the ace of hearts
public enum CardPowerRedEnum {

  ace(0,false,1),
  two(1,false,2),
  three(2,false,3),
  four(3,false,4),
  five(4,false,5),
  six(5,false,6),
  seven(6,false,7),
  eight(7,false,8),
  nine(8,false,9),
  ten(9,false,10),
  jack(10,false,11),
  queen(11,false,12),
  king(12,false,13),


  //This is duplicated but I couldn't think of a better way, in reality there are 3 categories (with most of the cards ranking being the same either way, being both is the 3rd category)
  //and i wanted to keep it as boolean because simpler, not the worst thing ever
  twoTrump(1,true,2),
  threeTrump(2,true,3),
  fourTrump(3,true,4),
  sixTrump(5,true,6),
  sevenTrump(6,true,7),
  eightTrump(7,true,8),
  nineTrump(8,true,9),
  tenTrump(9,true,10),
  queenTrump(11,true,12),
  kingTrump(12,true,13),

  aceTrump(0,true,14),
  JackTrump(10,true,16),
  FiveTrump(4,true,17);

  private final int number;

  private final int power;

  private final boolean trump;


  CardPowerRedEnum(int number, boolean trump, int power) {
    this.number = number;
    this.power = power;
    this.trump = trump;
  }

  public static int getPower(Card card, boolean trump){
    //The ace of hearts hard code, foaming
    if(card.getSuit() == SuitEnum.HEART && card.getNumber() == 0){
      return 15;
    }

    for (CardPowerRedEnum value : values()){
      if (value.number == card.getNumber() && value.trump == trump){
        return value.power;
      }
    }
    throw new ServiceException("Invalid card number " + card.getNumber());
  }


}
