package uk.me.peteharris.pintinyork.base.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public class Pub {
    @SerializedName("name")
    public String name;
    @SerializedName("lat")
    double latitude;
    @SerializedName("lon")
    double longitude;


    public Uri getAddressUri() {
        return new Uri.Builder()
                .scheme("geo")
                .authority(String.format(Locale.UK, "%f,%f", latitude, longitude))
                .appendQueryParameter("q", name)
                .build();
    }
}
