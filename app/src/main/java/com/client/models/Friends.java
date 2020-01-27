package com.client.models;

import com.auth0.android.jwt.JWT;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Friends {

    @SerializedName("login1")
    @Expose
    private String login1;
    @SerializedName("login2")
    @Expose
    private String login2;
    @SerializedName("accept")
    @Expose
    private boolean accept;

    public String getLogin1() {
        return login1;
    }

    public void setLogin1(String login1) {
        this.login1 = login1;
    }

    public String getLogin2() {
        return login2;
    }

    public void setLogin2(String login2) {
        this.login2 = login2;
    }

    public boolean getAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

}
