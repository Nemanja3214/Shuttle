package com.shuttle.location.dto;

import java.util.List;

import com.shuttle.user.dto.BasicUserInfoDTO;

import lombok.Data;

@Data
public class CreateFavouriteRouteDTO {
    private String favoriteName;
    private List<RouteDTO> locations;
    private List<BasicUserInfoDTO> passengers;
    private String vehicleType;
    private boolean babyTransport;
    private boolean petTransport;

}
