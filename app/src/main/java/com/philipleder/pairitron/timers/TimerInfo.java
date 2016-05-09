package com.philipleder.pairitron.timers;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * Base model for a single timer.
 */
public class TimerInfo implements Serializable {
    //region Constants -----------------------------------------------------------------------------
    //endregion

    //region Android Members -----------------------------------------------------------------------

    public double savedSeconds;
    public Date startTime;
    public boolean started;
    public boolean running;
    public String header;
    public String footer;

    //endregion

    //region State Members -------------------------------------------------------------------------
    //endregion

    //region Constructors --------------------------------------------------------------------------

    public TimerInfo() {
        clear();
    }

    //endregion

    //region Life Cycle ----------------------------------------------------------------------------
    //endregion

    //region Public --------------------------------------------------------------------------------

    /**
     * Start or Stop the current timer.  Like the button on a stopwatch.
     */
    public void startStop() {
        if (running) {
            running = false;
            if (startTime != null) {
                long elapsedTime = (new Date().getTime()) - startTime.getTime();
                savedSeconds += elapsedTime / 1000.0;
            }
            startTime = null;
        } else {
            running = true;
            started = true;
            startTime = new Date();
        }
    }

    /**
     * Reset the timer to 0.
     */
    public void reset() {
        savedSeconds = 0;
        startTime = null;
        started = false;
        running = false;
    }

    /**
     * Reset the timer and remove the headers.
     */
    public void clear() {
        reset();
        header = "";
        footer = "";
    }

    /**
     * Calculate the format for the display on the timer.
     * @return Formatted time string.
     */
    public String getTitle() {
        double seconds = savedSeconds;
        if (startTime != null) {
            long startTimeMs = startTime.getTime();
            long currentTime = System.currentTimeMillis();
            seconds += (currentTime - startTimeMs) / 1000.0;
        }
        if (seconds >= 60.0) {
            int minutes = (int) (seconds / 60.0);
            float remainderSeconds = (float) (seconds - (minutes * 60.0));
            if (minutes >= 60) {
                int hours = (int) (minutes / 60.0);
                minutes = minutes - (hours * 60);
                return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, (int) remainderSeconds);
            }
            return String.format(Locale.getDefault(), "%d:%04.1f", minutes, remainderSeconds);
        }
        if (seconds == 0.0 || !started) {
            return "";
        }
        return String.format(Locale.getDefault(), "%.1f", (float) seconds);
    }

    //endregion

    //region Private -------------------------------------------------------------------------------
    //endregion

    //region Inner Classes -------------------------------------------------------------------------
    //endregion


}
