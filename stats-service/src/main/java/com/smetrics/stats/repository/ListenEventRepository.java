package com.smetrics.stats.repository;

import com.smetrics.stats.model.ListenEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ListenEventRepository extends MongoRepository<ListenEvent, String> {

    List<ListenEvent> findByUserId(Long userId);
}
