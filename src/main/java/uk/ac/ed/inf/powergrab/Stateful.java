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
    private final Astar astar;

    public Stateful(Position position, DroneType droneType, ArrayList<Station> stations, Random rnd) {
        super(position, droneType, stations);
        this.rnd = rnd;
        badStations = new ArrayList<>();
        for (Station each : this.stations) {
            if (each.getSymbol() == Station.Symbol.DANGER)
                badStations.add(each);
        }
        this.astar = new Astar(this.badStations);
    }

    public Station findNearest(ArrayList<Station> sts, Position dp) {
        Collections.sort(sts, new Comparator<Station>() {

            @Override
            public int compare(Station o1, Station o2) {
                Position p1 = o1.getCorrdinate();
                Position p2 = o2.getCorrdinate();
                double res1 = Drone.euclidDist(dp, p1);
                double res2 = Drone.euclidDist(dp, p2);
                if (res1 == res2)
                    return 0;
                return res1 < res2 ? -1 : 1;
            }

        });
        return sts.get(0);
    }

    public ArrayList<String> goStateless(Stateful sd) {
        Stateless stl = new Stateless(position, Drone.DroneType.STATELESS, badStations, rnd);
        stl.remainCoins = this.remainCoins;
        stl.remainPower = this.remainPower;
        stl.remainSteps = this.remainSteps;
        return stl.play();
    }

    @Override
    public ArrayList<String> play() {
        ArrayList<String> res = new ArrayList<>();
        ArrayList<Station> remaingood = new ArrayList<>();
        ArrayList<Station> again = new ArrayList<>();
        this.stations.forEach(s -> {
            if (s.getSymbol() == Station.Symbol.LIGHTHOUSE)
                remaingood.add(s);
        });
        while (true) {
            if (remaingood.size() == 0 && again.size() != 0) {
                remaingood.clear();
                remaingood.addAll(again);
                again.clear();
            }
            if (remaingood.size() == 0 && again.size() == 0) {
                break;
            }
            Station nearest = findNearest(remaingood, this.position);
            ArrayList<Position> ressss = astar.findPath(this.position, nearest);
            if(ressss == null) {
                remaingood.remove(nearest);
                continue;
            }
            if (ressss.size() <= 1) {
                remaingood.remove(nearest);
                again.add(nearest);
                continue;
            }
            Collections.reverse(ressss);         
            for (int i = 0; i < ressss.size() - 1; i++) {
                Direction dir = Position.nextDirection(ressss.get(i), ressss.get(i + 1));
                statefulMove(ressss.get(i + 1));           
                if (i == ressss.size() - 2) {
                    String s = String.format("%s,%s,%s,%s,%s", ressss.get(i), dir, ressss.get(i + 1), this.remainCoins,this.remainPower);
                    charge(nearest);
                    res.add(s);
                }else {
                String s = String.format("%s,%s,%s,%s,%s", ressss.get(i), dir, ressss.get(i + 1), this.remainCoins,this.remainPower);
                    res.add(s);}
                if (!this.gameStatus)
                    break;
            }

            remaingood.remove(nearest);
        }
        if (this.remainSteps != 0) {
            ArrayList<String> fin = goStateless(this);
            res.addAll(fin);
        }
        return res;
    }

}
