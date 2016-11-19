package com.ariel.guardian.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;

import com.ariel.guardian.GuardianApplication;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;



/**
 * Created by mikalackis on 20.7.16..
 */
public class CreateIFRuleService extends IntentService {

    private final String TAG = "CreateIFRuleService";

    public static final int ACTION_OK = 1;
    public static final int ACTION_FAILED = 2;

    public static final String EXTRA_PACKAGE_NAME = "package_name";
    public static final String EXTRA_PACKAGE_STATUS = "package_status";
    public static final String EXTRA_RESULT_RECEIVER = "receiver";

    private final String RULES_DIR = "/data/system/ifw";
    private final String RULE_FILE = "%s.xml";

    public CreateIFRuleService(){
        super("CreateIFRuleService");
    }

    public static Intent getCallingIntent(final String packageName, final boolean status, final ResultReceiver receiver){
        Intent serviceIntent = new Intent(GuardianApplication.getInstance(), CreateIFRuleService.class);
        serviceIntent.putExtra(CreateIFRuleService.EXTRA_PACKAGE_NAME, packageName);
        serviceIntent.putExtra(CreateIFRuleService.EXTRA_PACKAGE_STATUS, status);
        serviceIntent.putExtra(CreateIFRuleService.EXTRA_RESULT_RECEIVER, receiver);
        return serviceIntent;
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        String packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME);
        boolean status = intent.getBooleanExtra(EXTRA_PACKAGE_STATUS, false);

        ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);

        Logger.d("Received intent for "+packageName+" and status "+status);

        boolean result = false;

        if(status){
            // create a rule that will block the app with that package name
            result = createRuleFile(packageName);
        }
        else{
            result = removeRuleFile(packageName);
        }

        if(receiver!=null){
            if(result){
                receiver.send(ACTION_OK, null);
            } else{
                receiver.send(ACTION_FAILED, null);
            }
        }
    }

    private boolean createRuleFile(final String packageName){
        Logger.d("Creating rule file");
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
            return true;
        }
        catch(Exception e){
            Logger.d("Create rule file error");
            e.printStackTrace();
            return false;
        }
    }

    private boolean removeRuleFile(final String packageName){
        Logger.d("Removing rule file");
        try {
            File rulesDir = new File(RULES_DIR, String.format(RULE_FILE,packageName));
            rulesDir.delete();
            return true;
        }
        catch(Exception e){
            Logger.d("Removing rule file error");
            e.printStackTrace();
            return false;
        }
    }

}
