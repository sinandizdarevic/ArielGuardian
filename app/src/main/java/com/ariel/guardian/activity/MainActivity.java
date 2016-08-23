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

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.R;
import com.ariel.guardian.library.commands.application.ApplicationParams;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ariel.security.LockPatternUtilsHelper;

public class MainActivity extends AppCompatActivity {

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
                LockPatternUtilsHelper.performAdminLock("123qwe", GuardianApplication.getInstance());
            }
        });

        Button btnStop = (Button) findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LockPatternUtilsHelper.clearLock(GuardianApplication.getInstance());
                ApplicationParams ap = new ApplicationParams.ApplicationParamBuilder("com.example.android").build();
                Gson gson = new Gson();
                String json = gson.toJson(ap);
                Log.i("MainActivity", json);
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
                if(unlockPwd!=null && unlockPwd.length>0){
                    Log.i("MainActivity", "Lock password exists");
                }
                else{
                    Log.i("MainActivity", "No lock password exists");
                }

                byte[] unlockPattern = LockPatternUtilsHelper.getUnlockPattern();
                if(unlockPattern!=null && unlockPattern.length>0){
                    Log.i("MainActivity", "Lock pattern exists");
                }
                else{
                    Log.i("MainActivity", "No lock pattern exists");
                }
            }
        });
    }

    private String getTopPackage(){
        long ts = System.currentTimeMillis();
        UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts-1000, ts);
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
