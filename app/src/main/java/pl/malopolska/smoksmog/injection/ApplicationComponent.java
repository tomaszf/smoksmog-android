package pl.malopolska.smoksmog.injection;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;

import dagger.Component;
import pl.malopolska.smoksmog.location.GoogleApiClientModule;

@Component(
        modules = {
                ApplicationModule.class,
                GoogleApiClientModule.class
        }
)
public interface ApplicationComponent {

    Context context();

    GoogleApiClient googleApiClient();
}
