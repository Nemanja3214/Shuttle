package com.example.shuttlemobile.user;

import com.example.shuttlemobile.message.MessageDTO;
import com.example.shuttlemobile.message.SendMessageDTO;
import com.example.shuttlemobile.user.dto.UserChatDataDTO;
import com.example.shuttlemobile.user.dto.UserEmailDTO;
import com.example.shuttlemobile.util.ListDTO;
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
import retrofit2.http.Query;

public interface IUserService {
    @GET("/api/user/{id}/active")
    Call<Boolean> getActive(@Path("id") Long id);

    @PUT("/api/user/{id}/active")
    Call<Boolean> setActive(@Path("id") Long id);

    @PUT("/api/user/{id}/inactive")
    Call<Boolean> setInactive(@Path("id") Long id);

    @POST("/api/user/{id}/message")
    Call<MessageDTO> sendMessage(@Path("id") Long id, @Body SendMessageDTO dto);

    @GET("/api/user/{id}/message")
    Call<ListDTO<MessageDTO>> getMessages(@Path("id") Long id);

    @GET("/api/user/{id}/message/data")
    Call<UserChatDataDTO> getChatData(@Path("id") Long id);

    @GET("/api/user/email")
    Call<UserEmailDTO> findByEmail(@Query("email") String email);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Utils.ServerOrigin)
            .addConverterFactory(GsonConverterFactory.create())
            .client(RetrofitUtils.basicJsonJwtClient())
            .build();
    IUserService service = retrofit.create(IUserService.class);


}
