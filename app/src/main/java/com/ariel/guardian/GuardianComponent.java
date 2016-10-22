package com.ariel.guardian;

import com.ariel.guardian.command.Command;
import com.ariel.guardian.library.pubnub.ArielPubNubCallback;
import com.ariel.guardian.receivers.BootReceiver;
import com.ariel.guardian.receivers.ariel.DeviceApplicationReceiver;
import com.ariel.guardian.receivers.PackageReceiver;
import com.ariel.guardian.receivers.ShutdownReceiver;
import com.ariel.guardian.services.ArielService;
import com.ariel.guardian.services.DeviceLocationJobService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by mikalackis on 23.8.16..
 */
@Singleton
@Component(modules={GuardianModule.class})
public interface GuardianComponent {

    void inject(ArielPubNubCallback callback);

    //receivers
    void inject(ShutdownReceiver receiver);
    void inject(PackageReceiver receiver);
    void inject(BootReceiver receiver);
    void inject(DeviceApplicationReceiver receiver);

    //services
    void inject(DeviceLocationJobService deviceLocationJobService);
    void inject(ArielService service);

    //Commands
    void inject(Command command);

    //DeviceApplication class
    void inject(GuardianApplication application);

}
