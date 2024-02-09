package com.TCDZH.server.controller;

import com.TCDZH.api.server.controller.CreateGameApi;
import com.TCDZH.server.models.Player;
import com.TCDZH.server.service.GameService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateGameController implements CreateGameApi {

    @Autowired
    HttpServletRequest request;

    @Autowired
    GameService service;

    @Override
    public ResponseEntity<String> createGameGameSizePost(Integer gameSize, String port) {

        Player first = new Player();
        first.setPlayerAddr("http://" + request.getRemoteAddr() + ":" + port);
        first.setScore(0);
        first.setPlayerNo(0);

        return service.createGame(first,gameSize);
    }
}
