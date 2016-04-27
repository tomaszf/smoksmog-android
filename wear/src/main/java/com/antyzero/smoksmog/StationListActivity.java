package com.antyzero.smoksmog;

import android.os.Bundle;
import android.app.Fragment;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.antyzero.smoksmog.layout.StationListItemAdapter;
import com.antyzero.smoksmog.R;
import pl.malopolska.smoksmog.model.Station;

import java.util.Collections;

public class StationListActivity extends Fragment implements WearableListView.ClickListener, StationsChangedListener {

    @Bind(R.id.station_list_view)
    WearableListView stationList;

    private StationsListener listener;
    StationListItemAdapter stationListItemAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_stations, container, false);
        ButterKnife.bind(this, view);
        stationList.setGreedyTouchMode(true);
        stationListItemAdapter = new StationListItemAdapter(getActivity(), Collections.emptyList());
        stationList.setAdapter(stationListItemAdapter);
        stationList.setClickListener(this);
        stationList.addOnCentralPositionChangedListener(new WearableListView.OnCentralPositionChangedListener() {
            @Override
            public void onCentralPositionChanged(int i) {
                Station station = listener.stations.get(i);
                CurrentStation.it = station;
            }
        });

        listener = new StationsListener(this.getActivity(), this);

        return view;
    }


    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        viewHolder.getItemId();
        GridViewPager gridViewPager = (GridViewPager) getActivity().findViewById(R.id.pager);
        gridViewPager.setCurrentItem(0, 0);
    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    @Override
    public void stationsChanged() {
        stationListItemAdapter.setmDataset(listener.stations);
    }
}
