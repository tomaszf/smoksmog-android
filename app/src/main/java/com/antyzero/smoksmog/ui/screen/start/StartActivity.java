package com.antyzero.smoksmog.ui.screen.start;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.antyzero.smoksmog.BuildConfig;
import com.antyzero.smoksmog.R;
import com.antyzero.smoksmog.SmokSmogApplication;
import com.antyzero.smoksmog.error.ErrorReporter;
import com.antyzero.smoksmog.logger.Logger;
import com.antyzero.smoksmog.ui.screen.ActivityModule;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import pl.malopolska.smoksmog.SmokSmog;
import pl.malopolska.smoksmog.model.Station;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Base activity for future
 */
public class StartActivity extends BaseDragonActivity implements ViewPager.OnPageChangeListener {

    private static final String TAG = StartActivity.class.getSimpleName();

    @Inject
    SmokSmog smokSmog;
    @Inject
    Logger logger;
    @Inject
    ErrorReporter errorReporter;

    @Bind( R.id.toolbar )
    Toolbar toolbar;
    @Bind( R.id.viewPager )
    ViewPager viewPager;

    final List<Long> stationIds = new ArrayList<>();

    {
        // First station is closest one, marked with -1 value (or just negative TODO?)
        stationIds.add( 0L );

        if ( BuildConfig.DEBUG ) {
            stationIds.add( 4L );
            stationIds.add( 12L );
        }
    }

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        SmokSmogApplication.get( this ).getAppComponent()
                .plus( new ActivityModule( this ) )
                .inject( this );

        setContentView( R.layout.activity_start );

        setSupportActionBar( toolbar );

        viewPager.setAdapter( new StationSlideAdapter( getSupportFragmentManager(), stationIds ) );
        viewPager.addOnPageChangeListener( this );
    }

    @Override
    public void onPageScrollStateChanged( int state ) {
        // do nothing
    }

    @Override
    public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels ) {
        // do nothing
    }

    @Override
    public void onPageSelected( int position ) {
        Long stationId = stationIds.get( position );

        if ( stationId > 0 ) {
            smokSmog.getApi().station( stationId )
                    .subscribeOn( Schedulers.newThread() )
                    .observeOn( AndroidSchedulers.mainThread() )
                    .subscribe( this::updateUITitle,
                            throwable -> {
                                logger.i( TAG, "Unable to load station data (stationID:" + stationId + ")", throwable );
                                errorReporter.report( R.string.error_unable_to_load_station_data, stationId );
                            } );
        }
    }

    /**
     * Update activity ActionBar title with station name
     *
     * @param station data
     */
    private void updateUITitle( Station station ) {
        Toast.makeText( this, station.getName(), Toast.LENGTH_SHORT ).show();
        getSupportActionBar().setTitle( "xxx" );
    }
}
