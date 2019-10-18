package uk.ac.ed.inf.powergrab;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import com.mapbox.geojson.*;

public class LoadMap {
    private URL mapURL;
    private FeatureCollection fc;
    private ArrayList<Station> stations;

    public LoadMap(URL mapURL) throws java.io.IOException {
        this.mapURL = mapURL;
        this.stations = new ArrayList<>();
        HttpURLConnection huc = (HttpURLConnection) mapURL.openConnection();
        huc.setReadTimeout(10000); // milliseconds
        huc.setConnectTimeout(15000); // milliseconds
        huc.setRequestMethod("GET");
        huc.setDoInput(true);
        huc.connect();
        InputStream is = huc.getInputStream();
        Scanner s = new Scanner(is).useDelimiter("\\A");
        String str = s.hasNext() ? s.next() : "";
        this.fc = FeatureCollection.fromJson(str);
    }
    public ArrayList<Station> getStations(){
        return this.stations;
    }

    public void saveStations() {
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
