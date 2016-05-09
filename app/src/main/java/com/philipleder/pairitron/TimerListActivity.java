package com.philipleder.pairitron;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.philipleder.pairitron.timers.TimerInfo;
import com.philipleder.pairitron.timers.TimerListAdapter;
import com.philipleder.pairitron.timers.TimerSave;
import com.philipleder.pairitron.timers.TimerViewHolder;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Show a list of timers that are selectable.
 */
public class TimerListActivity extends FragmentActivity {
    //region Constants -----------------------------------------------------------------------------

    static final String TAG = TimerListActivity.class.getSimpleName();
    static final String DEFAULT_SAVE_FILENAME = "timerboxes";
    static final int TIMER_BOX_COUNT = 6;

    //endregion

    //region Android Members -----------------------------------------------------------------------

    RecyclerView timerListRecycler;
    RecyclerView.LayoutManager timerListLayoutManager;
    RecyclerView.Adapter timerListAdapter;
    Timer refreshTimerDisplay;
    String mPairString = "pair";

    //endregion

    //region State Members -------------------------------------------------------------------------

    ArrayList<TimerInfo> timerInfoList;
    String savedJsonCache;
    private Spinner mPairSpinner;


    //endregion

    //region Constructors --------------------------------------------------------------------------
    //endregion

    //region Life Cycle ----------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_list);

        addListenerOnSpinnerItemSelection();

        timerInfoList = new ArrayList<>();

        if (savedInstanceState != null && savedInstanceState.containsKey("save")) {
            String json = savedInstanceState.getString("save");
            if (json != null) {
                timerInfoList = loadFile(json);
            }
        }
        if (timerInfoList == null || timerInfoList.size() == 0) {
            timerInfoList = loadFile();
        }
        if (timerInfoList == null || timerInfoList.size() == 0) {
            timerInfoList = new ArrayList<>();
            for (int i = 0; i < TIMER_BOX_COUNT; i++) {
                TimerInfo timerInfo = new TimerInfo();
                timerInfoList.add(timerInfo);
            }
        }

        float timerBoxSize = getResources().getDimension(R.dimen.timer_box_size);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float timersWide = displayMetrics.widthPixels / timerBoxSize;

        timerListRecycler = (RecyclerView) findViewById(R.id.activity_timer_timer_list);
        timerListAdapter = new TimerListAdapter(timerInfoList, getSupportFragmentManager());
        timerListRecycler.setAdapter(timerListAdapter);
        timerListLayoutManager = new GridLayoutManager(this, (int) timersWide);
        timerListRecycler.setLayoutManager(timerListLayoutManager);

