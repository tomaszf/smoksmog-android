package com.antyzero.smoksmog;

import pl.malopolska.smoksmog.model.Station;

public class CurrentStation {

    public static volatile Station it = null;
    public static volatile int currentPollutant = 0;

}
