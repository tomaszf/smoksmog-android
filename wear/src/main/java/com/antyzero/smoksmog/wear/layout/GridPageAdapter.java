package com.antyzero.smoksmog.wear.layout;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.FragmentGridPagerAdapter;
import com.antyzero.smoksmog.wear.StationDataFragment;
import com.antyzero.smoksmog.wear.StationListActivity;

public class GridPageAdapter extends FragmentGridPagerAdapter {

    private Fragment stationListFragment;
    private StationDataFragment stationDataFragment;

    public GridPageAdapter(FragmentManager fm) {
        super(fm);
        stationListFragment = new StationListActivity();
        stationDataFragment = new StationDataFragment();
    }

    @Override
    public Fragment getFragment(int i, int i1) {
        if (i1 == 1) {
            return stationListFragment;
        }
        if (i1 == 0) {
            return stationDataFragment;
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int i) {
        return 2;
    }
}
