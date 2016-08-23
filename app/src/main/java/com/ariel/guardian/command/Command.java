package com.ariel.guardian.command;


import com.ariel.guardian.library.commands.Params;
import com.ariel.guardian.library.firebase.FirebaseHelper;

/**
 * Created by mikalackis on 6.7.16..
 */
public abstract class Command {

    public abstract void execute(final Params params);

    protected void reportCommand(String command){
        FirebaseHelper.getInstance().reportAction(command);
    }

}
