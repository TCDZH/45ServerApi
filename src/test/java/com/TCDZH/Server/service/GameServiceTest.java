package com.TCDZH.Server.service;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.TCDZH.api.server.domain.ClientCard;
import com.TCDZH.api.server.domain.ClientCard.SuitEnum;
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


  //Dont spy the game service, just find a way to mock out the repo, that is the
  //real issue.
  @BeforeEach
  public void WiremockSetup(WireMockRuntimeInfo wireMockRuntimeInfo){
    wireMock = wireMockRuntimeInfo.getWireMock();
    Player player = new Player();
    player.setPlayerAddr(wireMockRuntimeInfo.getHttpBaseUrl());
    game = Game.CreateNewGame(player, 2);


  }


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
    when(game.generateNewHand()).thenReturn(newHand);

    wireMock.register(WireMock.post("/end-round")
        .withRequestBody(equalTo(mapper.writeValueAsString(newHand)))
        .willReturn(ok()));

  }
}