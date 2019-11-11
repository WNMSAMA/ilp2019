package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;


public class Astaretest {
    public static void main(String[] args) {
        ArrayList<Station> bad = new ArrayList<>();
        bad.add(new Station("afdfsdaf",123,234,Station.Symbol.DANGER,new Position(55.9429,-3.1869)));
        Astar as = new Astar(bad);
        Station s = new Station("asd", 123, 324, Station.Symbol.LIGHTHOUSE, new Position(55.9428 ,-3.1847
                ));
        Station s2 = new Station("asd", 123, 324, Station.Symbol.LIGHTHOUSE, new Position(55.9434 ,-3.1893
                        ));
        
        ArrayList<Position> temp = as.findPath(new Position(55.9441 , -3.1883), s);
        Position new_pos = temp.get(0);
        ArrayList<Position> res = as.findPath(new_pos, s2);
        System.out.println(temp);
        System.out.print(res);

    }
}
