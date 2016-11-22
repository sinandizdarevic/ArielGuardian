package com.ariel.guardian.command;

import com.ariel.guardian.services.DeviceLocationJobService;

/**
 * Created by mikalackis on 6.7.16..
 */
public class LocateNowCommand extends Command {

    public LocateNowCommand(AbstractBuilder builder) {
        super(builder);
    }

    @Override
    public void execute() {
        mArielJobScheduler.registerNewJob(new DeviceLocationJobService(-1));
    }

    public static class LocateNowBuilder extends Command.AbstractBuilder<LocateNowBuilder> {

        @Override
        protected LocateNowBuilder me() {
            return this;
        }

        @Override
        public LocateNowCommand build() {
            return new LocateNowCommand(me());
        }

    }

}
