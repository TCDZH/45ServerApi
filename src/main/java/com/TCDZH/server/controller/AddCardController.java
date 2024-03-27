package com.TCDZH.server.controller;

import com.TCDZH.api.server.controller.AddCardApi;
import com.TCDZH.api.server.domain.ClientCard;
import com.TCDZH.server.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AddCardController implements AddCardApi {

  @Autowired
  GameService service;

  @Override
  public ResponseEntity<Void> addCardGameIdPost(String gameId, ClientCard card) {


    return service.addCard(gameId,card);
  }
}
