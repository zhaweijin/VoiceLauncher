package com.hiveview.dianshang.utils;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Patterns;
import android.view.WindowManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by carter on 4/10/17.
 */

public class Utils {

    private final static String tag ="Utils";
    private static SimpleDateFormat sf = null;

    /**
     * 打印测试
     * @param tag
     * @param value
     */
    public static void print(String tag,String value){
        Log.v(tag,value);
    }











    /**
     * 检测当前网络是否正常
     * @param context
     * @return
     */
    public static boolean networkIsConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (null != networkInfo && networkInfo.isAvailable() && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }




    /**
     * 根据包名启动
     * @param context
     * @param packageName
     */
    public static void startAppByPackage(Context context,String packageName){
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        // 这里如果intent为空，就说名没有安装要跳转的应用嘛
        if (intent != null) {
            context.startActivity(intent);
        } else {
            // 没有安装要跳转的app应用，提醒一下
            //ToastUtils.showToast(context,"找不app入口");
        }
    }

}
