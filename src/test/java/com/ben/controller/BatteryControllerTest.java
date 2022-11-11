package com.ben.controller;

import com.ben.dto.request.BatteryRequestDto;
import com.ben.repository.BatteryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class BatteryControllerTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BatteryRepository batteryRepository;

    @After
    public void cleanUp(){
        batteryRepository.deleteAll();
    }

    @Test
    public void savingData_success() throws Exception {
        List<BatteryRequestDto> batteryDtos = createBatteryDtoInput();
        mockMvc.perform(post("/api/v1/batteries")
                .content(objectMapper.writeValueAsString(batteryDtos))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
        batteryRepository.deleteAll();
    }

    @Test
    public void savingData_success_then_retrieveBatteries_success() throws Exception {
        long postCodeFrom = 0L;
        long postCodeTo = 6L;

        List<BatteryRequestDto> batteryDtos = createBatteryDtoInput();
        mockMcvInsertData(batteryDtos);
        mockMcvRetrieveBatteries(postCodeFrom, postCodeTo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("batteryDtos[0]").value(batteryDtos.get(0)))
                .andExpect(jsonPath("totalCapacity").value(9))
                .andExpect(jsonPath("averageCapacity").value(4.5));
    }

    @Test
    public void savingData_success_then_retrieveBatteries_empty_success() throws Exception {
        long postCodeFrom = 6L;
        long postCodeTo = 3L;

        List<BatteryRequestDto> batteryDtos = createBatteryDtoInput();
        mockMcvInsertData(batteryDtos);
        mockMcvRetrieveBatteries(postCodeFrom, postCodeTo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("batteryDtos").isArray())
                .andExpect(jsonPath("batteryNames.[0]").doesNotExist())
                .andExpect(jsonPath("totalCapacity").value(0))
                .andExpect(jsonPath("averageCapacity").value(0));
    }

    @Test
    public void retrieveBatteries_with_invalid_param_then_return_bad_request() throws Exception {
        long postCodeTo = 3L;
        mockMcvRetrieveBatteries(null, postCodeTo)
                .andExpect(status().isBadRequest());
    }

    private ResultActions mockMcvRetrieveBatteries(Long postCodeFrom, Long postCodeTo) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/api/v1/batteries");
        requestBuilder.param("postcodeFrom", String.valueOf(postCodeFrom));
        requestBuilder.param("postcodeTo", String.valueOf(postCodeTo));

        return mockMvc.perform(requestBuilder.contentType(MediaType.APPLICATION_JSON));
    }

    private void mockMcvInsertData(List<BatteryRequestDto> batteryDtos) throws Exception {
        mockMvc.perform(post("/api/v1/batteries")
                .content(objectMapper.writeValueAsString(batteryDtos))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
    }

    private List<BatteryRequestDto> createBatteryDtoInput() {
        List<BatteryRequestDto> batteries = new ArrayList<>();
        batteries.add(new BatteryRequestDto().setName("Bat1").setPostcode(1L).setCapacity(3.3));
        batteries.add(new BatteryRequestDto().setName("New Bat").setPostcode(5L).setCapacity(5.7));
        batteries.add(new BatteryRequestDto().setName("Old Bat").setPostcode(9L).setCapacity(10.7));
        return batteries;
    }

}
