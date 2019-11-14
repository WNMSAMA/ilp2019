package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Stateful extends Drone {
    private final ArrayList<Station> badStations;
    private final ArrayList<Station> goodStations;
    private final GreedyPath greedypath;

    public Stateful(Position position, DroneType droneType, ArrayList<Station> stations, Random rnd) {
        super(position, droneType, stations,rnd);
        badStations = new ArrayList<>();
        this.goodStations = new ArrayList<>();
        for (Station each : this.stations) {
            if (each.getSymbol() == Station.Symbol.DANGER)
                badStations.add(each);
            if (each.getSymbol() == Station.Symbol.LIGHTHOUSE)
                goodStations.add(each);
        }
        this.greedypath = new GreedyPath(this.badStations,this.goodStations);
    }

    public Station findNearest(ArrayList<Station> sts, Position dp) {
        sts.sort((s1, s2) -> {
            Position p1 = s1.getCorrdinate();
            Position p2 = s2.getCorrdinate();
            double res1 = Drone.euclidDist(dp, p1);
            double res2 = Drone.euclidDist(dp, p2);
            if (res1 == res2)
                return 0;
            return res1 < res2 ? -1 : 1;
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

    public Direction moveRandomly() {
        while (true) {
            Direction d = Direction.values()[rnd.nextInt(16)];
            Position nextpos = this.position.nextPosition(d);
            if(nextpos.inPlayArea() && Drone.canReach(nextpos, badStations,goodStations) ) {
                return d;
            }
        }
    }

    @Override
    public ArrayList<String> play() {
        ArrayList<String> res = new ArrayList<>();
        ArrayList<Station> remaingood = new ArrayList<>();
        this.stations.forEach(s -> {
            if (s.getSymbol() == Station.Symbol.LIGHTHOUSE)
                remaingood.add(s);
        });
        while (remaingood.size() != 0) {
            Station nearest = findNearest(remaingood, this.position);
            ArrayList<Position> ressss = greedypath.findPath(this.position, nearest);
            if (ressss == null) {
                remaingood.remove(nearest);
                continue;
            }
            if (ressss.size() <= 1) {
                Direction d = moveRandomly();
                Position prev = this.position;
                move(d);
                String s = String.format("%s,%s,%s,%s,%s", prev, d, this.position, this.remainCoins,
                        this.remainPower);
                res.add(s);
                continue;
            }
            Collections.reverse(ressss);
            for (int i = 0; i < ressss.size() - 1; i++) {
                Direction dir = Position.nextDirection(ressss.get(i), ressss.get(i + 1));
                move(dir);
                if (i == ressss.size() - 2) {
                    charge(nearest);
                    String s = String.format("%s,%s,%s,%s,%s", ressss.get(i), dir, ressss.get(i + 1), this.remainCoins,
                            this.remainPower);
                    res.add(s);
                } else {
                    String s = String.format("%s,%s,%s,%s,%s", ressss.get(i), dir, ressss.get(i + 1), this.remainCoins,
                            this.remainPower);
                    res.add(s);
                }
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