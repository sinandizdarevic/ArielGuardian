package com.ariel.guardian.activity;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ariel.guardian.R;
import com.ariel.guardian.library.database.model.DeviceApplication;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ariel.security.LockPatternUtilsHelper;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvSystemStatus = (TextView) findViewById(R.id.tv_system_status);
//        int arielSystemStatus = ArielSettings.Secure.getInt(getContentResolver(),
//                ArielSettings.Secure.ARIEL_SYSTEM_STATUS, ArielSettings.Secure.ARIEL_SYSTEM_STATUS_NORMAL);
//
//        tvSystemStatus.setText("Ariel system status: "+arielSystemStatus);

        Button btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DeviceApplication app = new DeviceApplication();
                app.setPackageName("com.google.sava");
                app.setAppName("Sava");
                app.setDisabled(false);


                //LockPatternUtilsHelper.performAdminLock("123qwe", GuardianApplication.getInstance());
                //ImageView imgView = (ImageView) findViewById(R.id.imageView);
//                try {
//                    //imgView.setImageBitmap(ArielLibrary.action().generateDeviceQRCode("SAVA_MIKALACKI"));
//                } catch (WriterException e) {
//                    e.printStackTrace();
//                }
            }
        });

        Button btnStop = (Button) findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //LockPatternUtilsHelper.clearLock(GuardianApplication.getInstance());
//                ApplicationParams ap = new ApplicationParams.ApplicationParamBuilder("com.example.android").build();
//                Gson gson = new Gson();
//                String json = gson.toJson(ap);
//                Log.i(TAG,"MainActivity", json);
            }
        });

        Button btnReadState = (Button) findViewById(R.id.btn_read_state);
        btnReadState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Command appUpdate = CommandProducer.getInstance().getApplicationCommand(ApplicationCommands.APPLICATION_UPDATE_COMMAND);
                //appUpdate.execute(new ApplicationCommands.ApplicationParamBuilder("net.trikoder.android.kurir").build());
//                ArielSettings.Secure.putInt(getContentResolver(),
//                        ArielSettings.Secure.ARIEL_SYSTEM_STATUS,
//                        ArielSettings.Secure.ARIEL_SYSTEM_STATUS_NORMAL);
                // LockPatternUtilsHelper.performAdminLock("123qwe", MainActivity.this);
                byte[] unlockPwd = LockPatternUtilsHelper.getUnlockPassword();
                if (unlockPwd != null && unlockPwd.length > 0) {
                    Log.i(TAG,"Lock password exists");
                } else {
                    Log.i(TAG,"No lock password exists");
                }

                byte[] unlockPattern = LockPatternUtilsHelper.getUnlockPattern();
                if (unlockPattern != null && unlockPattern.length > 0) {
                    Log.i(TAG,"Lock pattern exists");
                } else {
                    Log.i(TAG,"No lock pattern exists");
                }
            }
        });
    }

    private String getTopPackage() {
        long ts = System.currentTimeMillis();
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 1000, ts);
        if (usageStats == null || usageStats.size() == 0) {
            return "unknown";
        }
        Collections.sort(usageStats, new RecentUseComparator());
        return usageStats.get(0).getPackageName();
    }

    static class RecentUseComparator implements Comparator<UsageStats> {

        @Override
        public int compare(UsageStats lhs, UsageStats rhs) {
            return (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) ? -1 : (lhs.getLastTimeUsed() == rhs.getLastTimeUsed()) ? 0 : 1;
        }
    }
}
