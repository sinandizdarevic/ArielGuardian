package com.ariel.guardian.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

/**
 * Created by mikalackis on 2.6.16..
 */
public class PackageManagerUtilities {

    public static String getAppNameFromPackage(final Context context, final String packageName) {
        final PackageManager pm = context.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }

    public static List<ApplicationInfo> getInstalledApplications(final Context context){
        final PackageManager pm = context.getPackageManager();
        return pm.getInstalledApplications(PackageManager.GET_META_DATA);
    }

    public static void setApplicationEnabledState(final Context context, final String packageName, final boolean state) {
        if(state){
            context.getPackageManager().setApplicationEnabledSetting(packageName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
        }
        else{
            context.getPackageManager().setApplicationEnabledSetting(packageName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
        }
    }

    public static void killPackageProcess(final Context context, final String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(packageName);
//        Iterator<ActivityManager.RecentTaskInfo> it = am.getRecentTasks(100, ActivityManager.RECENT_WITH_EXCLUDED).iterator();
//        while(it.hasNext()){
//            ActivityManager.RecentTaskInfo task = it.next();
//            Log.i("PackageManagerUtil", "Recent package name: "+task.baseIntent.getComponent().getPackageName());
//            if(task.baseIntent.getComponent().getPackageName().contains(packageName)){
//                am.remo
//                break;
//            }
//        }
    }

//
//    public void enableAllReceivers() {
//        ComponentName bootReceiver = new ComponentName(getApplicationContext(),
//                BootReceiver.class);
//        getPackageManager().setComponentEnabledSetting(bootReceiver,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//        ComponentName smsReceiver = new ComponentName(getApplicationContext(),
//                AntiTheftSMSReceiver.class);
//        getPackageManager().setComponentEnabledSetting(smsReceiver,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//        final TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        tm.listen(new AntiTheftPhoneStateListener(), PhoneStateListener.LISTEN_SERVICE_STATE);
//    }
//
}
