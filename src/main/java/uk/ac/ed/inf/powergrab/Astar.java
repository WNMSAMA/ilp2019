package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Astar {
    private ArrayList<Station> bad;

    public Astar(ArrayList<Station> bad) {
        this.bad = bad;
        
    }

    public ArrayList<ArrayList<Position>> findNeighbors(ArrayList<Position> poss) {
        ArrayList<ArrayList<Position>> res = new ArrayList<>();
        List<Direction> dirs = Arrays.asList(Direction.values());
        dirs.forEach(d -> {
            if (canReach(poss.get(0).nextPosition(d))) {
                ArrayList<Position> temp = new ArrayList<Position>(poss);
                temp.add(0,poss.get(0).nextPosition(d));
                res.add(temp);
            }
        });
        return res;
    }

    public boolean canReach(Position pos) {
        if (!pos.inPlayArea())
            return false;
        ArrayList<Station> rangebad = new ArrayList<>();
        bad.forEach(s -> {
            if (Drone.inRange(pos, s))
                rangebad.add(s);
        });
        if (rangebad.size() != 0) {
            for (Station each : rangebad) {
                if (Drone.euclidDist(pos, each.getCorrdinate()) <= 0.00025)
                    return false;
            }
        }
        return true;

    }
    public double hValue(Position x, Position y) {
        return Drone.euclidDist(x, y);
    }
    public double cost(ArrayList<Position> ps) {
        return 1.25*(ps.size()-1);
    }
    public boolean checkInExplored(ArrayList<Position> explored,Position p) {
        for(Position pos : explored) {
            if(Math.abs(pos.getLatitude()-p.getLatitude()) <= 0.00000001 && Math.abs(pos.getLongitude()-p.getLongitude()) <= 0.00000001)
                return true;
        }
        return false;
    }
    public boolean checkArrival(Position pos,Station s) {
        return Drone.euclidDist(pos, s.getCorrdinate()) <= 0.00025;
    }
    public ArrayList<Position> find(Station s,ArrayList<ArrayList<Position>> open , ArrayList<Position> explored){
        if(open.size() == 0) return null;
        ArrayList<Position> track = open.get(0);
        ArrayList<ArrayList<Position>> rest = new ArrayList<>(open);
        rest.remove(0);
        boolean b = false;
        for(ArrayList<Position> each : rest) {
            if(each.contains(track.get(0))) {
                b=true;
                break;
            }
        }
        if(checkArrival(track.get(0),s)) {
            return track;
        }
        else if(checkInExplored(explored,track.get(0)) || b) {
            return find(s,rest,explored);
        }
        else {
            ArrayList<ArrayList<Position>> next = findNeighbors(track);
            next.forEach(arrs -> rest.add(0,arrs));
            explored.add(track.get(0));
            Collections.sort(rest,new Comparator<ArrayList<Position>>() {

                @Override
                public int compare(ArrayList<Position> arg0, ArrayList<Position> arg1) {
                    double h0 = hValue(arg0.get(0),s.getCorrdinate());//+cost(arg0);
                    double h1 = hValue(arg1.get(0),s.getCorrdinate());//+cost(arg1);
                    if(h0-h1 < 0) return -1;
                    else if(h0-h1 > 0) return 1;
                    else return 0;
                }
                
            });
            return find(s,rest,explored);
        }
    }
    public ArrayList<Position> findPath(Position start, Station s) {
        ArrayList<ArrayList<Position>> init = new ArrayList<>();
        ArrayList<Position> initt = new ArrayList<>();
        initt.add(start);
        init.add(initt);
        ArrayList<Position> explored = new ArrayList<>();
        return find(s,init,explored);
    }
    
}
