package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

public class Stateless extends Drone {
    private Random rnd;

    public Stateless(Position position, DroneType droneType, ArrayList<Station> stations, Random rnd) {
        super(position, droneType, stations);
        this.rnd = rnd;
    }

    @Override
    public ArrayList<String> play() {
        ArrayList<String> res = new ArrayList<>();
        while (true) {
            if (!this.gameStatus) {
                // res.add("Game over. Remaining coins " + this.remainCoins);
                break;
            }
            StringBuilder sb = new StringBuilder("");
            sb.append(this.position.getLatitude());
            sb.append(",");
            sb.append(this.position.getLongitude());
            sb.append(",");
            Direction nextdir = null;
            int flag = 0;
            ArrayList<Direction> dangers = new ArrayList<>();
            for (Direction d : Direction.values()) {
                Position pos = this.position.nextPosition(d);
                if (!pos.inPlayArea())
                    continue;
                TreeMap<Double, Integer> nearstts = saveAndSort(pos);
                if(nearstts.size() == 0) {
                    continue;
                }
                flag = 1;
                Station nearest = this.stations.get(nearstts.firstEntry().getValue());
                if (nearest.getSymbol() == Station.Symbol.LIGHTHOUSE && nearstts.firstEntry().getKey() <= 0.00025) {
                    move(d);
                    if (!this.gameStatus)
                        break;                       
                    charge(nearest);
                    nextdir = d;
                    break;
                }
                if(nearest.getSymbol() == Station.Symbol.DANGER && nearstts.firstEntry().getKey() <= 0.00025) {
                    dangers.add(d);
                }
                flag = 0;
            }
            if (flag == 0) {
                while (true) {
                    nextdir = Direction.values()[rnd.nextInt(16)];
                    if(dangers.contains(nextdir) || !this.position.nextPosition(nextdir).inPlayArea()) continue;
                    move(nextdir);
                    break;
                    
                }
            }

            sb.append(nextdir + ",");
            sb.append(this.position.getLatitude() + ",");
            sb.append(this.position.getLongitude() + ",");
            sb.append(this.remainCoins + ",");
            sb.append(this.remainPower);
            res.add(sb.toString());
        }
        return res;
    }

    

    public TreeMap<Double, Integer> saveAndSort(Position pos) {
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
