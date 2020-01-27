package com.client.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.auth0.android.jwt.JWT;
import com.client.MainActivity;
import com.client.NetworkService;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class Tokens {

    @SerializedName("login")
    @Expose
    private String login;
    @SerializedName("accessToken")
    @Expose
    private String accessToken;
    @SerializedName("refreshToken")
    @Expose
    private String refreshToken;
    @SerializedName("error")
    @Expose
    private String error;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static int checkToken(String token)
    {
        JWT jwt = new JWT(token);
        Date expiresAt = jwt.getExpiresAt();
        Date now = new Date();
        return (int)(expiresAt.getTime()/1000-now.getTime()/1000);
    }

    public static void updateTokens(Context context){

        final String ACCESS_TOKEN = "accessToken";
        final String REFRESH_TOKEN = "refreshToken";
        SharedPreferences sharedPreferences = context.getSharedPreferences("preferences", MODE_PRIVATE);

            try {
                Response<Tokens> response = NetworkService.getInstance().getJSONApi().refreshToken(
                        sharedPreferences.getString(REFRESH_TOKEN, "")).execute();
                if (response.code()==200) {
                    Tokens tokens = response.body();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(ACCESS_TOKEN, tokens.getAccessToken());
                    editor.putString(REFRESH_TOKEN, tokens.getRefreshToken());
                    editor.apply();
                }

                if (response.code()==401){
                    sharedPreferences.edit().clear().apply();
                    Intent intent = new Intent(context, MainActivity.class);
                    ((Activity)context).finish();
                    context.startActivity(intent);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
