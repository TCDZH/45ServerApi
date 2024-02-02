package com.TCDZH.server.controller;

import com.TCDZH.api.server.controller.CreateGameApi;
import com.TCDZH.server.models.Player;
import com.TCDZH.server.service.CreateGameService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateGameController implements CreateGameApi {

    @Autowired
    HttpServletRequest request;

    @Autowired
    CreateGameService service;

    @Override
    public ResponseEntity<String> createGameGameSizePost(Integer gameSize, String port) {

        Player first = new Player();
        first.setPlayerAddr("http://" + request.getRemoteAddr() + ":" + port);
        first.setScore(0);
        first.setPlayerNo(0);
        String gameId = service.createGame(first,gameSize);

        return new ResponseEntity<>(gameId,HttpStatus.OK);
    }
}
