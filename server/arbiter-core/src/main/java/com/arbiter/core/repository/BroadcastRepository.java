package com.arbiter.core.repository;

import static java.util.Optional.ofNullable;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.arbiter.core.domain.Broadcast;
import java.util.Optional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class BroadcastRepository {

  private final MongoTemplate template;

  public BroadcastRepository(MongoTemplate template) {
    this.template = template;
  }

  public Broadcast saveBroadcast(Broadcast broadcast) {
    return template.save(broadcast);
  }

  public Optional<Broadcast> getBroadcast(String id) {
    return ofNullable(template.findOne(new Query().addCriteria(where("_id").is(id)), Broadcast.class));
  }

  public Long removeAll() {
    return template.remove(Broadcast.class)
        .all().getDeletedCount();
  }
}
