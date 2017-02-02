package com.example.andrey.p_databindingsample;


import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Counter extends RealmObject implements Observable, RealmDataBinding {

    public static final int DEFAULT_VALUE = 0;
    public static final int MIN_COUNT = -9999;
    public static final int MAX_COUNT = 99999;

    @PrimaryKey
    private String name;

    @Bindable
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        if (!isValid()) {
            notifyPropertyChanged(BR.name);
        }
    }

    private int value;

    @Bindable
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        if (!isValid()) {
            notifyPropertyChanged(BR.value);
        }
    }

    // FROM BASE OBSERVABLE
    @Ignore
    private transient PropertyChangeRegistry mCallbacks;

    // from observable interface
    @Override
    public synchronized void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        if (mCallbacks == null) {
            mCallbacks = new PropertyChangeRegistry();
        }
        mCallbacks.add(callback);
    }

    // from observable interface
    @Override
    public synchronized void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }

    @Override
    public synchronized void notifyChange() {
        if (mCallbacks != null) {
            mCallbacks.notifyCallbacks(this, 0, null);
        }
    }

    public void notifyPropertyChanged(int fieldId) {
        if (mCallbacks != null) {
            mCallbacks.notifyCallbacks(this, fieldId, null);
        }
    }
}