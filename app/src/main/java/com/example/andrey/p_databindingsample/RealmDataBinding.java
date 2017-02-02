package com.example.andrey.p_databindingsample;

public interface RealmDataBinding {
    interface Factory {
        DataChangedListener create();
    }

    void notifyChange();
}