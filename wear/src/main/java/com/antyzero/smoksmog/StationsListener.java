package com.antyzero.smoksmog;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.joda.time.DateTime;
import pl.malopolska.smoksmog.DateTimeDeserializer;
import pl.malopolska.smoksmog.model.Station;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StationsListener implements DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks {

    GoogleApiClient googleApiClient;
    public List<Station> stations = new ArrayList<>();
    private final StationsChangedListener lstnr;

    public StationsListener(Context context, StationsChangedListener lstnr) {
        initGoogleApiClient(context);
        googleApiClient.connect();
        this.lstnr = lstnr;
    }

    private void initGoogleApiClient(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .useDefaultAccount()
                .build();
    }

    public void connect() {
        googleApiClient.connect();
        Wearable.DataApi.addListener(googleApiClient, this);
    }

    public void disconnect() {
        Wearable.DataApi.removeListener(googleApiClient, this);
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("connected");
        System.out.println("connected");
        connect();

        Wearable.DataApi.getDataItems(googleApiClient).setResultCallback(new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(@NonNull DataItemBuffer dataItems) {
                for (DataItem item : dataItems) {
                    onDataItemChanged(item);
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("suspended");
    }

    private void onDataItemChanged(DataItem item) {
        if (item.getUri().getPath().compareTo("/stations") == 0) {
            DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

            String stationsJson = dataMap.getString("stations");
            Gson gson = createGson();

            Type listType = new TypeToken<ArrayList<Station>>() {
            }.getType();

            stations = gson.fromJson(stationsJson, listType);
        }

        if (lstnr != null) {
            lstnr.stationsChanged();
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        try {
            for (DataEvent evt : dataEventBuffer) {
                if (evt.getType() == DataEvent.TYPE_CHANGED) {
                    // DataItem changed
                    DataItem item = evt.getDataItem();
                    if (item.getUri().getPath().compareTo("/stations") == 0) {
                        onDataItemChanged(item);
                    }
                } else if (evt.getType() == DataEvent.TYPE_DELETED) {
                    // DataItem deleted
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Gson createGson() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeDeserializer());
        Converters.registerLocalDate(gsonBuilder);
        return gsonBuilder.create();
    }
}
