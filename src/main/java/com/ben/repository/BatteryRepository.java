package com.ben.repository;

import com.ben.entity.Battery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatteryRepository extends CrudRepository<Battery, Integer> {

    List<Battery> findByPostcodeBetweenOrderByName(long from, long to);

}