        refreshTimerDisplay = new Timer("Refresh Timer");
        refreshTimerDisplay.schedule(new RefreshTimerDisplayTask(), 0, 100);
    }

    private void refreshUI() {

        timerInfoList = loadFile();
        if (timerInfoList == null || timerInfoList.size() == 0) {
            timerInfoList = new ArrayList<>();
            for (int i = 0; i < TIMER_BOX_COUNT; i++) {
                TimerInfo timerInfo = new TimerInfo();
                timerInfoList.add(timerInfo);
            }
        }
        timerListAdapter = new TimerListAdapter(timerInfoList, getSupportFragmentManager());
        timerListRecycler.setAdapter(timerListAdapter);
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        savedJsonCache = saveFile();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (savedJsonCache != null) {
            outState.putString("save", savedJsonCache);
            super.onSaveInstanceState(outState);
            savedJsonCache = null;
        }
    }

    //endregion

    //region Public --------------------------------------------------------------------------------

    public void addListenerOnSpinnerItemSelection() {
        mPairSpinner = (Spinner) findViewById(R.id.pair_spinner);
        mPairSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //endregion

    //region Private -------------------------------------------------------------------------------

    private String getPairName() {
        return mPairString.replace(" ", "");
    }

    private String getDateString() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(month) + "-" + String.valueOf(day) + "-" + String.valueOf(year);
    }

    private String getSaveFileName() {
        String fileName = getPairName() +"_"+ getDateString()+"."+DEFAULT_SAVE_FILENAME;;
        Toast.makeText(getApplicationContext(),
                "OnItemSelectedListener : " + mPairString,
                Toast.LENGTH_SHORT).show();
        return fileName;
    }

    /**
     * Loads the default save file.
     *
     * @return The list of timers found in file.
     */
    ArrayList<TimerInfo> loadFile() {
        ArrayList<TimerInfo> list = null;
        try {

            FileDescriptor fileDescriptor = getApplicationContext().openFileInput(getSaveFileName()).getFD();
            fileDescriptor.sync();
            FileReader fileReader = new FileReader(fileDescriptor);
            Gson gson = new GsonBuilder().create();
            TimerSave save = gson.fromJson(fileReader, TimerSave.class);
            fileReader.close();
            if (save != null && save.timers != null) {
                list = save.timers;
                Log.i(TAG, "Loaded " + save.timers.size() + " boxes from " + getSaveFileName());
            }
        } catch (FileNotFoundException ex) {
            // no problem, it's the first time they ran the app, or they just cleared cache
        } catch (IOException ex) {
            Log.w(TAG, "Trying to load from file " + getSaveFileName() + ", but couldn't.", ex);
        } catch (JsonSyntaxException ex) {
            Log.w(TAG, "Invalid JSON from file " + getSaveFileName(), ex);
        } catch (NullPointerException ex) {
            Log.w(TAG, "Trying to load from file " + getSaveFileName(), ex);
        }
        return list;
    }

    /**
     * Converts a JSON file into a list of Timers/
     *
     * @param json String contents of the JSON file to load.
     * @return List of timers that were in the JSON file.
     */
    ArrayList<TimerInfo> loadFile(String json) {
        ArrayList<TimerInfo> list = null;
        try {
            Gson gson = new GsonBuilder().create();
            TimerSave save = gson.fromJson(json, TimerSave.class);
            if (save != null && save.timers != null) {
                list = save.timers;
                Log.i(TAG, "Loaded " + save.timers.size() + " boxes from string.");
            }
        } catch (JsonSyntaxException ex) {
            Log.w(TAG, "Invalid JSON from string.", ex);
        } catch (NullPointerException ex) {
            Log.w(TAG, "Trying to load from string.", ex);
        }
        return list;
    }

    /**
     * Saves timers to the default save file.
     *
     * @return JSON encoded string containing file contents.
     */
    String saveFile() {
        TimerSave timerSave = new TimerSave();
        timerSave.app = "Timer Boxes";
        timerSave.version = "1.0";
        timerSave.timers = timerInfoList;
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(timerSave);
        try {
            FileDescriptor fd = getApplicationContext().openFileOutput(getSaveFileName(), MODE_PRIVATE).getFD();
            FileWriter fileWriter = new FileWriter(fd);
            fileWriter.write(json);
            fileWriter.close();
        } catch (FileNotFoundException ex) {
            Log.w(TAG, "File not found when opening new file, makes no sense.", ex);
        } catch (IOException ex) {
            Log.w(TAG, "Trying to save, but couldn't.", ex);
        } catch (NullPointerException ex) {
            Log.w(TAG, "Trying to save.", ex);
        }
        Log.i(TAG, "Saved file " + getSaveFileName());
        return json;
    }

    //endregion

    //region Inner Classes -------------------------------------------------------------------------

    class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

            mPairString = parent.getItemAtPosition(pos).toString();

            Toast.makeText(parent.getContext(),
                    "OnItemSelectedListener : " + mPairString,
                    Toast.LENGTH_SHORT).show();
            refreshUI();

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    /**
     * Refreshes the UI of individual timers so they can look like they are counting up.
     */
    class RefreshTimerDisplayTask extends TimerTask {
        @Override
        public void run() {
            if (timerListRecycler != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Use the child count which should be just the visible cells.
                        int count = timerListRecycler.getChildCount();
                        for (int i = 0; i < count; i++) {
                            View child = timerListRecycler.getChildAt(i);
                            TimerViewHolder timerViewHolder = (TimerViewHolder) timerListRecycler.getChildViewHolder(child);
                            timerViewHolder.getTimerView().update();
                        }
                    }
                });
            }
        }
    }

    //endregion


}
