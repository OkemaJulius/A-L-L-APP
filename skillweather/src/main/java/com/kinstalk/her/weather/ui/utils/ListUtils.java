package com.kinstalk.her.weather.ui.utils;

import java.util.Collection;

/**
 * Created by siqing on 17/9/23.
 */

public class ListUtils {

    public static boolean isEmpty(Collection list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        return false;
    }
}
