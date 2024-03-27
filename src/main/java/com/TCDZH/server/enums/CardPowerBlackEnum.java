package com.TCDZH.server.enums;

import com.TCDZH.server.exceptions.ServiceException;
import com.TCDZH.server.models.Card;
import lombok.Getter;


//Power 15 is always reserved for the ace of hearts
//both colours could be refacatored into one enum with the addition of an extra "col" attribute
@Getter
public enum CardPowerBlackEnum {

  ace(0,false,10),
  two(1,false,9),
  three(2,false,8),
  four(3,false,7),
  five(4,false,6),
  six(5,false,5),
  seven(6,false,4),
  eight(7,false,3),
  nine(8,false,2),
  ten(9,false,1),

  jack(10,false,11),
  queen(11,false,12),
  king(12,false,13),

  twoTrump(1,true,9),
  threeTrump(2,true,8),
  fourTrump(3,true,7),
  sixTrump(5,true,5),
  sevenTrump(6,true,4),
  eightTrump(7,true,3),
  nineTrump(8,true,2),
  tenTrump(9,true,1),

  queenTrump(11,true,12),
  kingTrump(12,true,13),

  aceTrump(0,true,14),
  JackTrump(10,true,16),
  FiveTrump(4,true,17);

  private final int number;

  private final int power;

  private final boolean trump;


  CardPowerBlackEnum(int number, boolean trump, int power) {
    this.number = number;
    this.power = power;
    this.trump = trump;
  }

  public static int getPower(Card card, boolean trump){
    for (CardPowerBlackEnum value : values()){
      if (value.number == card.getNumber() && value.trump == trump){
        return value.power;
      }
    }
    throw new ServiceException("Invalid card number " + card.getNumber());
  }





}
