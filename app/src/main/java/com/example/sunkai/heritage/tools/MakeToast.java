package com.example.sunkai.heritage.tools;

import android.widget.Toast;

import com.example.sunkai.heritage.Data.GlobalContext;

/**
 * Created by sunkai on 2017/12/29.
 */

public class MakeToast {
    public static void MakeText(String toastText){
        Toast.makeText(GlobalContext.Companion.getInstance(),toastText,Toast.LENGTH_SHORT).show();
    }
}
