package com.vg.infrastructure.persistence.repository;

import com.vg.infrastructure.persistence.entity.Consumer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsumerRepo extends CrudRepository<Consumer, Long> {

    Optional<Consumer> findByApiKey(String apiKey);

}
