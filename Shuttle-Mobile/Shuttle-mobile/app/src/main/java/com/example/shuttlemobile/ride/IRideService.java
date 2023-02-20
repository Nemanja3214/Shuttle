package com.example.shuttlemobile.ride;

import com.example.shuttlemobile.passenger.dto.FavoriteRouteDTO;
import com.example.shuttlemobile.passenger.dto.GraphEntryDTO;
import com.example.shuttlemobile.ride.dto.PanicDTO;
import com.example.shuttlemobile.ride.dto.RejectionDTOMinimal;
import com.example.shuttlemobile.review.ReviewDTO;
import com.example.shuttlemobile.review.ReviewSendDTO;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.util.RetrofitUtils;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IRideService {
    @GET("/api/ride/{id}")
    Call<RideDTO> getById(@Path("id") long rideId);

    @GET("/api/ride/driver/{driverId}/active")
    Call<RideDTO> getActiveRide(@Path("driverId") long driverId);

    @PUT("/api/ride/{rideId}/end")
    Call<RideDTO> endRide(@Path("rideId") long rideId);

    @GET("/api/ride/passenger/{id}/active")
    Call<RideDTO> getActiveRidePassenger(@Path("id") Long passengerId);

    @GET("/api/ride/driver/{id}/active")
    Call<RideDTO> getActiveRideDriver(@Path("id") Long driverId);

    @PUT("/api/ride/{id}/cancel")
    Call<RideDTO> rejectRide(@Path("id") Long rideId, @Body RejectionDTOMinimal rejectionDTOMinimal);

    @PUT("/api/ride/{id}/accept")
    Call<RideDTO> acceptRide(@Path("id") Long rideId);

    @PUT("/api/ride/{id}/start")
    Call<RideDTO> startRide(@Path("id") Long rideId);

    @PUT("/api/ride/{id}/panic")
    Call<RideDTO> panicRide(@Path("id") Long rideId, @Body PanicDTO panicDTO);

    @GET("/api/ride/favorites/passenger/{id}")
    Call<List<FavoriteRouteDTO>> getFavoriteRidesByPassenger(@Path("id") long passengerId);

    @DELETE("/api/ride/favorites/{id}")
    Call<Void> deleteFavoriteRoute(@Path("id") long id);

    IRideService service = RetrofitUtils.retrofit.create(IRideService.class);

    @POST("/api/ride")
    Call<RideDTO> createRide(@Body FavoriteRouteDTO favoriteRoute);

    @GET("/api/ride/graph/passenger/{passengerId}")
    Call<ArrayList<GraphEntryDTO>> getPassengerGraphData(@Path("passengerId") long passengerId, @Query("from") String start, @Query("to") String end);
    @GET("/api/ride/graph/driver/{driverId}")
    Call<ArrayList<GraphEntryDTO>> getDriverGraphData(@Path("driverId") long passengerId, @Query("from") String start, @Query("to") String end);

    @POST("/api/ride/favorites")
    Call<FavoriteRouteDTO> createFavoriteRoute(@Body FavoriteRouteDTO favorite);
}
