package com.canli.oya.traininventoryfirebase.utils;

import android.text.TextUtils;

public final class DataBindingUtils {

    public static String[] splitLocation(String location) {
        return TextUtils.isEmpty(location) ? null : location.split("-");
    }

    public static String encloseInParanthesis(String category){
        return "(" + category + ")";
    }

    public static String addPoint(int number) {
        return number + ".";
    }
}
