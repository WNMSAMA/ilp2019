package uk.ac.ed.inf.powergrab;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import com.mapbox.geojson.*;

/**
 * @author s1703367
 */
public class PlayPowergrab {
    /**
     * This method takes the output of a drone's track.
     * e.g.
     * 55.944212867965646,-3.1881838679656442,NW,55.944425,-3.188396,63.775421544612854,247.8231837318418
     * Then picks the position of each move and store them in an ArrayList.
     *
     * @param input This is the returned ArrayList from Drone.play() method which in required output format.
     * @return An ArrayList of Point(Geojson).
     */
    private static ArrayList<Point> getPoints(ArrayList<String> input){
        ArrayList<Point> res = new ArrayList<>();
        for(int i = 0 ; i < input.size();i++) {
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

    /**
     * This method takes a FeatureCollection and add LineString Feature to it.
     * The LineString is constructed by the out put of getPoints() method.()
     *
     * @param fc The old FeatureCollection which need to add a LineString Feature.
     * @param input The ArrayList of Point
     * @return A String in Geojson style.
     * @throws java.lang.NullPointerException
     */
    private static String getNewGeoJson(FeatureCollection fc, ArrayList<Point> input){
        LineString ls = LineString.fromLngLats(input);
        Feature f = Feature.fromGeometry(ls);
        ArrayList<Feature> temp = new ArrayList<>(fc.features());
        temp.add(f);
        FeatureCollection fcc = FeatureCollection.fromFeatures(temp);
        return fcc.toJson();
    }

    /**
     *The main function used to output files.
     * @param args The console inputs.
     */
    public static void main(String[] args){
        try {
            if(args.length != 7) throw new java.lang.IllegalArgumentException("Invalid length of input values!");
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
            }else if(args[6].equals("stateful")) {
                drone = new Stateful(initpos,Drone.DroneType.STATEFUL,map.getStations(),rnd);
            }
            else
                throw new java.lang.IllegalArgumentException("Invalid drone type.");
            ArrayList<String> res = drone.play();
            ArrayList<Point> points = getPoints(res);
            String geojsonmap = getNewGeoJson(map.getFc(),points);
            StringBuilder sb = new StringBuilder();
            for(String each : res) {
                sb.append(each);
                sb.append("\n");
            }
            FileOutputStream outputStream = (new FileOutputStream(
                    String.format("%s-%s-%s-%s.txt",args[6],args[0],args[1],args[2])));
            byte[] strToBytes = sb.toString().getBytes();
            outputStream.write(strToBytes);
            FileOutputStream outputStream2 = (new FileOutputStream(
                    String.format("%s-%s-%s-%s.geojson",args[6],args[0],args[1],args[2])));
            byte[] strToBytes2 = geojsonmap.getBytes();
            outputStream2.write(strToBytes2);
            outputStream.close();
            outputStream2.close();
        } catch(java.lang.NumberFormatException e){
            System.out.println("Input longitude or latitude or random seed is not a number.");
            e.printStackTrace();
        } catch(java.io.FileNotFoundException e){
            System.out.println("Input map cannot be found!");
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}