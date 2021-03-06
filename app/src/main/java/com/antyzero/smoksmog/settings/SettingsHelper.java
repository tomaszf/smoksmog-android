package com.antyzero.smoksmog.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.antyzero.smoksmog.R;
import com.antyzero.smoksmog.permission.PermissionHelper;
import com.antyzero.smoksmog.utils.Convert;

import java.util.ArrayList;
import java.util.List;


/**
 * Working on settings helper.
 * <p>
 * This class should be responsible for data manipulation of every user-changeable data
 */
public class SettingsHelper implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsHelper.class.getSimpleName();

    private static final String EMPTY_STRING = "awd5ijsadf";
    private static final String SPLIT_CHAR = "%!@";
    private static final String KEY_STATION_ID_LIST = "KEY_STATION_ID_LIST";

    private final SharedPreferences defaultPreferences;

    private final List<Long> stationIds;

    private final Convert stringConvert = new Convert();
    private final Context context;

    private final String keyStationClosest;
    private final String keyPercent;

    private Percent percentMode;

    public SettingsHelper( Context context, PermissionHelper permissionHelper ) {
        this.context = context;

        PreferenceManager.setDefaultValues( context, R.xml.settings_general, false );
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences( context );
        defaultPreferences.registerOnSharedPreferenceChangeListener( this );
        stationIds = getList( defaultPreferences, KEY_STATION_ID_LIST, Long.class );

        keyStationClosest = context.getString( R.string.pref_key_station_closest );
        keyPercent = context.getString( R.string.pref_key_percent );

        if ( !permissionHelper.isGrantedLocationCorsare() ) {
            setClosesStationVisible( false );
        }

        updatePercentMode();
    }

    private void updatePercentMode() {
        updatePercentMode( defaultPreferences );
    }

    protected void updatePercentMode( SharedPreferences sharedPreferences ) {
        String defValue = context.getString( R.string.pref_percent_value_default );
        String string = sharedPreferences.getString( keyPercent, defValue );
        percentMode = Percent.find( context, string );
    }

    public SharedPreferences getPreferences() {
        return defaultPreferences;
    }

    public void setClosesStationVisible( boolean value ) {
        defaultPreferences.edit().putBoolean( keyStationClosest, value ).apply();
    }

    /**
     * Get info is closes station should be visible on main screen
     *
     * @return boolean value
     */
    public boolean isClosesStationVisible() {
        return defaultPreferences.getBoolean(
                context.getString( R.string.pref_key_station_closest ), false );
    }

    public Percent getPercentMode() {
        return percentMode;
    }

    /**
     * Get current station id list
     *
     * @return station id list
     */
    public List<Long> getStationIdList() {
        return stationIds;
    }

    /**
     * Update station list
     *
     * @param longList
     */
    public void setStationIdList( List<Long> longList ) {
        List<Long> longsTemp = new ArrayList<>( longList );
        stationIds.clear();
        if ( isClosesStationVisible() ) {
            stationIds.add( 0L );
        }
        stationIds.addAll( longsTemp );
        defaultPreferences.edit().putString( KEY_STATION_ID_LIST, TextUtils.join( SPLIT_CHAR, longList ) ).apply();
    }

    public String getKeyStationClosest() {
        return keyStationClosest;
    }

    /**
     * Converts string into list of objects of requested type
     *
     * @param sharedPreferences for data
     * @param key               which to get data from
     * @param type              of list
     * @param <T>               generic for list
     * @return list of requested type
     */
    @NonNull
    private <T> List<T> getList( SharedPreferences sharedPreferences, String key, Class<T> type ) {
        List<T> result = new ArrayList<>();
        String string = sharedPreferences.getString( key, EMPTY_STRING );
        if ( !TextUtils.isEmpty( string ) && !string.equalsIgnoreCase( EMPTY_STRING ) ) {
            String[] array = string.split( SPLIT_CHAR );
            for ( String item : array ) {
                result.add( ( T ) stringConvert.getConverterFor( type ).convert( item ) );
            }
        }
        return result;
    }

    @Override
    public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key ) {
        if ( key.equals( keyPercent ) ) {
            updatePercentMode( sharedPreferences );
        }
    }
}
