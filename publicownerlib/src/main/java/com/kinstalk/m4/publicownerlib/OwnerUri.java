package com.kinstalk.m4.publicownerlib;

import android.net.Uri;

/**
 * Created by mamingzhang on 2017/4/21.
 */

public class OwnerUri {
    public static final String AUTHORITY = "com.kinstalk.her.ownerprovider";

    public static final String OWNER_PATH = "owner";

    public static final String LOCATION_PATH = "location";

    public static final Uri OWNER_URI = Uri.parse("content://" + AUTHORITY + "/" + OWNER_PATH);

    public static final Uri LOCATION_URI = Uri.parse("content://" + AUTHORITY + "/" + LOCATION_PATH);

}
