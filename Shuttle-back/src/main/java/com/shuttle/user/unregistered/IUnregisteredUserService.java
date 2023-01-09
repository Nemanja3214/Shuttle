package com.shuttle.user.unregistered;

import com.shuttle.common.exception.NonExistantVehicleType;
import com.shuttle.ride.dto.CreateRideEstimationDTO;
import com.shuttle.ride.dto.EstimationDTO;

public interface IUnregisteredUserService {
	public EstimationDTO getEstimation(CreateRideEstimationDTO rideDTO) throws NonExistantVehicleType;

}
