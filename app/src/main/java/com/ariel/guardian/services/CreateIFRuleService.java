package com.ariel.guardian.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.ariel.guardian.GuardianApplication;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by mikalackis on 20.7.16..
 */
public class CreateIFRuleService extends IntentService {

    private final String TAG = "CreateIFRuleService";

    public static final String EXTRA_PACKAGE_NAME = "package_name";
    public static final String EXTRA_PACKAGE_STATUS = "package_status";

    private final String RULES_DIR = "/data/system/ifw";
    private final String RULE_FILE = "%s.xml";

    public CreateIFRuleService(){
        super("CreateIFRuleService");
    }

    public static Intent getCallingIntent(final String packageName, final boolean status){
        Intent serviceIntent = new Intent(GuardianApplication.getInstance(), CreateIFRuleService.class);
        serviceIntent.putExtra(CreateIFRuleService.EXTRA_PACKAGE_NAME, packageName);
        serviceIntent.putExtra(CreateIFRuleService.EXTRA_PACKAGE_STATUS, status);
        return serviceIntent;
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        String packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME);
        boolean status = intent.getBooleanExtra(EXTRA_PACKAGE_STATUS, false);

        Log.i(TAG, "Received intent for "+packageName+" and status "+status);

        if(status){
            // create a rule that will block the app with that package name
            createRuleFile(packageName);
        }
        else{
            removeRuleFile(packageName);
        }

    }

    private void createRuleFile(final String packageName){
        Log.i(TAG, "Creating rule file");
        try {
            File rulesDir = new File(RULES_DIR, String.format(RULE_FILE,packageName));
            FileOutputStream fos = new FileOutputStream(rulesDir);
            StringBuffer sb = new StringBuffer();
            sb.append("<rules>\n");
            sb.append("<activity block=\"true\" log=\"true\">\n");
            sb.append("<intent-filter />\n");
            sb.append("<component-filter name=\""+packageName+"/*\" />\n");
            sb.append("</activity>\n");
            sb.append("</rules>");
            fos.write(sb.toString().getBytes());
            fos.flush();
            fos.close();
        }
        catch(Exception e){
            Log.i(TAG, "Create rule file error");
            e.printStackTrace();
        }
    }

    private void removeRuleFile(final String packageName){
        Log.i(TAG, "Removing rule file");
        try {
            File rulesDir = new File(RULES_DIR, String.format(RULE_FILE,packageName));
            rulesDir.delete();
        }
        catch(Exception e){
            Log.i(TAG, "Removing rule file error");
            e.printStackTrace();
        }
    }

}
