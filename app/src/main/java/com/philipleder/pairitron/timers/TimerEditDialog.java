package com.philipleder.pairitron.timers;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import com.philipleder.pairitron.R;

/**
 * The popup for editing a single TimerInfo.
 */
public class TimerEditDialog extends DialogFragment {

    //region Constants -----------------------------------------------------------------------------
    //endregion

    //region Android Members -----------------------------------------------------------------------

    View mainView;
    AppCompatEditText headerText;
    AppCompatTextView timerText;
    AppCompatEditText footerText;
    ImageView startStopImageView;
    ImageView resetImageView;
    int colorBackgroundInit;
    int colorBackgroundStarted;
    int colorBackgroundStopped;
    int colorTextInit;
    int colorTextStarted;
    int colorTextStopped;
    int colorHintInit;
    int colorHintStarted;
    int colorHintStopped;

    //endregion

    //region State Members -------------------------------------------------------------------------

    TimerInfo timerInfo;
    TimerChangedListener timerChangedListener;

    //endregion

    //region Constructors --------------------------------------------------------------------------

    public TimerEditDialog() {
    }

    // Android team doesn't understand Dependency Injection
    @SuppressLint("ValidFragment")
    public TimerEditDialog(TimerInfo timerInfo) {
        this.timerInfo = timerInfo;
    }

    //endregion

    //region Life Cycle ----------------------------------------------------------------------------

    /**
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    @SuppressLint("InflateParams") // https://possiblemobile.com/2013/05/layout-inflation-as-intended/ #EveryRuleHasAnException
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (timerInfo == null) {
            // Android team doesn't understand Dependency Injection, so dismiss when
            // Android creates a rogue dialog. PREVENTS A CRASH
            AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
            dialog.dismiss();
            return dialog;
        }
        Resources resources = getResources();

        mainView = getActivity().getLayoutInflater().inflate(R.layout.dialog_timer_edit, null);
        headerText = (AppCompatEditText) mainView.findViewById(R.id.dialog_timer_edit_header);
        timerText = (AppCompatTextView) mainView.findViewById(R.id.dialog_timer_edit_time);
        footerText = (AppCompatEditText) mainView.findViewById(R.id.dialog_timer_edit_footer);
        startStopImageView = (ImageView) mainView.findViewById(R.id.dialog_timer_edit_start_stop);
        resetImageView = (ImageView) mainView.findViewById(R.id.dialog_timer_edit_reset);

        headerText.addTextChangedListener(new TimerStringWatcher(FIELD_TO_WATCH.HEADER));
        footerText.addTextChangedListener(new TimerStringWatcher(FIELD_TO_WATCH.FOOTER));
        startStopImageView.setOnClickListener(new StartStopClicked());
        resetImageView.setOnClickListener(new ResetClicked());

        //noinspection deprecation
        colorBackgroundInit = resources.getColor(R.color.init_background);
        //noinspection deprecation
        colorBackgroundStarted = resources.getColor(R.color.started_background);
        //noinspection deprecation
        colorBackgroundStopped = resources.getColor(R.color.stopped_background);
        //noinspection deprecation
        colorTextInit = resources.getColor(R.color.init_text);
        //noinspection deprecation
        colorTextStarted = resources.getColor(R.color.started_text);
        //noinspection deprecation
        colorTextStopped = resources.getColor(R.color.stopped_text);
        //noinspection deprecation
        colorHintInit = resources.getColor(R.color.init_text_hint);
        //noinspection deprecation
        colorHintStarted = resources.getColor(R.color.started_text_hint);
        //noinspection deprecation
        colorHintStopped = resources.getColor(R.color.stopped_text_hint);

        updateTimer();
        updateHeaders();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mainView);
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    //endregion

    //region Public --------------------------------------------------------------------------------

    /**
     * Update the inner time views with the data from the TimerInfo object.
     */
    public void updateTimer() {
        if (mainView == null) {
            return;
        }
        timerText.setText(timerInfo.getTitle());
        if (timerInfo.running) {
            setColorsStarted();
            startStopImageView.setImageResource(R.drawable.ic_pause_black_24dp);
        } else {
            startStopImageView.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            if (timerInfo.savedSeconds == 0.0) {
                setColorsInit();
            } else {
                setColorsStopped();
            }
        }
        if (!timerInfo.running && timerInfo.savedSeconds == 0.0) {
            resetImageView.setImageResource(R.drawable.ic_clear_black_24dp);
        } else {
            resetImageView.setImageResource(R.drawable.ic_restore_black_24dp);
        }
    }

    /**
     * Update the inner text views with data from the TimerInfo object.
     */
    public void updateHeaders() {
        headerText.setText(timerInfo.header);
        footerText.setText(timerInfo.footer);
    }

    /**
     * Subscribe to TimerEditDialog events.
     *
     * @param timerChangedListener Callback class.
     */
    public void setTimerChangedListener(TimerChangedListener timerChangedListener) {
        this.timerChangedListener = timerChangedListener;
    }

    //endregion

    //region Private -------------------------------------------------------------------------------

    void setColorsInit() {
        mainView.setBackgroundColor(colorBackgroundInit);
        headerText.setTextColor(colorTextInit);
        headerText.setHintTextColor(colorHintInit);
        timerText.setTextColor(colorTextInit);
        footerText.setTextColor(colorTextInit);
        footerText.setHintTextColor(colorHintInit);
    }

    void setColorsStarted() {
        mainView.setBackgroundColor(colorBackgroundStarted);
        headerText.setTextColor(colorTextStarted);
        headerText.setHintTextColor(colorHintStarted);
        timerText.setTextColor(colorTextStarted);
        footerText.setTextColor(colorTextStarted);
        footerText.setHintTextColor(colorHintStarted);
    }

    void setColorsStopped() {
        mainView.setBackgroundColor(colorBackgroundStopped);
        headerText.setTextColor(colorTextStopped);
        headerText.setHintTextColor(colorHintStopped);
        timerText.setTextColor(colorTextStopped);
        footerText.setTextColor(colorTextStopped);
        footerText.setHintTextColor(colorHintStopped);
    }

    //endregion

    //region Inner Classes -------------------------------------------------------------------------

    /**
     * TimerEditDialog public events.
     */
    public interface TimerChangedListener {
        /**
         * Executed when the edit dialog saved information back to the internal timerInfo object.
         */
        void timerChanged();
    }

    enum FIELD_TO_WATCH {
        HEADER,
        FOOTER,
    }

    /**
     * Change the appropriate timerInfo field.
     */
    class TimerStringWatcher implements TextWatcher {

        FIELD_TO_WATCH fieldToWatch;

        /**
         * @param fieldToWatch Which timerInfo field will change?
         */
        public TimerStringWatcher(FIELD_TO_WATCH fieldToWatch) {
            this.fieldToWatch = fieldToWatch;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            switch (fieldToWatch) {
                case HEADER:
                    timerInfo.header = s.toString();
                    break;
                case FOOTER:
                    timerInfo.footer = s.toString();
                    break;
            }
            if (timerChangedListener != null) {
                timerChangedListener.timerChanged();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    /**
     * When the start button is pressed.
     */
    class StartStopClicked implements ImageView.OnClickListener {
        @Override
        public void onClick(View v) {
            timerInfo.startStop();
            updateTimer();
        }
    }

    /**
     * When the reset button is pressed.
     */
    class ResetClicked implements ImageView.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!timerInfo.running && timerInfo.savedSeconds == 0.0) {
                timerInfo.clear();
                updateHeaders();
            } else {
                timerInfo.reset();
            }
            updateTimer();
        }
    }

    //endregion
}
