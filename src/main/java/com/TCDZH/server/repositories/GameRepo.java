package com.TCDZH.server.repositories;

import com.TCDZH.server.models.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface GameRepo extends MongoRepository<Game,String> {

}
