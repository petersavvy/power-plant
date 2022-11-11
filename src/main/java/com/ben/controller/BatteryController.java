package com.ben.controller;

import com.ben.dto.request.BatteryRequestDto;
import com.ben.dto.response.BatteryResponseDto;
import com.ben.service.BatteryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BatteryController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final BatteryService batteryService;

    public BatteryController(BatteryService batteryService) {
        this.batteryService = batteryService;
    }

    // Save battery entity in the h2 database.
    @PostMapping(value = "/batteries")
    @ResponseStatus(HttpStatus.CREATED)
    public void savingData(@RequestBody List<BatteryRequestDto> batteryDtos) {
        log.info("Saving Battery in the database.");
        batteryService.savingData(batteryDtos);
    }

    @GetMapping(value = "/batteries")
    @ResponseStatus(HttpStatus.OK)
    public BatteryResponseDto retrieveBatteries(@RequestParam(name = "postcodeFrom") long postcodeFrom,
                                                @RequestParam(name = "postcodeTo") long postcodeTo) {
        log.info("Retrieved Batteries in the database.");
        return batteryService.retrieveBatteries(postcodeFrom, postcodeTo);
    }

}
