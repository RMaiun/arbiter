package com.arbiter.core.repository;

import static java.util.Optional.ofNullable;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.arbiter.core.domain.Player;
import com.arbiter.core.domain.Round;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class PlayerRepository {

  private final MongoTemplate template;

  public PlayerRepository(MongoTemplate template) {
    this.template = template;
  }

  public List<Player> listAll(boolean onlyActive) {
    var foundList = onlyActive
        ? template.find(new Query().addCriteria(where("active").is(true)), Player.class)
        : template.findAll(Player.class);
    return foundList.stream()
        .sorted(Comparator.comparing(Player::getId))
        .collect(Collectors.toList());
  }

  public List<Player> findPlayers(List<String> surnames) {
    return template.find(new Query().addCriteria(where("surname").in(surnames).and("active").is(true)), Player.class);
  }

  public Optional<Player> getPlayer(String name) {
    return ofNullable(template.findOne(new Query().addCriteria(where("surname").is(name)), Player.class));
  }

  public Optional<Player> getPlayerByCriteria(Criteria criteria) {
    return Optional.ofNullable(template.findOne(new Query(criteria), Player.class));
  }

  public Player savePlayer(Player player) {
    return template.save(player);
  }

  public Player updatePlayer(Player player) {
    return template.save(player);
  }

  public Long removeAll() {
    return template.remove(Player.class)
        .all().getDeletedCount();
  }

  public Long removeByTid(String tid) {
    var query = new Query(Criteria.where("tid").is(tid));
    return template.remove(query, Player.class).getDeletedCount();
  }

  public int bulkSave(List<Player> players) {
    return template.bulkOps(BulkMode.ORDERED,Player.class).insert(players).execute().getInsertedCount();
  }
}
