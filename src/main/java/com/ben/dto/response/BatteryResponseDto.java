package com.ben.dto.response;

import com.ben.dto.request.BatteryRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class BatteryResponseDto {

    private List<BatteryRequestDto> batteryDtos;
    private Double totalCapacity;
    private Double averageCapacity;

}
