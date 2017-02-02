package com.example.andrey.p_databindingsample;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import com.example.andrey.p_databindingsample.databinding.CounterItemBinding;

import java.util.List;

import io.realm.Realm;

public class CounterItemRecyclerAdapter extends
        RecyclerView.Adapter<CounterItemRecyclerAdapter.CounterItemViewHolder> {
    private static final String TAG = "CounterItemAdapter";

    private Context mContext;
    private List<Counter> countersList;
    private LayoutInflater inflater;
    private static final long SHORT_VIBRATION_DURATION = 30;
    private static final long LONG_VIBRATION_DURATION = 60;
    private SharedPreferences settings;
    private Vibrator vibrator;
    private Realm realm = Realm.getDefaultInstance();

    private enum Sound {INCREMENT_SOUND, DECREMENT_SOUND}
    private SoundPool soundPool;
    private SparseIntArray soundsMap;

    public CounterItemRecyclerAdapter(Context mContext, List<Counter> countersList) {
        this.mContext = mContext;
        this.countersList = countersList;
    }

    @Override
    public CounterItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        settings = PreferenceManager.getDefaultSharedPreferences(mContext);

        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        soundsMap = new SparseIntArray(2);
        soundsMap.put(Sound.INCREMENT_SOUND.ordinal(), soundPool.load(mContext, R.raw.increment_sound, 1));
        soundsMap.put(Sound.DECREMENT_SOUND.ordinal(), soundPool.load(mContext, R.raw.decrement_sound, 1));

        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        CounterItemBinding binding = CounterItemBinding.inflate(inflater, parent, false);
        return new CounterItemViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(final CounterItemViewHolder holder, int position) {
        Counter counter = countersList.get(position);
        holder.binding.setCounter(counter);
        holder.binding.setClick(new DataChangedListener() {

            @Override
            public void onDecrementClick(View view) {
                vibrate(SHORT_VIBRATION_DURATION);
                playSound(Sound.DECREMENT_SOUND);
                realm.beginTransaction();
                int value = counter.getValue();
                counter.setValue(--value);
                realm.insertOrUpdate(counter);
                realm.commitTransaction();
                notifyItemChanged(position);
                Log.i(TAG, "onDecrement: " + String.valueOf(counter.getValue()));
            }

            @Override
            public void onIncrementClick(View view) {
                vibrate(SHORT_VIBRATION_DURATION);
                playSound(Sound.INCREMENT_SOUND);
                realm.beginTransaction();
                int value = counter.getValue();
                counter.setValue(++value);
                realm.insertOrUpdate(counter);
                realm.commitTransaction();
                notifyItemChanged(position);
                Log.i(TAG, "onIncrement: " + String.valueOf(counter.getValue()));
            }

            @Override
            public void onItemClick(View view) {
                vibrate(LONG_VIBRATION_DURATION);
                Log.i(TAG, "onItemClick: " + position);
                openListDialog(position, counter);
            }
        });
    }

    private void vibrate(long duration) {
        if (settings.getBoolean("vibrationOn", true)) {
            vibrator.vibrate(duration);
        }
    }

    private void playSound(Sound sound) {
        if (settings.getBoolean("soundsOn", false)) {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            soundPool.play(soundsMap.get(sound.ordinal()), volume, volume, 1, 0, 1f);
        }
    }

    private void openListDialog(int position, Counter counter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(R.array.edit_remove_dialog, (dialogInterface, i) -> {
            switch (i) {
                case 0:
                    Log.i(TAG, "Edit position " + position);
                    openEditDialog(position, counter);
                    break;
                case 1:
                    Log.i(TAG, "Delete position " + position);
                    realm.beginTransaction();
                    counter.deleteFromRealm();
                    realm.commitTransaction();
                    notifyDataSetChanged();
                    break;
            }
        });
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return countersList == null ? 0 : countersList.size();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        realm.close();
    }

    private void openEditDialog(int position, Counter counter) {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.edit_dialog, null);
        final EditText valueInput = (EditText) dialogView.findViewById(R.id.edit_value);
        valueInput.setText(String.valueOf(counter.getValue()));

        AlertDialog.Builder adb = new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setTitle(R.string.dialog_edit_counter)
                .setPositiveButton(R.string.edit, (dialog, which) -> {
                    String value = valueInput.getText().toString();
                    int value1;
                    if (!value.equals("")) {
                        value1 = Integer.parseInt(value);
                    } else {
                        value1 = Counter.DEFAULT_VALUE;
                    }
                    realm.beginTransaction();
                    counter.setValue(value1);
                    realm.insertOrUpdate(counter);
                    realm.commitTransaction();
                    notifyItemChanged(position);
                })
                .setNegativeButton(R.string.cancel, null);
        adb.create().show();
    }

    public static class CounterItemViewHolder extends RecyclerView.ViewHolder {
        private CounterItemBinding binding;

        public CounterItemViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}