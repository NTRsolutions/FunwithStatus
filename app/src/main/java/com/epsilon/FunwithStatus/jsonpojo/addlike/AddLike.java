package com.epsilon.FunwithStatus.jsonpojo.addlike;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddLike {
    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("data")
    @Expose
    public Object data;

}
