package uk.me.peteharris.pintinyork.base.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class Pub {
    @SerializedName("name")
    public String name;
    @SerializedName("p")
    public String postcode;
    @SerializedName("lat")
    double latitude;
    @SerializedName("lon")
    double longitude;


    public Uri getAddressUri() {
        return new Uri.Builder()
                .scheme("geo")
                .authority(String.format("%f,%f", latitude, longitude))
                .appendQueryParameter("q", String.format("%s, %s", name, postcode))
                .build();
    }
}
