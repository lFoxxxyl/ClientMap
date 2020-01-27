package com.client;

import com.client.models.Friends;
import com.client.models.Tokens;
import com.client.models.User;
import com.client.models.UserLocation;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerAPI {
    @FormUrlEncoded
    @POST("/user/create")
    public Call<ResponseBody> createUser(@Field("login") String login, @Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("/user/login")
    public Call<Tokens> loginUser(@Field("login") String login, @Field("password") String password);

    @GET("/user/search")
    public Call<List<User>> searchUser(@Header("Authorization") String accessToken, @Query("login") String login);

    @GET("user/logout")
    public Call<ResponseBody> logoutUser(@Header("Authorization") String accessToken);


    @FormUrlEncoded
    @POST("friends/request")
    public Call<ResponseBody> requestFriends (@Header("Authorization") String accessToken, @Field("login2") String login2);

    @FormUrlEncoded
    @POST("/user/refreshToken")
    public Call<Tokens> refreshToken (@Field("refreshToken") String refreshToken);

    @FormUrlEncoded
    @POST("/user/location/set")
    public Call<ResponseBody> setLocation (@Header("Authorization") String accessToken, @Field("latitude") Double latitude, @Field("longitude") Double longitude);

    @GET("/user/location/get")
    public Call<List<UserLocation>> getLocation (@Header("Authorization") String accessToken, @Query("logins") String[] logins);

    @GET("/friends/get")
    public Call<List<String>> getFriends (@Header("Authorization") String accessToken);

    @GET("/friends/requests/get")
    public Call<List<Friends>> getRequests(@Header("Authorization") String accessToken);

    @FormUrlEncoded
    @POST("friends/accept")
    public Call<ResponseBody> requestAccept (@Header("Authorization") String accessToken,@Field("login1") String login1, @Field("accept") boolean accept);


}
