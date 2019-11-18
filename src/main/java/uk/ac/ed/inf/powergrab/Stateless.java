package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

/**
 * @author s1703367
 */
public class Stateless extends Drone {

    /**
     * The constructor of the Stateless drone.
     *
     * @param position  The initial position
     * @param droneType The drone type
     * @param stations  All the stations
     * @param rnd       The random seed
     */
    public Stateless(Position position, DroneType droneType, ArrayList<Station> stations, Random rnd) {
        super(position, droneType, stations, rnd);
    }


    /**
     * The stateless drone will only move to a positive station if it can reach that station with in one step.
     * If not, move randomly and avoid danger stations.
     *
     * @return An ArrayList of String, each String is in required output format.
     */
    @Override
    public ArrayList<String> play() {
        ArrayList<String> res = new ArrayList<>();
        while (this.gameStatus) {// If game is over, break the loop.
            StringBuilder sb = new StringBuilder();// create an string builder to store the current step's output.
            sb.append(this.position.getLatitude()).append(",").append(this.position.getLongitude()).append(",");
            Direction nextdir = null;
            int flag = 0;// The flag is used to remember whether there is a nearby good station.
            ArrayList<Direction> dangers = new ArrayList<>();// Store all directions which will cause an invalid move.
            for (Direction d : Direction.values()) {
                Position pos = this.position.nextPosition(d);
                if (!pos.inPlayArea())
                    continue;
                TreeMap<Double, Integer> nearstts = saveAndSort(pos);
                if (nearstts.size() == 0) {// if there are no nearby stations, search for next position.
                    continue;
                }
                flag = 1;// found nearby station(s), set flag to 1.
                Station nearest = this.stations.get(nearstts.firstEntry().getValue());//get the closest station.
                if (nearest.getSymbol() == Station.Symbol.LIGHTHOUSE && nearstts.firstEntry().getKey() <= Drone.CHARGE_RANGE) {
                    //If the closest station is a good station, move towards it and charge.
                    move(d);
                    charge();
                    nextdir = d;
                    break;// break to add result to the ArrayList.
                }
                if (nearest.getSymbol() == Station.Symbol.DANGER && nearstts.firstEntry().getKey() <= Drone.CHARGE_RANGE) {
                    //if the closest station is a danger one, record it and continue the for-loop.
                    dangers.add(d);
                }
                flag = 0;
            }
            if (flag == 0) {// if there is no nearby good station, randomly move to a safe position.
                while (true) {
                    nextdir = Direction.values()[rnd.nextInt(16)];
                    if (dangers.contains(nextdir) || !this.position.nextPosition(nextdir).inPlayArea()) continue;
                    move(nextdir);
                    charge();
                    break;
                }
            }
            sb.append(nextdir).append(",").append(this.position.getLatitude()).append(",").append(this.position.getLongitude()).append(",").append(this.remainCoins).append(",").append(this.remainPower);
            res.add(sb.toString());//add status of the drone after the move.
        }
        return res;
    }


    /**
     * Since the root of a TreeMap is the smallest value of the key, I use a TreeMap to sort all Station in the ascending order of
     * distance to the current position.
     * The method will first check if a station is roughly in range.
     *
     * @param pos The current position.
     * @return A TreeMap: Keys: the euclidean distance to all stations. Values : the index of the Station in the origin ArrayList of Station.
     */
    private TreeMap<Double, Integer> saveAndSort(Position pos) {
        TreeMap<Double, Integer> dists = new TreeMap<>();
        int idx = 0;
        for (Station each : this.stations) {
            if (inRange(pos, each))
                dists.put(euclidDist(each.getCorrdinate(), pos), idx);
            idx++;
        }
        return dists;
    }
}