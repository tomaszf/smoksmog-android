package com.antyzero.smoksmog;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.antyzero.smoksmog.R;
import pl.malopolska.smoksmog.model.Particulate;
import pl.malopolska.smoksmog.model.Station;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class StationDataFragment extends Fragment {

    @Bind(R.id.pollutant_name)
    TextView pollutantName;

    @Bind(R.id.pollutant_value)
    TextView pollutantValue;

    @Bind(R.id.station_name)
    TextView stationName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.station_data, container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentPollutant(new Random().nextInt());
            }
        });

        ButterKnife.bind(this, view);

        Timer timer = new Timer("hi");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Station station = CurrentStation.it;
                if (station != null) {
                    StationDataFragment.this.getActivity().runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    stationName.setText(station.getName());
                                    Particulate pollutant = station.getParticulates().get(0);
                                    pollutantName.setText(pollutant.getShortName());
                                    pollutantValue.setText(String.valueOf(pollutant.getValue()));
                                }
                            }
                    );
                }
            }
        }, 500L, 100L);

        return view;
    }

    public void setCurrentPollutant(int pollutantIndex) {
        pollutantName.setText("Poll " + pollutantIndex);
        pollutantName.setText("Value " + pollutantIndex);
    }

}
