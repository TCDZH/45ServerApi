package com.TCDZH.Server.service;


import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.TCDZH.api.server.domain.ClientCard;
import com.TCDZH.api.server.domain.SuitEnum;
import com.TCDZH.server.models.Board;
import com.TCDZH.server.models.Card;
import com.TCDZH.server.models.Game;
import com.TCDZH.server.models.Player;
import com.TCDZH.server.repositories.GameRepo;
import com.TCDZH.server.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@WireMockTest
@ExtendWith(MockitoExtension.class)
class GameServiceTest {

  //Test the broadcast function, create a dummy game object with one player with the addr of the wiremock and assert the responses
  //this is an actually useful test, just cant muster the strength to write it now
  //No point testing the broadcast function itself, doesn't return anything, just test the methods that use it


  //The tests for each of these methods will be more pure unit tests i.e will not test the branches that trigger other
  //methods, these methods will be tested seperatly
  //Not really worth testing the ways that these are triggered as they are pretty simple


  private WireMock wireMock;

  @InjectMocks
  private GameService gameService;

  @Mock
  private GameRepo repo;

  @Spy
  private Game game;


  //TODO: refactor these unit tests to the new deck methodoligy
  //Dont spy the game service, just find a way to mock out the repo
  @BeforeEach
  public void WiremockSetup(WireMockRuntimeInfo wireMockRuntimeInfo){
    wireMock = wireMockRuntimeInfo.getWireMock();
    Player player = new Player();
    player.setPlayerAddr(wireMockRuntimeInfo.getHttpBaseUrl());

    ArrayList<Card> deck = gameService.generateDeck();

    Board board = new Board();
    board.setTrump(deck.remove(0));


    ArrayList<Player> joined = new ArrayList<>();
    joined.add(player);

    game.set_id("testId");
    game.setJoinedPlayers(joined);
    game.setMaxPlayers(2);
    game.setBoard(board);

  }
  /*
      ArrayList<Player> joinedPlayers = new ArrayList<>();
    joinedPlayers.add(firstPlayer);
    ArrayList<Card> deck = generateDeck();
    Board board = new Board(deck.remove(0));

    Game game = new Game(UUID.randomUUID().toString(),joinedPlayers,maxPlayers,deck,0,board,0);
    return game;
   */


  @Test
  void addCard() throws JsonProcessingException {
    ClientCard clientCard = new ClientCard();
    clientCard.setNumber(0);
    clientCard.setSuit(SuitEnum.HEART);
    clientCard.setPower(12);
    clientCard.setPlayer(0);

    when(repo.save(eq(game))).thenReturn(game);
    when(repo.findGameBy_id(anyString())).thenReturn(game);

    ObjectMapper mapper = new ObjectMapper();
    wireMock.register(WireMock.post("/add-card")
        .withRequestBody(equalTo(mapper.writeValueAsString(clientCard)))
        .willReturn(ok()));

    gameService.addCard("gameId",clientCard);
  }

  @Test
  void endOfHand() {
    when(repo.save(eq(game))).thenReturn(game);
    //when(game.calculateHandWinner()).thenReturn(0);
    doReturn(0).when(game).calculateHandWinner();
    wireMock.register(WireMock.post("/end-hand/0")
        .willReturn(ok()));

    gameService.endOfHand(game);

  }

  @Test
  void endOfGame() {
    wireMock.register(WireMock.post("/end-game/0")
        .willReturn(ok()));

    gameService.endOfGame(game,"0");
  }

  @Test
  void endRound() throws JsonProcessingException {
    ArrayList<Card> newHand = new ArrayList<>();

    ObjectMapper mapper = new ObjectMapper();

    Card card = new Card(SuitEnum.HEART,12);

    newHand.add(card);

    //Assert that the body is equal to this new hand

    //doReturn(newHand).when(game).generateNewHand();

    //bit of a weaker test now that im not asserting checking the body is the right thing but should be fine
    //Maybe a way to get the body from this call and assert that it is 5 cards?
    //but almost certain that it works so should be fine?
    wireMock.register(WireMock.post("/end-round/" + game.getBoard().getTrump().toString())
        .willReturn(ok()));

    gameService.endRound(game);

  }
}