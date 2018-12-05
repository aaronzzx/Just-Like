package com.aaron.justlike.data;

import com.google.gson.annotations.SerializedName;

public class Likes {

    @SerializedName("total")
    public String total;

    public String getTotal() {
        return total;
    }
}
