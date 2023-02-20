package com.example.shuttlemobile.driver;

import com.example.shuttlemobile.passenger.dto.PassengerDTO;
import com.example.shuttlemobile.ride.IRideService;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.util.ListDTO;
import com.example.shuttlemobile.util.RetrofitUtils;
import com.example.shuttlemobile.util.Utils;
import com.example.shuttlemobile.vehicle.VehicleDTO;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IDriverService {
    @GET("/api/driver/{id}/vehicle")
    Call<VehicleDTO> getVehicle(@Path("id") Long driverId);
    @GET("/api/driver/{id}")
    Call<DriverDTO> getDriver(@Path("id") Long driverId);

    @PUT("/api/driver/{id}/vehicle")
    Call<VehicleDTO> updateVehicle(@Path("id") Long driverId, @Body VehicleDTO vehicleDTO);
    @PUT("/api/driver/{id}/request")
    Call<DriverDTO> updateDriver(@Path("id") Long driverId, @Body DriverDTO driverDTO);

    @GET("/api/driver/{id}/ride")
    Call<ListDTO<RideDTO>> getRides(@Path("id") Long driverId);

//    @GET("/api/driver/{id}")
//    Call<PassengerDTO> getDriver(@Path("id") Long driverId); // TODO: UserDTO?

    @GET("/api/driver/{id}/stats")
    Call<DriverStatsDTO> getStatistics(@Path("id") Long driverId, @Query("scope") String scope);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Utils.ServerOrigin)
            .addConverterFactory(GsonConverterFactory.create())
            .client(RetrofitUtils.basicJsonJwtClient())
            .build();
    IDriverService service = retrofit.create(IDriverService.class);


}
