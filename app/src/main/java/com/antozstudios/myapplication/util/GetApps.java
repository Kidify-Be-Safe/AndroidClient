package com.antozstudios.myapplication.util;

import static android.content.Context.ACTIVITY_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.antozstudios.myapplication.activities.AppManagerActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetApps {

    private Context mContext;
    static List<String> appNames = new ArrayList<>();
    static List<String> appPackageNames = new ArrayList<>();

    static List<Drawable> appIcon = new ArrayList<>();

    private static  GetApps instance = null;





    private GetApps(Context context) {
        this.mContext = context;
    }

    public static GetApps getInstance(Context context){
        if(instance==null){
            instance = new GetApps(context);
        }

        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);


        for (ApplicationInfo packageInfo : packages) {
            //Checks if its a user app
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packageManager.getApplicationLabel(packageInfo).toString();
                    appNames.add(appName);
                appPackageNames.add(packageInfo.packageName);
                try {
                    appIcon.add(packageManager.getApplicationIcon(packageInfo.packageName));
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        return  instance;

    }


    public List<String> getAppNames() {
        return appNames;
    }

    public List<String> getAppPackageNames() {
        return appPackageNames;
    }

    public List<Drawable> getAppIcon() {
        return appIcon;
    }


    public  Drawable getIconFromPackage(Context context, String packageName){
        int i = 0;
        for(String s: appPackageNames){
            if(s.equals(packageName)){
               break;
            }
            i++;
        }
        return appIcon.get(i);
    }

    public  String getAppNameFromPackage(Context context, String packageName){
        int i = 0;
        for(String s: appPackageNames){
            if(s.equals(packageName)){
                break;
            }
            i++;
        }
        return appNames.get(i);
    }



    public  String getLauncherTopApp(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        UsageStatsManager usageStatsManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningTaskInfo> taskInfoList = manager.getRunningTasks(1);
            if (null != taskInfoList && !taskInfoList.isEmpty()) {
                return taskInfoList.get(0).topActivity.getPackageName();
            }
        } else {
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 10000;
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = usageStatsManager.queryEvents(beginTime, endTime);

            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.getPackageName();
                }
            }
            if (!TextUtils.isEmpty(result)){
                return result;
            }

        }
        return "";
    }


}
