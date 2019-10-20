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
                //res.add("Game over. Remaining coins " + this.remainCoins);
                break;
            }
            StringBuilder sb = new StringBuilder("");
            sb.append(this.position.getLatitude());
            sb.append(",");
            sb.append(this.position.getLongitude());
            sb.append(",");
            Direction nextdir;
            while (true) {
                nextdir = Direction.values()[rnd.nextInt(16)];
                Position nextpos = this.position.nextPosition(nextdir);
                TreeMap<Double, Integer> dists = saveAndSort(nextpos);
                int i = dists.firstEntry().getValue();
                double dist = dists.firstEntry().getKey();
                Station stt = this.stations.get(i);
                if ((dist > 0.00025 || (dist <= 0.00025 && stt.getSymbol() == Station.Symbol.DEAD))
                        && nextpos.inPlayArea()) {
                    move(nextdir);
                    break;
                }
                if (nextpos.inPlayArea() && dist <= 0.00025 && stt.getSymbol() != Station.Symbol.DANGER) {
                    move(nextdir);
                    if (!this.gameStatus)
                        break;
                    charge(stt);
                    if(stt.getCoins() == 0 && stt.getPower() == 0)this.stations.remove(stt);
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
    public TreeMap<Double, Integer> saveAndSort(Position nextpos){
        TreeMap<Double, Integer> dists = new TreeMap<>();
        int idx = 0;
        for (Station each : this.stations) {
            dists.put(euclidDist(each.getCorrdinate(), nextpos), idx);
            idx++;
        }
        return dists;
    }
}
