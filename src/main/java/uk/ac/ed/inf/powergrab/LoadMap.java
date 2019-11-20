package uk.ac.ed.inf.powergrab;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import com.mapbox.geojson.*;

/**
 * @author s1703367
 * @since 2019-10-25
 */
class LoadMap {
    private FeatureCollection fc;
    private final ArrayList<Station> stations;

    /**
     * The constructor will initialise the FeatureCollection to the .geojson file get from the URL connection.
     * @param mapURL The URL of the .geojson file.
     * @throws java.io.IOException
     */
    @SuppressWarnings("resource")
    LoadMap(URL mapURL) throws java.io.IOException {
        this.stations = new ArrayList<>();
        HttpURLConnection huc = (HttpURLConnection) mapURL.openConnection();
        huc.setReadTimeout(10000);
        huc.setConnectTimeout(15000);
        huc.setRequestMethod("GET");
        huc.setDoInput(true);
        huc.connect();
        InputStream is = huc.getInputStream();
        Scanner s = new Scanner(is).useDelimiter("\\A");
        String str = s.hasNext() ? s.next() : "";
        this.fc = FeatureCollection.fromJson(str);
    }

    ArrayList<Station> getStations(){
        return this.stations;
    }

    FeatureCollection getFc() {
        return fc;
    }

    /**
     * The method will get all features of a station and construct a Station class.
     * Then save all Station to an ArrayList.
     *
     * @throws java.lang.NullPointerException
     */
    void saveStations() throws  java.lang.NullPointerException{
        for (Feature each : this.fc.features()) {
            String id = each.getProperty("id").getAsString();
            double coins = each.getProperty("coins").getAsDouble();
            double power = each.getProperty("power").getAsDouble();
            Station.Symbol sym = each.getProperty("marker-symbol").getAsString().equals("lighthouse")
                    ? Station.Symbol.LIGHTHOUSE
                    : Station.Symbol.DANGER;
            Position pos = new Position(((Point) each.geometry()).coordinates().get(1),
                    ((Point) each.geometry()).coordinates().get(0));
            this.stations.add(new Station(id, coins, power, sym, pos));
        }
    }
}