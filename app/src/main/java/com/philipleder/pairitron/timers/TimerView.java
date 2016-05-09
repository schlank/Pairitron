package com.philipleder.pairitron.timers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philipleder.pairitron.R;


/**
 * Represents an individual cell for a TimerInfo object.
 */
public class TimerView extends FrameLayout {

    //region Constants -----------------------------------------------------------------------------
    //endregion

    //region Android Members -----------------------------------------------------------------------

    LinearLayout linearLayout;
    TextView headerTextView;
    TextView timeTextView;
    TextView footerTextView;
    FragmentManager fragmentManager;
    TimerEditDialog timerEditDialog;
    TimerEditDialog.TimerChangedListener timerChangedListener;

    //endregion

    //region State Members -------------------------------------------------------------------------

    TimerInfo timerInfo;
    Boolean startedCache;
    Boolean runningCache;

    //endregion

    //region Constructors --------------------------------------------------------------------------

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View mainView = inflate(context, R.layout.timer_view, this);
        mainView.setOnClickListener(new ClickListener());
        mainView.setOnLongClickListener(new LongPressListener());
        linearLayout = (LinearLayout) mainView.findViewById(R.id.timer_view_layout);
        headerTextView = (TextView) mainView.findViewById(R.id.timer_view_header);
        timeTextView = (TextView) mainView.findViewById(R.id.timer_view_time);
        footerTextView = (TextView) mainView.findViewById(R.id.timer_view_footer);
        timerChangedListener = new TimerEditDoneListener();
    }

    /**
     * Initialization constructor, because Android handles the construction lifecycle.
     *
     * @param timerInfo TimerInfo object that this View will control.
     */
    public void init(TimerInfo timerInfo, FragmentManager fragmentManager) {
        this.timerInfo = timerInfo;
        this.fragmentManager = fragmentManager;
        headerTextView.setText(timerInfo.header);
        timeTextView.setText(timerInfo.getTitle());
        footerTextView.setText(timerInfo.footer);
        updateColors();
    }

    //endregion

    //region Life Cycle ----------------------------------------------------------------------------
    //endregion

    //region Public --------------------------------------------------------------------------------

    /**
     * Refresh the display to correspond to the current state and time.
     */
    public void update() {
        timeTextView.setText(timerInfo.getTitle());
        if (startedCache == null || startedCache != timerInfo.started || runningCache == null || runningCache != timerInfo.running) {
            startedCache = timerInfo.started;
            runningCache = timerInfo.running;
            updateColors();
        }
        if (timerEditDialog != null) {
            timerEditDialog.updateTimer();
        }
    }

    //endregion

    //region Private -------------------------------------------------------------------------------

    /**
     * Update the background color of the button.
     */
    private void updateColors() {
        Resources resources = getResources();
        int textColor;
        Drawable backgroundDrawable;
        if (timerInfo.running) {
            //noinspection deprecation -- don't care, want to use on older APIs
            backgroundDrawable = resources.getDrawable(R.drawable.box_background_started);
            //noinspection deprecation -- don't care, want to use on older APIs
            textColor = resources.getColor(R.color.started_text);
        } else if (timerInfo.started) {
            //noinspection deprecation -- don't care, want to use on older APIs
            backgroundDrawable = resources.getDrawable(R.drawable.box_background_stopped);
            //noinspection deprecation -- don't care, want to use on older APIs
            textColor = resources.getColor(R.color.stopped_text);
        } else {
            //noinspection deprecation -- don't care, want to use on older APIs
            backgroundDrawable = resources.getDrawable(R.drawable.box_background_init);
            //noinspection deprecation -- don't care, want to use on older APIs
            textColor = resources.getColor(R.color.init_text);
        }
        //noinspection deprecation -- don't care, want to use on older APIs
        linearLayout.setBackgroundDrawable(backgroundDrawable);
        headerTextView.setTextColor(textColor);
        timeTextView.setTextColor(textColor);
        footerTextView.setTextColor(textColor);
    }

    //endregion

    //region Inner Classes -------------------------------------------------------------------------

    /**
     * Executed when the timer is clicked.
     */
    class ClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            timerInfo.startStop();
        }
    }

    /**
     * Executed when the timer is held down.
     */
    class LongPressListener implements OnLongClickListener {
        /**
         * Called when a view has been clicked and held.
         *
         * @param v The view that was clicked and held.
         * @return true if the callback consumed the long click, false otherwise.
         */
        @Override
        public boolean onLongClick(View v) {
            timerEditDialog = null;
            timerEditDialog = new TimerEditDialog(timerInfo);
            timerEditDialog.setTimerChangedListener(timerChangedListener);
            timerEditDialog.show(fragmentManager, "TimerEditDialog");
            return false;
        }
    }

    class TimerEditDoneListener implements TimerEditDialog.TimerChangedListener {
        @Override
        public void timerChanged() {
            headerTextView.setText(timerInfo.header);
            footerTextView.setText(timerInfo.footer);
        }
    }

    //endregion
}
