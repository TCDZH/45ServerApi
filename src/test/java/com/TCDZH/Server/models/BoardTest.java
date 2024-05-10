package com.TCDZH.Server.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.TCDZH.api.server.domain.SuitEnum;
import com.TCDZH.server.models.Board;
import com.TCDZH.server.models.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BoardTest {

  @Test
  void testFindWinner(){
    Card card1 = new Card(SuitEnum.HEART,12);
    card1.setPower(100);
    card1.setPlayer(1);

    Card card2 = new Card(SuitEnum.CLUB,12);
    card1.setPower(99);

    Card card3 = new Card(SuitEnum.SPADE,12);
    card1.setPower(1);

    Board board = new Board();
    board.setTrump(card1);

    board.addCard(card1);
    board.addCard(card2);
    board.addCard(card3);

    assertEquals(board.findWinner(),card1.getPlayer());

  }

  @Nested
  class TestCardPowerCalc{

    Board board;

    @BeforeEach
    void setBoard(){
      Card setTrump = new Card(SuitEnum.CLUB,13);
      Card setLed = new Card(SuitEnum.SPADE,1);
      board = new Board();
      board.setTrump(setTrump);
      board.setLedCard(setLed);
    }

    /*
  tiers are 3 - trump, 2 - led, 1 - neither
  Testing strategy
    - same colour same tier (Do trump and none trump)
    - diff colour same tier - these prove colour scoring correctly
    - each tier gap (3-2, 3-1, 2-1)
    - something to do with the ace of hearts
    -
   */

    @Test
    void sameColSameTier(){
      Card win = new Card(SuitEnum.HEART,10);
      Card lose = new Card(SuitEnum.DIAMOND, 8);

      assertTrue(board.findPowerOfCard(win) > board.findPowerOfCard(lose));
    }

    @Test
    void sameColSameTierTrump(){
      Card win = new Card(SuitEnum.CLUB,4);
      Card lose = new Card(SuitEnum.CLUB, 9);

      assertTrue(board.findPowerOfCard(win) > board.findPowerOfCard(lose));
    }

    @Test
    void diffColSameTier(){
      Card changeLed = new Card(SuitEnum.DIAMOND,12);
      board.setLedCard(changeLed);

      Card win = new Card(SuitEnum.SPADE, 2);
      Card lose = new Card(SuitEnum.HEART,6);

      assertTrue(board.findPowerOfCard(win) > board.findPowerOfCard(lose));
    }

    //Im pretty sure that a couple of these scenarios can't happen in a real game but worth the test

    @Test
    void trumpVsLed(){
      Card win = new Card(SuitEnum.CLUB,2);
      Card lose = new Card(SuitEnum.SPADE, 8);

      assertTrue(board.findPowerOfCard(win) > board.findPowerOfCard(lose));
    }


    @Test
    void trumpVsNormal(){
      Card win = new Card(SuitEnum.CLUB,9);
      Card lose = new Card(SuitEnum.DIAMOND, 12);

      assertTrue(board.findPowerOfCard(win) > board.findPowerOfCard(lose));
    }

    @Test
    void ledVsNormal(){
      Card win = new Card(SuitEnum.SPADE,9);
      Card lose = new Card(SuitEnum.DIAMOND, 12);

      assertTrue(board.findPowerOfCard(win) > board.findPowerOfCard(lose));

    }

    //If this passes then it is implied that AoH will beat all lower tiers
    @Test
    void aceOfHeartsVsTrump(){
      Card win = new Card(SuitEnum.HEART,0);
      Card lose = new Card(SuitEnum.CLUB, 0);

      assertTrue(board.findPowerOfCard(win) > board.findPowerOfCard(lose));

    }









  }





}
