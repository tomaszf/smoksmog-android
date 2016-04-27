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
import com.antyzero.smoksmog.wear.R;

public class StationListActivity extends Fragment implements WearableListView.ClickListener {

    @Bind(R.id.station_list_view)
    WearableListView stationList;



    private static String[] stations = {"Kurdwanow", "Nowa Huta"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState ) {
        View view = inflater.inflate(R.layout.activity_stations, container,false);
        ButterKnife.bind(this, view);
        stationList.setGreedyTouchMode(true);
        stationList.setAdapter(new StationListItemAdapter(getActivity(), stations));
        stationList.setClickListener(this);
        return view;
    }


    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        viewHolder.getItemId();
        GridViewPager gridViewPager = (GridViewPager) getActivity().findViewById(R.id.pager);
        gridViewPager.setCurrentItem(0,0);

    }

    @Override
    public void onTopEmptyRegionClick() {

    }
}
