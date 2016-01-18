package com.antyzero.smoksmog.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.antyzero.smoksmog.R;
import com.antyzero.smoksmog.SmokSmogApplication;
import com.antyzero.smoksmog.logger.Logger;

import javax.inject.Inject;

import pl.malopolska.smoksmog.SmokSmog;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link StationWidgetConfigureActivity StationWidgetConfigureActivity}
 */
public class StationWidget extends AppWidgetProvider {

    @Inject
    Logger logger;
    @Inject
    SmokSmog smokSmog;

    @Override
    public void onReceive( Context context, Intent intent ) {
        SmokSmogApplication.get( context ).getAppComponent().inject( this );
        super.onReceive( context, intent );
    }

    static void updateAppWidget( Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId ) {

        CharSequence widgetText = StationWidgetConfigureActivity.loadTitlePref( context, appWidgetId );
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.station_widget );
        views.setTextViewText( R.id.appwidget_text, widgetText );

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget( appWidgetId, views );
    }

    @Override
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds ) {
        // There may be multiple widgets active, so update all of them
        for ( int appWidgetId : appWidgetIds ) {
            updateAppWidget( context, appWidgetManager, appWidgetId );
        }
    }

    @Override
    public void onDeleted( Context context, int[] appWidgetIds ) {
        // When the user deletes the widget, delete the preference associated with it.
        for ( int appWidgetId : appWidgetIds ) {
            StationWidgetConfigureActivity.deleteTitlePref( context, appWidgetId );
        }
    }

    @Override
    public void onEnabled( Context context ) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled( Context context ) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

