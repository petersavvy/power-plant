package com.ben.service;

import com.ben.dto.request.BatteryRequestDto;
import com.ben.dto.response.BatteryResponseDto;
import com.ben.entity.Battery;
import com.ben.repository.BatteryRepository;
import com.ben.util.Try;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BatteryService {

    private final BatteryRepository batteryRepository;

    // Save battery entity in the h2 database.
    public void savingData(List<BatteryRequestDto> batteryDtos) {
        List<Battery> batteries = batteryDtos.stream()
                .map(this::createBattery).collect(Collectors.toList());
        batteryRepository.saveAll(batteries);
    }

    public BatteryResponseDto retrieveBatteries(long postcodeFrom, long postcodeTo) {
        List<Battery> batteries = Try.of(() -> batteryRepository.findByPostcodeBetweenOrderByName(postcodeFrom, postcodeTo))
                .orElse(Collections.emptyList());
        return createBatteryResponseDto(batteries);
    }

    private Battery createBattery(BatteryRequestDto batteryDto) {
        return new Battery()
                .setCapacity(batteryDto.getCapacity())
                .setName(batteryDto.getName())
                .setPostcode(batteryDto.getPostcode());
    }

    private BatteryResponseDto createBatteryResponseDto(List<Battery> batteries) {
        List<BatteryRequestDto> batteryRequestDtos = batteries.stream()
                .map(o -> new BatteryRequestDto()
                        .setCapacity(o.getCapacity())
                        .setName(o.getName())
                        .setPostcode(o.getPostcode()))
                .collect(Collectors.toList());

        int respSize = batteries.size();
        // total capacity
        double totalCapacity = batteryRequestDtos.stream()
                .filter(b -> b.getCapacity() > 0f)
                .mapToDouble(BatteryRequestDto::getCapacity)
                .sum();
        // average capacity
        double averageCapacity = totalCapacity / (respSize > 0 ? respSize : 1);

        return new BatteryResponseDto().setBatteryDtos(batteryRequestDtos)
                .setAverageCapacity(averageCapacity)
                .setTotalCapacity(totalCapacity);
    }

}
