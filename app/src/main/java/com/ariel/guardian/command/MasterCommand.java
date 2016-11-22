package com.ariel.guardian.command;

import com.ariel.guardian.library.database.model.ArielMaster;

/**
 * Created by mikalackis on 6.7.16..
 */
public class MasterCommand extends Command {

    protected MasterCommand(AbstractBuilder builder) {
        super(builder);
    }

    @Override
    public void execute() {
        ArielMaster arielMaster = mGson.fromJson(message.getDataObject(), ArielMaster.class);
        mArielDatabase.createOrUpdateMaster(arielMaster);
        reportToMaster();
    }

    public static class MasterBuilder extends AbstractBuilder<MasterBuilder> {

        @Override
        protected MasterBuilder me() {
            return this;
        }

        @Override
        public MasterCommand build() {
            return new MasterCommand(me());
        }

    }

}
