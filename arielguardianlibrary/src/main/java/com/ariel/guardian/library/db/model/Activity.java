package com.ariel.guardian.library.db.model;

import com.ariel.guardian.library.db.ArielDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by mikalackis on 5.10.16..
 */

@Table(database = ArielDatabase.class)
public class Activity extends ArielModel {

    @Column
    @PrimaryKey
    long id;

    /**
     * Action to be reported
     */
    @Column
    public String action;
    /**
     * Timestamp when the action was performed
     */
    @Column
    public long timestamp;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
