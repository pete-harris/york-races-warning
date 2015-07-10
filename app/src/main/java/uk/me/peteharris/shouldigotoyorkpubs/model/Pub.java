package uk.me.peteharris.shouldigotoyorkpubs.model;

import android.net.Uri;

import java.text.DateFormat;
import java.util.Date;

import lombok.Getter;

@Getter
public class Pub {
    String name;
    String address;

    public Uri getAddressUri() {
        return new Uri.Builder()
                .scheme("geo")
                .authority("0,0")
                .appendQueryParameter("q", String.format("%s, %s", name, address))
                .build();
    }
}
