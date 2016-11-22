package com.ariel.guardian.command;

import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.database.model.WrapperMessage;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.ariel.guardian.library.utils.ArielConstants;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.ariel.guardian.sync.SyncIntentService;
import com.google.gson.Gson;

import javax.inject.Inject;

/**
 * Created by mikalackis on 6.7.16..
 */
public class Command {

    protected WrapperMessage message = null;

    protected Command next = null;

    protected Gson mGson = null;

    @Inject
    ArielDatabase mArielDatabase;

    @Inject
    ArielPubNub mArielPubNub;

    @Inject
    GuardianApplication mApplication;

    @Inject
    ArielJobScheduler mArielJobScheduler;

    public Command(AbstractBuilder builder){

        GuardianApplication.getInstance().getGuardianComponent().inject(this);

        message = builder.message;
        next = builder.next;
        mGson = ArielUtilities.getGson();
    }

    public void setWrapperMessage(final WrapperMessage message){
        this.message = message;
    }

    protected void execute(){
        // to be overriden in each command implementation
    }

    protected void onNextCommand(){
        next.execute();
    }

    //public abstract void injectComponent(final GuardianComponent component);

    protected void reportToMaster(){
        if(message!=null) {
            message.setOriginalMessageType(message.getMessageType());
            message.setMessageType(ArielConstants.MESSAGE_TYPE.REPORT);
            message.setSender(ArielUtilities.getUniquePseudoID());
            message.setExecuted(true);
            message.setSent(false);
            message.setReportReception(false);
            mArielDatabase.createWrapperMessage(message);
            mApplication.startService(SyncIntentService.getSyncIntent(message.getId()));
        }
    }

//    public static class CommandBuilder {
//
//        private Command next;
//        private WrapperMessage message;
//
//        public Command build(){
//            return new Command(this);
//        }
//
//        public CommandBuilder nextCommand(final Command next) {
//            this.next = next;
//            return this;
//        }
//
//        public CommandBuilder withMessage(final WrapperMessage message){
//            this.message = message;
//            return this;
//        }
//
//    }


    public static abstract class AbstractBuilder<BuilderT extends AbstractBuilder<BuilderT>> {

        private Command next;
        private WrapperMessage message;

        protected abstract BuilderT me();

        public abstract Command build();

        public BuilderT nextCommand(final Command next) {
            this.next = next;
            return me();
        }

        public BuilderT withMessage(final WrapperMessage message){
            this.message = message;
            return me();
        }

    }

}
