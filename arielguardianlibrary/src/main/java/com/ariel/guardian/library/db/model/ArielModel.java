package com.ariel.guardian.library.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

/**
 * Created by mikalackis on 23.10.16..
 */

public class ArielModel extends BaseModel {
    @JsonIgnore
    @Override
    public ModelAdapter getModelAdapter(){
        return super.getModelAdapter();
    }
}
