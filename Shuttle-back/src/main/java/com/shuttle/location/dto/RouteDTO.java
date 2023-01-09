package com.shuttle.location.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.util.SloppyMath;

import com.shuttle.location.Location;
import com.shuttle.location.Route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteDTO {
	public LocationDTO departure;
	public LocationDTO destination;
	
	public static List<RouteDTO> getRoutes(Route route) {
		List<Location> locations =route.getLocations();
		List<RouteDTO> routes = new ArrayList<RouteDTO>();
		
		for(int i = 1; i < locations.size(); ++i) {
			LocationDTO departure = new LocationDTO(locations.get(i-1));
			LocationDTO destination = new LocationDTO(locations.get(i));
			routes.add(new RouteDTO(departure, destination));
		}
		return routes;
	}
	
	public double getDistance() {
		return SloppyMath.haversinMeters(departure.getLatitude(), departure.getLongitude(), destination.getLatitude(), destination.getLongitude());
	}
}
