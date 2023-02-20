package com.example.shuttlemobile.passenger;

import com.example.shuttlemobile.common.UserDTONoPassword;
import com.example.shuttlemobile.passenger.dto.PassengerDTO;
import com.example.shuttlemobile.ride.Ride;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.util.ListDTO;
import com.example.shuttlemobile.util.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IPassengerService {
    @GET("/api/passenger/{id}")
    Call<PassengerDTO> getPassenger(@Path("id") Long passengerId);

    IPassengerService service = RetrofitUtils.retrofit.create(IPassengerService.class);

    @PUT("/api/passenger/{id}")
    Call<PassengerDTO> updatePassenger(@Path("id") Long id, @Body PassengerDTO passenger);

    @POST("/api/passenger")
    Call<UserDTONoPassword> registerPassenger(@Body PassengerDTO passengerDTO);

    @GET("/api/passenger/{id}/ride")
    Call<ListDTO<RideDTO>> getRides(@Path("id") Long passengerId);
}
