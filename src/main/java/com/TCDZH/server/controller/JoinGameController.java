package com.TCDZH.server.controller;

import com.TCDZH.api.server.controller.JoinGameApi;
import com.TCDZH.server.models.Player;
import com.TCDZH.server.service.JoinGameService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JoinGameController implements JoinGameApi {

  @Autowired
  HttpServletRequest request;

  @Autowired
  JoinGameService joinService;

  @Override
  public ResponseEntity<Integer> joinGameGameIdPost(String gameId, String port) {

    Player newPlayer = new Player();
    newPlayer.setPlayerAddr("http://" + request.getRemoteAddr() + ":" + port);
    newPlayer.setScore(0);

    return joinService.joinGame(newPlayer, gameId);
  }
}
