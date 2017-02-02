package com.example.andrey.p_databindingsample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        initAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initAdapter();
    }

    private void initAdapter() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(new CounterItemRecyclerAdapter(this, findAllCounterObject()));
    }

    @NonNull
    private RealmResults<Counter> findAllCounterObject() {
        realm = Realm.getDefaultInstance();
        return realm.where(Counter.class).findAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                openAddDialog(this);
                return true;
            case R.id.menu_settings:
                Intent settingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(settingsActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openAddDialog(Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.add_dialog, null);
        final EditText nameInput = (EditText) dialogView.findViewById(R.id.add_name);
        final EditText valueInput = (EditText) dialogView.findViewById(R.id.add_value);

        AlertDialog.Builder adb = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setTitle(getString(R.string.add_counter))
                .setPositiveButton(getString(R.string.add), (dialog, which) -> {
                    String name = nameInput.getText().toString();
                    if (name.equals("")) {
                        Toast toast = Toast.makeText(context,
                                R.string.no_name_message, Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        String value = valueInput.getText().toString();
                        int value1;
                        if (!value.equals("")) {
                            value1 = Integer.parseInt(value);
                        } else {
                            value1 = Counter.DEFAULT_VALUE;
                        }
                        realm.beginTransaction();
                        Counter counter = realm.createObject(Counter.class, name);
                        counter.setValue(value1);
                        realm.commitTransaction();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null);
        adb.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {realm.close();}
    }
}