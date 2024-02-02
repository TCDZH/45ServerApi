package com.TCDZH.server.models;

import lombok.Data;

@Data
public class Player {
  private int playerNo;

  private String playerAddr; //http + ip collected from initial request + provided port

  private int score;

}
