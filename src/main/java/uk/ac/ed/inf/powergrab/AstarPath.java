package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AstarPath {
    private final ArrayList<Station> bad;
    private final ArrayList<Station> good;

    public AstarPath(ArrayList<Station> bad ,ArrayList<Station> good) {
        this.bad = bad;
        this.good = good;
    }

    private ArrayList<ArrayList<Position>> findNeighbors(ArrayList<Position> poss) {
        ArrayList<ArrayList<Position>> res = new ArrayList<>();
        List<Direction> dirs = Arrays.asList(Direction.values());
        dirs.forEach(d -> {
            if (Drone.canReach(poss.get(0).nextPosition(d),this.bad,this.good)) {
                ArrayList<Position> temp = new ArrayList<>(poss);
                temp.add(0,poss.get(0).nextPosition(d));
                res.add(temp);
            }
        });
        return res;
    }


    private double hValue(Position x, Position y) {
        return Drone.euclidDist(x, y);
    }
    private double cost(ArrayList<Position> poss) {
        return poss.size() * 0.000125;
    }
    private boolean checkInExplored(ArrayList<Position> explored, Position p) {
        for(Position pos : explored) {
            if(Math.abs(pos.getLatitude()-p.getLatitude()) <= 1.0E-12d && Math.abs(pos.getLongitude()-p.getLongitude()) <= 1.0E-12d)
                return true;
        }
        return false;
    }
    private boolean checkArrival(Position pos, Station s) {
        Station nearest = Stateful.findNearest(this.good,pos);
        return (Drone.euclidDist(pos, s.getCorrdinate()) <= 0.00025) && (nearest.getId().equals(s.getId()));
    }

    public ArrayList<Position> findPath(Position start,Station s){
        ArrayList<ArrayList<Position>> open = new ArrayList<>();
        ArrayList<Position> explored = new ArrayList<>();
        ArrayList<Position> init = new ArrayList<>();
        init.add(start);
        open.add(init);
        while (open.size() != 0) {
            ArrayList<Position> track = open.get(0);
            ArrayList<ArrayList<Position>> rest = new ArrayList<>(open);
            rest.remove(0);
            boolean b = false;
            for (ArrayList<Position> each : rest) {
                if (each.contains(track.get(0))) {
                    b = true;
                    break;
                }
            }
            if (checkArrival(track.get(0), s)) {
                return track;
            } else if (checkInExplored(explored, track.get(0)) || b) {
                open.clear();
                open.addAll(rest);
            } else {
                ArrayList<ArrayList<Position>> next = findNeighbors(track);
                next.forEach(arrs -> rest.add(0, arrs));
                explored.add(track.get(0));
                rest.sort((arg0, arg1) -> {
                    double h0 = hValue(arg0.get(0), s.getCorrdinate())+ cost(arg0);
                    double h1 = hValue(arg1.get(0), s.getCorrdinate())+ cost(arg1);
                    if (h0 - h1 < 0) return -1;
                    else if (h0 - h1 > 0) return 1;
                    else return 0;
                });
                open.clear();
                open.addAll(rest);
            }
        }
        return null;
    }

}