package com.shuttle.location;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuttle.location.dto.LocationDTO;

@Service
public class LocationService implements ILocationService {
	@Autowired
	private ILocationRepository locationRepository;
	
	@Override
	public Location findOrAdd(LocationDTO locationDTO) {
		final Optional<Location> ol = locationRepository.findByLatitudeAndLongitude(locationDTO.getLatitude(), locationDTO.getLongitude());
		
		if (ol.isEmpty()) {
			Location l = new Location();
			l.setAddress(locationDTO.getAddress());
			l.setLatitude(locationDTO.getLatitude());
			l.setLongitude(locationDTO.getLongitude());
			locationRepository.save(l);
			return l;
		} else {
			return ol.get();
		}
	}
}
