package com.philipleder.pairitron.timers;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Recycler View Holder for a TimerView
 */
public class TimerViewHolder extends RecyclerView.ViewHolder {
    //region Constants -----------------------------------------------------------------------------
    //endregion

    //region Android Members -----------------------------------------------------------------------

    TimerView timerView;

    //endregion

    //region State Members -------------------------------------------------------------------------
    //endregion

    //region Constructors --------------------------------------------------------------------------

    public TimerViewHolder(View itemView) {
        super(itemView);
        timerView = (TimerView) itemView;
    }

    //endregion

    //region Life Cycle ----------------------------------------------------------------------------
    //endregion

    //region Public --------------------------------------------------------------------------------

    public TimerView getTimerView() {
        return timerView;
    }

    //endregion

    //region Private -------------------------------------------------------------------------------
    //endregion

    //region Inner Classes -------------------------------------------------------------------------
    //endregion

}
