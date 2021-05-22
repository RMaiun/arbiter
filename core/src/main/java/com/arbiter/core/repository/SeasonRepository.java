package com.arbiter.core.repository;

import static java.util.Optional.ofNullable;

import com.arbiter.core.domain.Season;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class SeasonRepository {

  public static final String ATTR_SEASON_END_NOTIFICATION = "seasonEndNotification";
  private final MongoTemplate template;

  public SeasonRepository(MongoTemplate template) {
    this.template = template;
  }

  public Optional<Season> getSeason(String name) {
    return ofNullable(template.findOne(new Query(Criteria.where("name").is(name)), Season.class));
  }

  public Season saveSeason(Season season) {
    return template.insert(season);
  }

  public Season updateSeason(Season season) {
    return template.save(season);
  }

  public List<Season> listAll() {
    return template.findAll(Season.class);
  }

  public Long removeAll() {
    return template.remove(Season.class).all().getDeletedCount();
  }

  public Optional<Season> findFirstSeasonWithoutNotification() {
    Criteria criteria = Criteria.where(ATTR_SEASON_END_NOTIFICATION).is(null);
    Query query = new Query(criteria).with(Sort.by(Direction.ASC, ATTR_SEASON_END_NOTIFICATION));
    return ofNullable(template.findOne(query, Season.class));
  }
}
