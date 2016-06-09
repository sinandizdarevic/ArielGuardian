package com.ariel.guardian.activity;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.R;
import com.ariel.guardian.utils.Utilities;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ariel.providers.ArielSettings;

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
                Intent i = new Intent(ArielGuardianApplication.getInstance(),UsageLogActivity.class);
                startActivity(i);
            }
        });

        Button btnStop = (Button) findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Device Unique ID: "+ Utilities.getUniquePsuedoID(), Toast.LENGTH_LONG).show();
                Log.i("Main activity","Device Unique ID: "+ Utilities.getUniquePsuedoID());
            }
        });

        Button btnReadState = (Button) findViewById(R.id.btn_read_state);
        btnReadState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int arielSystemStatus = ArielSettings.Secure.getInt(getContentResolver(),
                    ArielSettings.Secure.ARIEL_SYSTEM_STATUS, ArielSettings.Secure.ARIEL_SYSTEM_STATUS_NORMAL);
                Toast.makeText(MainActivity.this,"Ariel system status: "+arielSystemStatus,Toast.LENGTH_LONG).show();
                ArielSettings.Secure.putInt(getContentResolver(),ArielSettings.Secure.ARIEL_SYSTEM_STATUS,ArielSettings.Secure.ARIEL_SYSTEM_STATUS_NORMAL);
                arielSystemStatus = ArielSettings.Secure.getInt(getContentResolver(),
                        ArielSettings.Secure.ARIEL_SYSTEM_STATUS, ArielSettings.Secure.ARIEL_SYSTEM_STATUS_NORMAL);
                Toast.makeText(MainActivity.this,"Ariel system status NEW: "+arielSystemStatus,Toast.LENGTH_LONG).show();
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
