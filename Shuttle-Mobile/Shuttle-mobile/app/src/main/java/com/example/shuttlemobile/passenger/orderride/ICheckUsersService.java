package com.example.shuttlemobile.passenger.orderride;

import com.example.shuttlemobile.user.dto.RidePassengerDTO;
import com.example.shuttlemobile.util.RetrofitUtils;
import com.example.shuttlemobile.util.Utils;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ICheckUsersService {
    @GET("/api/passenger/email")
    Call<RidePassengerDTO> getUser(@Query("email") String email);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Utils.ServerOrigin)
            .addConverterFactory(GsonConverterFactory.create())
            .client(RetrofitUtils.basicJsonJwtClient())
            .build();
    ICheckUsersService service = retrofit.create(ICheckUsersService.class);
}
