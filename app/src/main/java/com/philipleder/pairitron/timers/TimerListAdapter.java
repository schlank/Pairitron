package com.philipleder.pairitron.timers;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * RecyclerView Adapter for TimerInfos
 */
public class TimerListAdapter extends RecyclerView.Adapter<TimerViewHolder> {
    //region Constants -----------------------------------------------------------------------------
    //endregion

    //region Android Members -----------------------------------------------------------------------

    FragmentManager fragmentManager;

    //endregion

    //region State Members -------------------------------------------------------------------------

    List<TimerInfo> timerInfoList;

    //endregion

    //region Constructors --------------------------------------------------------------------------

    public TimerListAdapter(List<TimerInfo> timerInfoList, FragmentManager fragmentManager) {
        this.timerInfoList = timerInfoList;
        this.fragmentManager = fragmentManager;
    }

    //endregion

    //region Life Cycle ----------------------------------------------------------------------------

    @Override
    public TimerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = new TimerView(viewGroup.getContext(), null);
        return new TimerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TimerViewHolder timerViewHolder, int i) {
        timerViewHolder.getTimerView().init(timerInfoList.get(i), fragmentManager);
    }

    @Override
    public int getItemCount() {
        return timerInfoList.size();
    }

    //endregion

    //region Public --------------------------------------------------------------------------------
    //endregion

    //region Private -------------------------------------------------------------------------------
    //endregion

    //region Inner Classes -------------------------------------------------------------------------
    //endregion

}
