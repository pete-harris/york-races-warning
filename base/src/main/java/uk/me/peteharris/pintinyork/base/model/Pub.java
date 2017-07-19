package uk.me.peteharris.pintinyork.base.model;

import android.net.Uri;

public class Pub {
    public String name;
    public String address;

    public Uri getAddressUri() {
        return new Uri.Builder()
                .scheme("geo")
                .authority("0,0")
                .appendQueryParameter("q", String.format("%s, %s", name, address))
                .build();
    }
}
