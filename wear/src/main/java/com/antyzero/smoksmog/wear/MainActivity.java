package com.antyzero.smoksmog.wear;

import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;
import android.widget.Toast;

import com.antyzero.smoksmog.wear.layout.GridPageAdapter;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.components.RxActivity;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.malopolska.smoksmog.SmokSmog;
import smoksmog.logger.Logger;


public class MainActivity extends RxActivity {

    @Inject
    SmokSmog smokSmog;

    @Bind(R.id.pager)
    GridViewPager gridViewPager;

    @Inject
    Logger logger;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pager );
        ButterKnife.bind(this);
        gridViewPager.setAdapter(new GridPageAdapter(getFragmentManager()));
        SmokSmogWearApplication.get( this ).getApplicationComponent().inject( this );
    }

}
