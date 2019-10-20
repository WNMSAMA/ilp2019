package uk.ac.ed.inf.powergrab;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import com.mapbox.geojson.*;


public class PlayPowergrab {
    public static ArrayList<Point> getPoints(ArrayList<String> input){
        ArrayList<Point> res = new ArrayList<>();
        for(int i = 0 ; i < input.size()-1;i++) {
            String[] temp = input.get(i).split(",");
            if(i == 0) {
                Point p = Point.fromLngLat(Double.parseDouble(temp[1]), Double.parseDouble(temp[0]));
                res.add(p);
            }
            Point p = Point.fromLngLat(Double.parseDouble(temp[4]), Double.parseDouble(temp[3]));
            res.add(p);
        }
        return res;
    }
    public static String getNewGeoJson(FeatureCollection fc,ArrayList<Point> input) {
        LineString ls = LineString.fromLngLats(input);
        Feature f = Feature.fromGeometry(ls);
        ArrayList<Feature> temp = new ArrayList<>(fc.features());
        temp.add(f);
        FeatureCollection fcc = FeatureCollection.fromFeatures(temp);
        return fcc.toJson();
    }
    public static void main(String[] args){
        try {
            String link = String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson",
                    args[2], args[1], args[0]);
            URL url = new URL(link);
            LoadMap map = new LoadMap(url);
            Random rnd = new Random(Integer.parseInt(args[5]));
            map.saveStations();
            Drone drone;
            Position initpos = new Position(Double.parseDouble(args[3]),Double.parseDouble(args[4]));
            if(args[6].equals("stateless")) {
                drone = new Stateless(initpos,Drone.DroneType.STATELESS,map.getStations(),rnd);
            }else {
                drone = new Stateful(initpos,Drone.DroneType.STATEFUL,map.getStations(),rnd);
            }
            ArrayList<String> res = new ArrayList<>();
            res = drone.play();
            ArrayList<Point> points = getPoints(res);
            String strs = getNewGeoJson(map.getFc(),points);
            StringBuilder sb = new StringBuilder();
            for(String each : res) {
                sb.append(each);
                sb.append("\n");
            }
            
            FileOutputStream outputStream = new FileOutputStream(String.format("%s-%s-%s-%s.txt",args[6],args[0],args[1],args[2]));
            byte[] strToBytes = sb.toString().getBytes();
            outputStream.write(strToBytes);
            FileOutputStream outputStream2 = new FileOutputStream(String.format("%s-%s-%s-%s.geojson",args[6],args[0],args[1],args[2]));
            byte[] strToBytes2 = strs.toString().getBytes();
            outputStream2.write(strToBytes2);
            outputStream.close();
            outputStream2.close();
            
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
