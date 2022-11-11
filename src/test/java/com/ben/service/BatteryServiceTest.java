package com.ben.service;

import com.ben.dto.request.BatteryRequestDto;
import com.ben.dto.response.BatteryResponseDto;
import com.ben.entity.Battery;
import com.ben.repository.BatteryRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class BatteryServiceTest {

    @Autowired
    private BatteryRepository batteryRepository;

    @Autowired
    private BatteryService service;

    @After
    public void cleanUp(){
        batteryRepository.deleteAll();
    }

    @Test
    public void retrieveBatteries_success() {
        long postCodeFrom = 0L;
        long postCodeTo = 6L;
        List<BatteryRequestDto> batteryDtoInput = createBatteryDtoInput();
        service.savingData(createBatteryDtoInput());
        BatteryResponseDto responseDto = service.retrieveBatteries(postCodeFrom, postCodeTo);
        assertThat(responseDto.getBatteryDtos()).hasSizeLessThan(batteryDtoInput.size());
    }

    @Test
    public void retrieveBatteries_EmptyList_success() {
        long postCodeFrom = 0L;
        long postCodeTo = 6L;
        BatteryResponseDto responseDto = service.retrieveBatteries(postCodeFrom, postCodeTo);
        assertThat(responseDto.getBatteryDtos()).isEmpty();
    }

    @Test
    public void savingData_success() {
        service.savingData(createBatteryDtoInput());
        List<Battery> batteryList = (List<Battery>) batteryRepository.findAll();
        assertThat(batteryList.get(0).getName()).isEqualTo("Bat1");
    }

    private List<BatteryRequestDto> createBatteryDtoInput() {
        List<BatteryRequestDto> batteries = new ArrayList<>();
        batteries.add(new BatteryRequestDto().setName("Bat1").setPostcode(1L).setCapacity(3.3));
        batteries.add(new BatteryRequestDto().setName("New Bat").setPostcode(5L).setCapacity(5.7));
        batteries.add(new BatteryRequestDto().setName("Old Bat").setPostcode(9L).setCapacity(10.7));
        return batteries;
    }

}


