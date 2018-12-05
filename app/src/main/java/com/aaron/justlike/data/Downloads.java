package com.aaron.justlike.data;

import com.google.gson.annotations.SerializedName;

public class Downloads {

    @SerializedName("total")
    public String total;

    public String getTotal() {
        return total;
    }
}
