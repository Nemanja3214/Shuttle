package com.shuttle.user.unregistered;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuttle.common.exception.NonExistantVehicleType;
import com.shuttle.ride.dto.CreateRideEstimationDTO;
import com.shuttle.ride.dto.EstimationDTO;
import com.shuttle.vehicle.vehicleType.IVehicleTypeRepository;
import com.shuttle.vehicle.vehicleType.VehicleType;

@Service
public class UnregisteredUserService implements IUnregisteredUserService{

	@Autowired
	private IVehicleTypeRepository vehicleTypeRepository;
	
	@Override
	public EstimationDTO getEstimation(CreateRideEstimationDTO dto) throws NonExistantVehicleType {
		Optional<VehicleType> vehicleType = vehicleTypeRepository.findVehicleTypeByNameIgnoreCase(dto.getVehicleType());
		if(vehicleType.isEmpty()) {
			throw new NonExistantVehicleType();
		}
		
		double price = vehicleType.get().getPricePerKM() * dto.calculateLength();
		long time = dto.getTravelTime();
		
		return new EstimationDTO(time, price);
	}
	
	

}
