package com.example.shuttlemobile.review;

import com.example.shuttlemobile.ride.IRideService;
import com.example.shuttlemobile.util.RetrofitUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IReviewService {
    @POST("/api/review/{rideId}/vehicle")
    Call<ReviewDTO> leaveReviewVehicle(@Path("rideId") Long rideId, @Body ReviewSendDTO review);

    @POST("/api/review/{rideId}/driver")
    Call<ReviewDTO> leaveReviewDriver(@Path("rideId") Long rideId, @Body ReviewSendDTO review);

    @GET("/api/review/{rideId}")
    Call<List<ReviewPairDTO>> findByRide(@Path("rideId") Long rideId);

    IReviewService service = RetrofitUtils.retrofit.create(IReviewService.class);

}
