package com.antyzero.smoksmog.wear;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

import java.util.Random;

public class StationDataFragment extends Fragment {

    @Bind(R.id.pollutant_name)
    TextView pollutantName;

    @Bind(R.id.pollutant_value)
    TextView pollutantValue;

    @Bind(R.id.station_name)
    TextView stationName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState ) {
        View view = inflater.inflate(R.layout.station_data, container,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentPollutant(new Random().nextInt());
            }
        });

        ButterKnife.bind(this, view);
        return view;
    }

    public void setCurrentPollutant(int pollutantIndex) {
        pollutantName.setText("Poll " + pollutantIndex);
        pollutantName.setText("Value " + pollutantIndex);
    }

}
