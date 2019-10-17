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
    public LoadMap(URL mapURL) {
        this.mapURL = mapURL;
        try {
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
        catch(java.io.IOException e) {
            System.out.println("The input format is not a url");
            e.printStackTrace();
            }
    }
    public ArrayList<Point> getPoints() {
        ArrayList<Point> res = new ArrayList<>();
        this.fc.features().forEach(f -> res.add((Point)f.geometry()));
        return res;
    }
    public static void main(String[] args) throws java.net.MalformedURLException {
        URL url = new URL("http://homepages.inf.ed.ac.uk/stg/powergrab/2019/01/01/powergrabmap.geojson");
        LoadMap lm = new LoadMap(url);
        //System.out.println(lm.getMap());
        System.out.println(lm.getPoints());
    }
}
