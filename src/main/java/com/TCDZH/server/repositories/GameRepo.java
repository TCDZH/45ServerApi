package com.TCDZH.server.repositories;

import com.TCDZH.server.models.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepo extends MongoRepository<Game,String> {

  @Query("{_id: '?0'}")
  Game findGameBy_id(String gameID);

}
