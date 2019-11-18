package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

// TODO: Auto-generated Javadoc

/**
 * The Class Stateless.
 */
public class Stateless extends Drone {

    /**
     * Instantiates a new stateless.
     *
     * @param position  the position
     * @param droneType the drone type
     * @param stations  the stations
     * @param rnd       the rnd
     */
    public Stateless(Position position, DroneType droneType, ArrayList<Station> stations, Random rnd) {
        super(position, droneType, stations, rnd);
    }

    /* (non-Javadoc)
     * @see uk.ac.ed.inf.powergrab.Drone#play()
     */
    @Override
    public ArrayList<String> play() {
        ArrayList<String> res = new ArrayList<>();
        while (this.gameStatus) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.position.getLatitude()).append(",").append(this.position.getLongitude()).append(",");
            Direction nextdir = null;
            int flag = 0;
            ArrayList<Direction> dangers = new ArrayList<>();
            for (Direction d : Direction.values()) {
                Position pos = this.position.nextPosition(d);
                if (!pos.inPlayArea())
                    continue;
                TreeMap<Double, Integer> nearstts = saveAndSort(pos);
                if (nearstts.size() == 0) {
                    continue;
                }
                flag = 1;
                Station nearest = this.stations.get(nearstts.firstEntry().getValue());
                if (nearest.getSymbol() == Station.Symbol.LIGHTHOUSE && nearstts.firstEntry().getKey() <= 0.00025) {
                    move(d);
                    charge();
                    if (!this.gameStatus)
                        break;
                    nextdir = d;
                    break;
                }
                if (nearest.getSymbol() == Station.Symbol.DANGER && nearstts.firstEntry().getKey() <= 0.00025) {
                    dangers.add(d);
                }
                flag = 0;
            }
            if (flag == 0) {
                while (true) {
                    nextdir = Direction.values()[rnd.nextInt(16)];
                    if (dangers.contains(nextdir) || !this.position.nextPosition(nextdir).inPlayArea()) continue;
                    move(nextdir);
                    charge();
                    break;

                }
            }
            sb.append(nextdir).append(",").append(this.position.getLatitude()).append(",").append(this.position.getLongitude()).append(",").append(this.remainCoins).append(",").append(this.remainPower);
            res.add(sb.toString());
        }
        return res;
    }


    /**
     * Save and sort.
     *
     * @param pos the pos
     * @return the tree map
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