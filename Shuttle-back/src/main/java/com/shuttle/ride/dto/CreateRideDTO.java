package com.shuttle.ride.dto;

import java.util.List;
import com.shuttle.location.dto.RouteDTO;
import com.shuttle.user.dto.BasicUserInfoDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRideDTO {
	private List<BasicUserInfoDTO> passengers;
	private List<RouteDTO> locations;
	private String vehicleType;
	private Boolean babyTransport;
	private Boolean petTransport;
    private String hour;
    private String minute;
}
