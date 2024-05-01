package com.vg.infrastructure.persistence.repository;

import com.vg.infrastructure.persistence.entity.WeatherItem;
import com.vg.infrastructure.persistence.entity.WeatherItemId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherItemRepo extends CrudRepository<WeatherItem, WeatherItemId> {

}
