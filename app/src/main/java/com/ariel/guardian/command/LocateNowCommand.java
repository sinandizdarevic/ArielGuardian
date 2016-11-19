package com.ariel.guardian.command;

import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.command.params.LocationParams;
import com.ariel.guardian.services.DeviceLocationJobService;

import javax.inject.Inject;

/**
 * Created by mikalackis on 6.7.16..
 */
public class LocateNowCommand extends Command<LocationParams> {

    @Inject
    ArielJobScheduler mJobScheduler;

    public LocateNowCommand(AbstractBuilder builder) {
        super(builder);
        GuardianApplication.getInstance().getGuardianComponent().inject(this);
    }

    @Override
    public void execute() {
        mJobScheduler.registerNewJob(new DeviceLocationJobService(-1));
    }

    public static class LocateNowBuilder extends Command.AbstractBuilder<LocateNowBuilder, LocationParams> {

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
