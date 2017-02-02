package com.example.andrey.p_databindingsample;

import android.view.View;

public interface DataChangedListener {
    void onDecrementClick(View view);
    void onIncrementClick(View view);
    void onItemClick(View view);
}
