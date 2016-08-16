package com.ariel.guardian.command;


import com.ariel.guardian.library.firebase.FirebaseHelper;

import java.util.ArrayList;

import ariel.commands.Param;

/**
 * Created by mikalackis on 6.7.16..
 */
public abstract class Command {

    public abstract void execute(final ArrayList<Param> params);

    protected void reportCommand(String command){
        FirebaseHelper.getInstance().reportAction(command);
    }

}
