package com.antyzero.smoksmog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import com.antyzero.smoksmog.layout.GridPageAdapter;
import com.antyzero.smoksmog.R;
import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.components.RxActivity;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import org.joda.time.DateTime;
import pl.malopolska.smoksmog.DateTimeDeserializer;
import pl.malopolska.smoksmog.SmokSmog;
import pl.malopolska.smoksmog.model.Station;
import rx.android.schedulers.AndroidSchedulers;
import smoksmog.logger.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends RxActivity implements WatchViewStub.OnLayoutInflatedListener,
        StationsChangedListener {

    @Inject
    SmokSmog smokSmog;
    @Inject
    Logger logger;

    @Bind(R.id.pager)
    GridViewPager gridViewPager;

    @Bind(com.antyzero.smoksmog.R.id.text)
    TextView text;

    StationsListener stationsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        stationsListener = new StationsListener(this, this);
        setContentView( R.layout.activity_pager );
        gridViewPager.setAdapter(new GridPageAdapter(getFragmentManager()));
        SmokSmogWearApplication.get(this).getApplicationComponent().inject(this);
        ((WatchViewStub) findViewById(R.id.watch_view_stub)).setOnLayoutInflatedListener(this);
    }

    @Override
    protected void onStop() {
        stationsListener.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        stationsListener.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stationsListener.disconnect();
    }

    @Override
    public void onLayoutInflated(WatchViewStub watchViewStub) {
        ButterKnife.bind(this, watchViewStub);
    }

    @Override
    public void stationsChanged() {
        text.setText(stationsListener.stations.toString());
    }
}
