package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Stateful extends Drone {
    private Random rnd;
    private ArrayList<Station> badStations;

    public Stateful(Position position, DroneType droneType, ArrayList<Station> stations, Random rnd) {
        super(position, droneType, stations);
        this.rnd = rnd;
        badStations = new ArrayList<>();
        for (Station each : this.stations) {
            if (each.getSymbol() == Station.Symbol.DANGER)
                badStations.add(each);
        }
    }

    public ArrayList<Direction> findDirs(ArrayList<Position> poss) {
        int idx = 0;
        ArrayList<Direction> res = new ArrayList<>();
        while (idx < poss.size() - 1) {
            Position prev = poss.get(idx);
            Position next = poss.get(idx + 1);
            res.add(Position.nextDirection(next,prev));
        }
        return res;
    }

    @Override
    public ArrayList<String> play() {
        ArrayList<String> res = new ArrayList<>();
        Astar as = new Astar(this.badStations);
        ArrayList<Station> remaingood = new ArrayList<>();
        this.stations.forEach(s -> {
            if (s.getSymbol() == Station.Symbol.LIGHTHOUSE)
                remaingood.add(s);
        });
        while (true) {
            if (remaingood.size() == 0) break;
            if (!this.gameStatus) {
                 res.add("Game over. Remaining coins " + this.remainCoins);
                break;
            }

            Collections.sort(remaingood, new Comparator<Station>() {

                @Override
                public int compare(Station s0, Station s1) {
                    double d1 = euclidDist(s0.getCorrdinate(), position);
                    double d2 = euclidDist(s1.getCorrdinate(), position);
                    if (d1 < d2)
                        return -1;
                    else if (d1 > d2)
                        return 1;
                    return 0;
                }

            });
            while (true) {
                Station s = remaingood.get(0);
                ArrayList<Position> track = as.findPath(this.position, s);
                if (track != null) {
                    ArrayList<Direction> dirs = findDirs(track);
                    for (int i = 0; i < dirs.size(); i++) {
                        StringBuilder sb = new StringBuilder("");
                        if (i == dirs.size() - 1) {
                            sb.append(this.position.getLatitude());
                            sb.append(",");
                            sb.append(this.position.getLongitude());
                            sb.append(",");
                            sb.append(dirs.get(i) + ",");
                            move(dirs.get(i));
                            sb.append(this.position.getLatitude() + ",");
                            sb.append(this.position.getLongitude() + ",");
                            if (!this.gameStatus) {
                                break;
                            }
                            charge(s);
                            sb.append(this.remainCoins + ",");
                            sb.append(this.remainPower);
                            res.add(sb.toString());
                            
                        }
                        sb.append(this.position.getLatitude());
                        sb.append(",");
                        sb.append(this.position.getLongitude());
                        sb.append(",");
                        sb.append(dirs.get(i) + ",");
                        move(dirs.get(i));
                        sb.append(this.position.getLatitude() + ",");
                        sb.append(this.position.getLongitude() + ",");
                        sb.append(this.remainCoins + ",");
                        sb.append(this.remainPower);
                        res.add(sb.toString());
                        if (!this.gameStatus) {
                            break;
                        }

                    }
                    break;
                }
                if(track == null) {
                    remaingood.remove(s);
                }

            }
            
        }
        return res;
    }

}
