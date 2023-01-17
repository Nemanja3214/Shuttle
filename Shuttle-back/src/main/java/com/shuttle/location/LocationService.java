package com.shuttle.location;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuttle.location.dto.LocationDTO;

@Service
public class LocationService implements ILocationService {
	@Autowired
	private ILocationRepository locationRepository;
	
	@Override
	public Location findOrAdd(LocationDTO locationDTO) {
		final List<Location> li = locationRepository.findByLatitudeAndLongitude(locationDTO.getLatitude(), locationDTO.getLongitude());
		
		if (li.isEmpty()) {
			Location l = new Location();
			l.setAddress(locationDTO.getAddress());
			l.setLatitude(locationDTO.getLatitude());
			l.setLongitude(locationDTO.getLongitude());
			locationRepository.save(l);
			return l;
		} else {
			return li.get(0);
		}
	}
}
