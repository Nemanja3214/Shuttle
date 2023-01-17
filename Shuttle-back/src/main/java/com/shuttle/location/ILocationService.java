package com.shuttle.location;

import com.shuttle.location.dto.LocationDTO;

public interface ILocationService {
	public Location findOrAdd(LocationDTO locationDTO);
}
