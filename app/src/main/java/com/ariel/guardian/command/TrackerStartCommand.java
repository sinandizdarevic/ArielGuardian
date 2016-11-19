package com.ariel.guardian.command;

/**
 * Created by mikalackis on 6.7.16..
 */
public class TrackerStartCommand extends Command {

    public TrackerStartCommand() {
        super(null);
    }

    @Override
    public void execute() {
        //GuardianApplication.getInstance().startService(DeviceTrackerService.getCallingIntent((LocationParams)params));
    }

//    @Override
//    public void injectComponent(GuardianComponent component) {
//        component.inject(this);
//    }
}
