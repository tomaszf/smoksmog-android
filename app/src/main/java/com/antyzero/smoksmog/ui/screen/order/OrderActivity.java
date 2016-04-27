package com.antyzero.smoksmog.ui.screen.order;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.View;

import com.antyzero.smoksmog.R;
import com.antyzero.smoksmog.SmokSmogApplication;
import com.antyzero.smoksmog.error.ErrorReporter;
import smoksmog.logger.Logger;
import com.antyzero.smoksmog.settings.SettingsHelper;
import com.antyzero.smoksmog.ui.BaseDragonActivity;
import com.antyzero.smoksmog.ui.screen.ActivityModule;
import com.antyzero.smoksmog.ui.screen.order.dialog.AddStationDialog;
import com.antyzero.smoksmog.ui.screen.order.dialog.StationDialogAdapter;
import com.antyzero.smoksmog.ui.utils.DimenUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import pl.malopolska.smoksmog.SmokSmog;
import pl.malopolska.smoksmog.model.Station;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static pl.malopolska.smoksmog.utils.StationUtils.convertStationsToIdsArray;
import static pl.malopolska.smoksmog.utils.StationUtils.convertStationsToIdsList;

public class OrderActivity extends BaseDragonActivity implements OnStartDragListener, StationDialogAdapter.StationListener,
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = OrderActivity.class.getSimpleName();
    private static final String EXTRA_DIALOG = "EXTRA_DIALOG";

    @Inject
    SmokSmog smokSmog;
    @Inject
    SettingsHelper settingsHelper;
    @Inject
    Logger logger;
    @Inject
    ErrorReporter errorReporter;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;

    private final List<Station> stationList = new ArrayList<>();
    private ItemTouchHelper itemTouchHelper;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGoogleApiClient();
        setContentView(R.layout.activity_order);

        if (getIntent() != null && getIntent().getBooleanExtra(EXTRA_DIALOG, false)) {
            showAddDialog();
        }

        setupFAB();
        setupNAvigationBar();

        recyclerView.setPadding(
                0, DimenUtils.getStatusBarHeight( this, R.dimen.nav_bar_height ),
                0, getResources().getDimensionPixelSize( R.dimen.item_air_quality_height ) * 3 );

        SmokSmogApplication.get( this )
                .getAppComponent()
                .plus( new ActivityModule( this ) )
                .inject( this );

        OrderAdapter adapter = new OrderAdapter(this, stationList, this, settingsHelper);

        recyclerView.setHasFixedSize( true );
        recyclerView.setAdapter( adapter );
        recyclerView.setLayoutManager( new LinearLayoutManager( this, VERTICAL, false ) );

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback( adapter );
        itemTouchHelper = new ItemTouchHelper( callback );
        itemTouchHelper.attachToRecyclerView( recyclerView );

        List<Long> stationIds = settingsHelper.getStationIdList();

        smokSmog.getApi().stations()
                .subscribeOn( Schedulers.newThread() )
                .flatMap( Observable::from )
                .filter( station -> stationIds.contains( station.getId() ) )
                .toSortedList( ( station, station2 ) -> stationIds.indexOf( station.getId() ) - stationIds.indexOf( station2.getId() ) )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe(
                        stations -> {
                            stationList.clear();
                            stationList.addAll( stations );
                            sendStationsToWearable();
                            recyclerView.getAdapter().notifyDataSetChanged();
                        },
                        throwable -> {
                            logger.w( TAG, "Unable to build list", throwable );
                        } );

    }

    private void setupFAB() {
        int margin = getResources().getDimensionPixelSize( R.dimen.margin_16 );

        LayoutParams params = new LayoutParams( WRAP_CONTENT, WRAP_CONTENT );

        int bottomMargin = margin;

        if ( getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ) {
            bottomMargin += DimenUtils.getNavBarHeight( this );
        }

        params.bottomMargin = bottomMargin;
        params.leftMargin = margin;
        params.rightMargin = margin;
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.anchorGravity = Gravity.BOTTOM | Gravity.END;
        params.setAnchorId( R.id.recyclerView );

        floatingActionButton.setLayoutParams( params );
    }

    @Override
    protected boolean addExtraTopPadding() {
        return false;
    }

    private void setupNAvigationBar() {
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN );
        if ( getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION );
        }
    }

    @OnClick( R.id.fab )
    void onClickFab() {
        showAddDialog();
    }

    private void showAddDialog() {
        AddStationDialog.show( getSupportFragmentManager(), convertStationsToIdsArray( stationList ) );
    }

    @Override
    public void onStartDrag( RecyclerView.ViewHolder viewHolder ) {
        itemTouchHelper.startDrag( viewHolder );
    }

    public static void start( Context context ) {
        start( context, false );
    }

    public static void start( Context context, boolean showDialog ) {
        Intent intent = new Intent( context, OrderActivity.class );
        intent.putExtra( EXTRA_DIALOG, showDialog );
        context.startActivity( intent );
    }

    private Gson createGson() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter( DateTime.class, new DateTimeDeserializer() );
        Converters.registerLocalDate( gsonBuilder );
        return gsonBuilder.create();
    }

    @Override
    public void onStation( long stationId ) {
        smokSmog.getApi().station( stationId )
                .subscribeOn( Schedulers.newThread() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe(
                        station -> {
                            stationList.add( station );
                            sendStationsToWearable();
                            recyclerView.getAdapter().notifyDataSetChanged();
                            settingsHelper.setStationIdList( convertStationsToIdsList( stationList ) );
                        },
                        throwable -> {
                            logger.e( TAG, "Unable to add station to station list", throwable );
                            errorReporter.report( R.string.error_unable_to_add_station );
                        }
                );
    }


    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .useDefaultAccount()
                .build();
    }

    void sendStationsToWearable() {
        try {
            Gson gson = createGson();
            String stationsJson = gson.toJson(stationList);
            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/stations");
            putDataMapReq.setUrgent();
            putDataMapReq.getDataMap().putString("stations", stationsJson);
            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            putDataReq.setUrgent();
            Wearable.DataApi.putDataItem(googleApiClient, putDataReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("onConnectionFailed " + connectionResult);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        System.out.println("onDataChanged " + dataEventBuffer);
    }
}
