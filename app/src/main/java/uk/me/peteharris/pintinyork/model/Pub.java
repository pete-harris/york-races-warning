package uk.me.peteharris.pintinyork.model;

import android.net.Uri;

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
