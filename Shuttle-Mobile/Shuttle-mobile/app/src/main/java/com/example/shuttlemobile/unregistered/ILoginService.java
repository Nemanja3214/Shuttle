package com.example.shuttlemobile.unregistered;

import com.example.shuttlemobile.unregistered.login.LoginDTO;
import com.example.shuttlemobile.unregistered.login.TokenDTO;
import com.example.shuttlemobile.user.dto.PasswordResetDTO;
import com.example.shuttlemobile.util.RetrofitUtils;
import com.example.shuttlemobile.util.Utils;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ILoginService {
    @POST("/api/user/login")
    Call<TokenDTO> getUser(@Body LoginDTO loginDTO);

    @GET("/api/user/{id}/resetPassword")
    Call<Void> resetPassword(@Path("id") Long id);

    @PUT("/api/user/{id}/resetPassword")
    Call<Void> resetPassword(@Path("id") Long id, @Body PasswordResetDTO dto);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Utils.ServerOrigin)
            .addConverterFactory(GsonConverterFactory.create())
            .client(RetrofitUtils.basicJsonJwtClient())
            .build();
    ILoginService service = retrofit.create(ILoginService.class);
}
