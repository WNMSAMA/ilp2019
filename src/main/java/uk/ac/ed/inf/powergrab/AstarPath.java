package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author s1703367
 */
public class AstarPath {
    private final ArrayList<Station> bad;
    private final ArrayList<Station> good;

    /**
     * The constructor of AstarPath.
     * @param bad Danger stations.
     * @param good Lighthouse stations.
     */
    public AstarPath(ArrayList<Station> bad ,ArrayList<Station> good) {
        this.bad = bad;
        this.good = good;
    }

    /**
     * This method takes an input path, return an ArrayList of paths,
     * the first position of each path is the next valid position where the drone can move to.
     * @param poss A path(ArrayList of positions)
     * @return An ArrayList of paths.
     */
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

    /**
     * The estimated cost from x to y.
     *
     * @param x The current position
     * @param y The destination position
     * @return The estimated cost
     */
    private double hValue(Position x, Position y) {
        return Drone.euclidDist(x, y);
    }

    /**
     * The total cost so far.
     * @param poss The current path traveled.
     * @return The cost of the path.
     */
    private double cost(ArrayList<Position> poss) {
        return poss.size() * 0.000125;
    }

    /**
     * This method returns true if a given position is in the explored list.
     * @param explored The explored list.
     * @param p The position.
     * @return Whether the position p is in explored list.
     */
    private boolean checkInExplored(ArrayList<Position> explored, Position p) {
        for(Position pos : explored) {
            if(Double.compare(pos.getLatitude(),p.getLatitude()) == 0
                    && Double.compare(pos.getLongitude(),p.getLongitude()) == 0)
                return true;
        }
        return false;
    }

    /**
     * Check if the position is in rage of the Station.
     *
     * @param pos Current position.
     * @param s The destination station.
     * @return true if the position is in rage of the Station.
     */
    private boolean checkArrival(Position pos, Station s) {
        Station nearest = Stateful.findNearest(this.good,pos);
        return (Drone.euclidDist(pos, s.getCorrdinate()) <= Drone.CHARGE_RANGE) && (nearest.getId().equals(s.getId()));
    }

    /**
     * This method will return an ArrayList of Position,
     * which is the shortest path from start position to destination station.
     * Using A star searching strategy.
     *
     * @param start Start position.
     * @param s Destination station.
     * @return Best path of the drone.
     */
    public ArrayList<Position> findPath(Position start,Station s){
        ArrayList<ArrayList<Position>> open = new ArrayList<>();
        ArrayList<Position> explored = new ArrayList<>();
        ArrayList<Position> init = new ArrayList<>();
        init.add(start);
        open.add(init);
        while (open.size() != 0) {
            ArrayList<Position> track = open.get(0);//pick the path with lowest total cost.
            ArrayList<ArrayList<Position>> rest = new ArrayList<>(open);
            rest.remove(0);
            boolean b = false;
            // Check if the current node is already in other branches.
            for (ArrayList<Position> each : rest) {
                if (each.contains(track.get(0))) {
                    b = true;
                    break;
                }
            }
            if (checkArrival(track.get(0), s)) {// If arrives, return the best track.
                return track;
            } else if (checkInExplored(explored, track.get(0)) || b) {
                //if the node is explored or already in other path, remove it.
                open.clear();
                open.addAll(rest);
            } else {
                ArrayList<ArrayList<Position>> next = findNeighbors(track);
                next.forEach(arrs -> rest.add(0, arrs));
                explored.add(track.get(0));//add current node to the explored list.
                rest.sort((arg0, arg1) -> {// sort by total cost.
                    double f0 = hValue(arg0.get(0), s.getCorrdinate())+ cost(arg0);
                    double f1 = hValue(arg1.get(0), s.getCorrdinate())+ cost(arg1);
                    if (f0 - f1 < 0) return -1;
                    else if (f0 - f1 > 0) return 1;
                    else return 0;
                });
                open.clear();
                open.addAll(rest);
            }
        }
        return null;
    }

}