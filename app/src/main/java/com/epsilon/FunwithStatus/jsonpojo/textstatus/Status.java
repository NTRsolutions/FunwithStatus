package com.epsilon.FunwithStatus.jsonpojo.textstatus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Status {
    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("data")
    @Expose
    public StatusData data;
}
