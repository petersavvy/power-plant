package com.ben.repository;

import com.ben.entity.Battery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BatteryRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BatteryRepository targetRepository;

    private final String FIXED_NAME = "Mock Battery";

    @After()
    public void cleanUp(){
        targetRepository.deleteAll();
    }

    @Before()
    public void setup(){
        this.insertOneBattery();
    }


    @Test
    public void testSaveNewBattery() {
        List<Battery> batteries = (List<Battery>) targetRepository.findAll();
        assertThat(batteries).size().isPositive();
    }

    @Test
    public void testGetRetrieveBatteries() {
        List<Battery> batteries = targetRepository.findByPostcodeBetweenOrderByName(0L, 2L);
        assertThat(batteries).size().isPositive();
        assertThat(batteries.get(0).getName()).isEqualTo(FIXED_NAME);
    }

    @Test
    public void testUpdateBatteries() {
        List<Battery> batteries = targetRepository.findByPostcodeBetweenOrderByName(0L, 2L);
        Battery b = batteries.get(0);
        b.setName(FIXED_NAME + "EDITED");
        targetRepository.save(b);
        batteries = targetRepository.findByPostcodeBetweenOrderByName(0L, 2L);
        Battery bAfterEdited = batteries.get(0);
        assertThat(bAfterEdited.getName()).isEqualTo(FIXED_NAME + "EDITED");
    }

    @Test
    public void testDeleteBatteries() {
        List<Battery> batteries = targetRepository.findByPostcodeBetweenOrderByName(0L, 2L);
        Battery b = batteries.get(0);
        b.setName(FIXED_NAME + "EDITED");
        targetRepository.delete(b);
        batteries = (List<Battery>) targetRepository.findAll();
        assertThat(batteries).size().isEqualTo(0);
    }

    private void insertOneBattery(){
        long FIXED_POSTCODE = 1L;
        Double FIXED_CAPACITY = 1d;
        Battery b = new Battery()
                .setCapacity(FIXED_CAPACITY)
                .setName(FIXED_NAME)
                .setPostcode(FIXED_POSTCODE);
        entityManager.persist(b);
    }
}
