package com.antyzero.smoksmog.ui;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.antyzero.smoksmog.R;
import com.antyzero.smoksmog.ui.utils.DimenUtils;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.ActivityLifecycleProvider;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * Base Activity that contains Dragon image and pollution background
 */
public abstract class BaseDragonActivity extends RxAppCompatActivity {

    private ImageView imageViewDragon;
    private ViewGroup container;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        super.setContentView( R.layout.activity_base );

        imageViewDragon = ( ImageView ) findViewById( R.id.imageViewDragon );
        container = ( ViewGroup ) findViewById( R.id.container );

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && addExtraTopPadding() ) {
            container.setPadding( 0, DimenUtils.getStatusBarHeight( this ), 0, 0 );
        }

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            int color;
            int colorResourceId = android.R.color.transparent;
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                color = getResources().getColor( colorResourceId, getTheme() );
            } else { //noinspection deprecation
                color = getResources().getColor( colorResourceId );
            }
            if ( getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT ) {
                getWindow().setNavigationBarColor( color );
            }
        }

        setupDragon();
    }

    /**
     * Override if needed, this solve issue with 4.4 status bar
     *
     * @return
     */
    protected boolean addExtraTopPadding() {
        return true;
    }

    @Override
    protected void attachBaseContext( Context newBase ) {
        super.attachBaseContext( CalligraphyContextWrapper.wrap( newBase ) );
    }

    @Override
    public void setContentView( @LayoutRes int layoutResID ) {
        LayoutInflater.from( this ).inflate( layoutResID, container, true );
        ButterKnife.bind( this );
    }

    @Override
    public void setContentView( View view ) {
        container.addView( view );
        ButterKnife.bind( this );
    }

    @Override
    public void setContentView( View view, ViewGroup.LayoutParams params ) {
        container.addView( view, params );
        ButterKnife.bind( this );
    }

    /**
     * Setup Dragon image position
     */
    private void setupDragon() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams( ( ViewGroup.MarginLayoutParams ) imageViewDragon.getLayoutParams() );
        layoutParams.bottomMargin = DimenUtils.getNavBarHeight( this, R.dimen.dragon_margin_bottom_default );
        layoutParams.addRule( RelativeLayout.ALIGN_PARENT_BOTTOM );
        layoutParams.addRule( RelativeLayout.ALIGN_PARENT_RIGHT );
        imageViewDragon.setLayoutParams( layoutParams );
        imageViewDragon.setVisibility( View.VISIBLE );
    }
}
