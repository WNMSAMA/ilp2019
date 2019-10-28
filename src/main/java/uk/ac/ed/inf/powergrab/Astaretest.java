package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Astaretest {
    public static void main(String[] args) {
        ArrayList<Station> bad = new ArrayList<>();
        Astar as = new Astar(bad);
        Station s = new Station("asd", 123, 324, Station.Symbol.LIGHTHOUSE, new Position(55.9436529,-3.1889163
                ));
        as.findPath(new Position(55.944425,-3.188396), s);
//        int dest = 50;
//        Position pos1 = new Position(50,50);
//        Comparator<ArrayList<Position>> c = new Comparator<ArrayList<Position>>() {
//
//            @Override
//            public int compare(ArrayList<Position> arg0, ArrayList<Position> arg1) {
//                double h0 = Drone.euclidDist(arg0.get(0), pos1);
//                double h1 = Drone.euclidDist(arg1.get(0), pos1);
////                ArrayList<Position> temp0 = new ArrayList<>(arg0);
////                ArrayList<Position> temp1 = new ArrayList<>(arg1);
////                temp0.remove(0);
////                temp1.remove(1);
////                double u0 = h0+cost(temp0);
////                double u1 = h1+cost(temp1);
//                if(h0-h1 < 0) return -1;
//                else if(h0-h1 > 0) return 1;
//                else return 0;
//            }
//            
//        };
//        ArrayList<Position> rest1 = new ArrayList<>();
//        rest1.add(new Position(49,49));
//        rest1.add(new Position(1324,3245));
//        rest1.add(new Position(4,4));
//
//        ArrayList<Position> rest2 = new ArrayList<>();
//        rest2.add(new Position(2345,2345));
//        rest2.add(new Position(50,50));
//        rest2.add(new Position(49,49));
//
//        ArrayList<Position> rest3 = new ArrayList<>();
//        rest3.add(new Position(345,454));
//        rest3.add(new Position(49,49));
//        rest3.add(new Position(49,49));
//
//        ArrayList<ArrayList<Position>> rest = new ArrayList<>();
//        rest.add(rest1);
//        rest.add(rest2);
//        rest.add(rest3);
//        Collections.sort(rest,c);
//        System.out.print(2);
    }
}
