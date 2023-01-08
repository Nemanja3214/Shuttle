package com.shuttle.user.unregistered;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuttle.ride.dto.CreateRideEstimationDTO;
import com.shuttle.ride.dto.EstimationDTO;
import com.shuttle.vehicle.vehicleType.IVehicleTypeRepository;
import com.shuttle.vehicle.vehicleType.VehicleType;

@Service
public class UnregisteredUserService implements IUnregisteredUserService{

	@Autowired
	private IVehicleTypeRepository vehicleTypeRepository;
	
	@Override
	public EstimationDTO getEstimation(CreateRideEstimationDTO dto) {
		Optional<VehicleType> vehicleType = vehicleTypeRepository.findVehicleTypeByName(dto.getVehicleType());
		if(vehicleType.isEmpty()) {
			return null;
		}
		
		double price = vehicleType.get().getPricePerKM() * dto.getRouteLength();
		long time = dto.getTravelTime();
		
		return new EstimationDTO(time, price);
	}

}
