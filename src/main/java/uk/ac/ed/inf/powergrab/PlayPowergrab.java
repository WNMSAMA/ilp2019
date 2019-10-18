package uk.ac.ed.inf.powergrab;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class PlayPowergrab {
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
            for(String each:res) {
                System.out.println(each);
            }
        } catch (java.io.IOException e) {
            System.out.println("The input string is not a url.");
            e.printStackTrace();
        }
    }
}
