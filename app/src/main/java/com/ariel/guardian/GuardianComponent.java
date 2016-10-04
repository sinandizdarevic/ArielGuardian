package com.ariel.guardian;

import com.ariel.guardian.command.Command;
import com.ariel.guardian.receivers.BootReceiver;
import com.ariel.guardian.receivers.NetworkChangeReceiver;
import com.ariel.guardian.receivers.PackageReceiver;
import com.ariel.guardian.receivers.ReportActionReceiver;
import com.ariel.guardian.receivers.ShutdownReceiver;
import com.ariel.guardian.services.ArielService;
import com.ariel.guardian.services.DeviceConfigService;
import com.ariel.guardian.services.DeviceFinderJobService;
import com.ariel.guardian.pubnub.PubNubService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by mikalackis on 23.8.16..
 */
@Singleton
@Component(modules={GuardianModule.class})
public interface GuardianComponent {

    //receivers
    void inject(ShutdownReceiver receiver);
    void inject(PackageReceiver receiver);
    void inject(BootReceiver receiver);
    void inject(ReportActionReceiver receiver);
    void inject(NetworkChangeReceiver receiver);

    //services
    void inject(DeviceFinderJobService deviceFinderJobService);
    void inject(PubNubService service);
    void inject(ArielService service);
    void inject(DeviceConfigService service);

    //Commands
    void inject(Command command);

    //Application class
    void inject(GuardianApplication application);

}
