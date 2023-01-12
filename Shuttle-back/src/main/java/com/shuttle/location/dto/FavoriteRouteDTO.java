package com.shuttle.location.dto;

import java.util.List;

import com.shuttle.location.FavoriteRoute;
import com.shuttle.user.dto.BasicUserInfoDTO;

import lombok.Data;

@Data
public class FavoriteRouteDTO {
	private long id;
    private String favoriteName;
    private List<RouteDTO> locations;
    private List<BasicUserInfoDTO> passengers;
    private String vehicleType;
    private boolean babyTransport;
    private boolean petTransport;
    
    public static FavoriteRouteDTO from(FavoriteRoute favoriteRoute) {
    	FavoriteRouteDTO dto = new FavoriteRouteDTO();
    	dto.setId(favoriteRoute.getId());
    	dto.setBabyTransport(favoriteRoute.isBabyTransport());
    	dto.setFavoriteName(favoriteRoute.getFavoriteName());
    	dto.setLocations(RouteDTO.convertToRoutes(favoriteRoute.getLocations()));
    	List<BasicUserInfoDTO> usersInfo = favoriteRoute.getPassengers().stream().map(passenger -> BasicUserInfoDTO.from(passenger)).toList();
    	dto.setPassengers(usersInfo);
    	dto.setPetTransport(favoriteRoute.isPetTransport());
    	dto.setVehicleType(favoriteRoute.getVehicleType().getName());
    	return dto;
    }

}
