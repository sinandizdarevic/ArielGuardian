package com.ariel.guardian;

import com.ariel.guardian.command.Command;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.ariel.guardian.receivers.NetworkChangeReceiver;
import com.ariel.guardian.sync.PubNubCallback;
import com.ariel.guardian.receivers.BootReceiver;
import com.ariel.guardian.receivers.ariel.DeviceApplicationReceiver;
import com.ariel.guardian.receivers.PackageReceiver;
import com.ariel.guardian.receivers.ShutdownReceiver;
import com.ariel.guardian.services.ArielService;
import com.ariel.guardian.services.DeviceLocationJobService;
import com.ariel.guardian.sync.InstanceKeeperService;
import com.ariel.guardian.sync.SyncIntentService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by mikalackis on 23.8.16..
 */
@Singleton
@Component(modules={GuardianModule.class})
public interface GuardianComponent {

    void inject(PubNubCallback callback);

    //receivers
    void inject(ShutdownReceiver receiver);
    void inject(PackageReceiver receiver);
    void inject(BootReceiver receiver);
    void inject(DeviceApplicationReceiver receiver);
    void inject(NetworkChangeReceiver receiver);

    //services
    void inject(DeviceLocationJobService deviceLocationJobService);
    void inject(ArielService service);

    //Commands
    void inject(Command command);

    //DeviceApplication class
    void inject(GuardianApplication application);

    // pubnub
    void inject(ArielPubNub pubNub);

    // instance keeper
    void inject(InstanceKeeperService instanceKeeperService);

    // sync service
    void inject(SyncIntentService syncIntentService);


}
