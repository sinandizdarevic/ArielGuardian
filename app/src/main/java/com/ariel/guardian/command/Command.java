package com.ariel.guardian.command;

import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.GuardianComponent;
import com.ariel.guardian.library.commands.Params;
import com.ariel.guardian.library.firebase.FirebaseHelper;

import javax.inject.Inject;

/**
 * Created by mikalackis on 6.7.16..
 */
public abstract class Command {

    @Inject
    FirebaseHelper mFirebaseHelper;

    @Inject
    ArielJobScheduler mJobScheduler;

    public Command(){
        GuardianApplication.getInstance().getGuardianComponent().inject(this);
        //injectComponent(GuardianApplication.getInstance().getGuardianComponent());
    }

    public abstract void execute(final Params params);

    //public abstract void injectComponent(final GuardianComponent component);

    protected void reportCommand(String command){
        mFirebaseHelper.reportAction(command);
    }

}
