package com.ariel.guardian.library;

import com.ariel.guardian.library.db.ArielDatabaseInterface;
import com.ariel.guardian.library.pubnub.ArielPubNubInterface;

/**
 * Created by mikalackis on 14.10.16..
 */

public interface ArielInterface {

    ArielDatabaseInterface database();
    ArielPubNubInterface pubnub();

}
