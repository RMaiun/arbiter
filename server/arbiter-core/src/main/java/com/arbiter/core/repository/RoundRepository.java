package com.arbiter.core.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.arbiter.core.domain.Round;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class RoundRepository {

  private final MongoTemplate template;

  public RoundRepository(MongoTemplate template) {
    this.template = template;
  }

  public List<Round> listRoundsBySeason(String season) {
    return template.find(new Query().addCriteria(where("season").is(season)), Round.class);
  }

  public List<Round> listAllRoundsByPlayer(String player, boolean shutout) {
    Criteria criteria = new Criteria();
    criteria.orOperator(
        Criteria.where("winner1").is(player),
        Criteria.where("winner2").is(player),
        Criteria.where("loser1").is(player),
        Criteria.where("loser2").is(player)
    );
    if (shutout) {
      criteria.and("shutout").is(true);
    }
    return template.find(new Query().addCriteria(criteria), Round.class);
  }

  public Round getById(String id) {
    return template.findOne(new Query().addCriteria(where("_id").is(id)), Round.class);
  }

  public List<Round> listLastRoundsBySeason(String season, int roundsNum) {
    Criteria criteria = Criteria.where("season").is(season);
    Query query = new Query(criteria)
        .with(Sort.by(Direction.DESC, "created")).limit(roundsNum);
    return template.find(query, Round.class);
  }

  public Round saveRound(Round round) {
    return template.insert(round);
  }

  public int bulkSave(List<Round> rounds) {
    return template.bulkOps(BulkMode.ORDERED, Round.class).insert(rounds).execute().getInsertedCount();
  }

  public List<Round> listLastRoundsBeforeDate(ZonedDateTime before) {
    Criteria criteria = Criteria.where("created").lte(before);
    return template.find(new Query(criteria), Round.class);
  }

  public int countSeasonsForPlayer(String player) {
    Criteria criteria = new Criteria();
    criteria.orOperator(
        Criteria.where("winner1").is(player),
        Criteria.where("winner2").is(player),
        Criteria.where("loser1").is(player),
        Criteria.where("loser2").is(player)
    );

    return template.findDistinct(new Query(criteria), "season", Round.class, String.class).size();
  }

  public Long removeAll() {
    return template.remove(Round.class).all().getDeletedCount();
  }

}
