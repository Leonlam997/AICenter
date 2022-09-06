package com.leon.aicenter.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.security.MessageDigest;
import java.util.Locale;

public class NRSign {

    private static final String TAG = "NRApps";
    private String signSHA256 = "";
    private Context context;

    private NRSign() {
    }

    @SuppressLint("StaticFieldLeak")
    private static volatile NRSign mInstance = null;

    private static NRSign getInstance() {
        if (mInstance == null) {
            synchronized (NRSign.class) {
                if (mInstance == null) {
                    mInstance = new NRSign();
                }
            }
        }
        return mInstance;
    }


    public static void init(Application application) {
        Log.d(TAG, "init(context)");
        NRSign.getInstance().context = application;
    }

    public static Boolean isSPC() {
        //签名
        if (getSignSHA256().equals("2D:37:0C:21:F5:DF:D5:53:D2:A7:96:31:4B:70:92:5F:B3:8A:DE:EF:90:86:4C:92:0B:BB:BB:12:88:7D:35:22")) {
            Log.d(TAG, "isSPC");
            return true;
        }
        return false;
    }

    public static Boolean isYHK() {
        //签名
        if (getSignSHA256().equals("FA:F0:03:FF:83:92:C0:2B:AE:3D:C4:CA:48:DA:10:7D:6F:89:7C:A4:5F:98:FB:79:63:93:03:22:51:57:FB:3D")) {
            Log.d(TAG, "isYHK");
            return true;
        }
        return false;
    }


    //获取签名的SHA256
    private static String getSignSHA256() {
        if (NRSign.getInstance().signSHA256 != null && NRSign.getInstance().signSHA256.length() > 0) {
            return NRSign.getInstance().signSHA256;
        }
        String res = "";
        Context context = NRSign.getInstance().context;
        if (context == null) {
            Log.e(TAG, "请先NRApps.init(Context context)");
            return res;
        }
        try {
            PackageInfo info = getPackageInfo(context.getPackageName());
            if (info != null) {
                byte[] cert = info.signatures[0].toByteArray();
                MessageDigest md = MessageDigest.getInstance("SHA256");
                byte[] publicKey = md.digest(cert);
                StringBuilder hexString = new StringBuilder();
                for (byte b : publicKey) {
                    String appendString = Integer.toHexString(0xFF & b)
                            .toUpperCase(Locale.US);
                    if (appendString.length() == 1)
                        hexString.append("0");
                    hexString.append(appendString);
                    hexString.append(":");
                }
                String result = hexString.toString();
                res = result.substring(0, result.length() - 1);
                NRSign.getInstance().signSHA256 = res;//缓存结果
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @SuppressLint("PackageManagerGetSignatures")
    private static PackageInfo getPackageInfo(String packageName) {
        //Log.d(TAG,"getPackageInfo_start");
        Context context = NRSign.getInstance().context;
        if (context == null) {
            Log.e(TAG, "请先NRApps.init(Context context)");
            return null;
        }
        if (packageName == null) return null;
        PackageInfo info = null;
        try {//增加同步块,防止Package manager has died问题
            synchronized (NRSign.class) {
                PackageManager packageManager = context.getPackageManager();
                if (packageManager != null) {
                    try {
                        info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);//类型PackageManager.GET_SIGNATURES,不要改,获取签名要用到
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //NRSign.getInstance().cachePackageInfoMaps.put(packageName,info);
                    //Log.d(TAG,"getPackageInfo_ok");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.d(TAG,"getPackageInfo_end");
        return info;
    }
}
