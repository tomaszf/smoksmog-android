package pl.malopolska.smoksmog.network;

/**
 * Station with location details
 */
public interface StationDetails extends Station {

    /**
     * Station latitude.
     *
     * @return float value
     */
    float getLatitude();

    /**
     * Station longitude.
     *
     * @return float value
     */
    float getLongitude();
}
