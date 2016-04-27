package com.antyzero.smoksmog;

import android.app.Application;
import android.content.Context;
import com.antyzero.smoksmog.DaggerApplicationComponent;

public class SmokSmogWearApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule( new ApplicationModule( this ) ).build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public static SmokSmogWearApplication get( Context context ){
        return ( SmokSmogWearApplication ) context.getApplicationContext();
    }
}
