package com.shuttle.location.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.shuttle.location.FavoriteRoute;
import com.shuttle.user.dto.BasicUserInfoDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRouteDTO {
	private Long id;
    private String favoriteName;
    private List<RouteDTO> locations;
    private List<BasicUserInfoDTO> passengers;
    private String vehicleType;
    private Boolean babyTransport;
    private Boolean petTransport;
    private String scheduledTime;
    
    public static FavoriteRouteDTO from(FavoriteRoute favoriteRoute) {
    	FavoriteRouteDTO dto = new FavoriteRouteDTO();
    	dto.setId(favoriteRoute.getId());
    	dto.setBabyTransport(favoriteRoute.getBabyTransport());
    	dto.setFavoriteName(favoriteRoute.getFavoriteName());
    	dto.setLocations(RouteDTO.convertToRoutes(favoriteRoute.getLocations()));
    	List<BasicUserInfoDTO> usersInfo = favoriteRoute.getPassengers().stream().map(passenger -> BasicUserInfoDTO.from(passenger)).toList();
    	dto.setPassengers(usersInfo);
    	dto.setPetTransport(favoriteRoute.getPetTransport());
    	dto.setVehicleType(favoriteRoute.getVehicleType().getName());
    	if(favoriteRoute.getScheduledTime() != null) {
    		dto.setScheduledTime(favoriteRoute.getScheduledTime().format(DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"))));
    	}
    	return dto;
    }

}
