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
    return template.bulkOps(BulkMode.ORDERED,Round.class).insert(rounds).execute().getInsertedCount();
  }

  public List<Round> listLastRoundsBeforeDate(ZonedDateTime before) {
    Criteria criteria = Criteria.where("created").lte(before);
    return template.find(new Query(criteria), Round.class);
  }

  public Long removeAll() {
    return template.remove(Round.class).all().getDeletedCount();
  }

}
