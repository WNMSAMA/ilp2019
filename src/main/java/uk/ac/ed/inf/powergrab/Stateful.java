package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Stateful extends Drone {
    private final ArrayList<Station> badStations;
    private final ArrayList<Station> goodStations;
    private final AstarPath astarpath;

    public Stateful(Position position, DroneType droneType, ArrayList<Station> stations, Random rnd) {
        super(position, droneType, stations, rnd);
        badStations = new ArrayList<>();
        this.goodStations = new ArrayList<>();
        for (Station each : this.stations) {
            if (each.getSymbol() == Station.Symbol.DANGER)
                badStations.add(each);
            if (each.getSymbol() == Station.Symbol.LIGHTHOUSE)
                goodStations.add(each);
        }
        this.astarpath = new AstarPath(this.badStations, this.goodStations);
    }

    public ArrayList<String> goStateless(Stateful drone) {
        Stateless stl = new Stateless(drone.position, Drone.DroneType.STATELESS, drone.badStations, drone.rnd);
        stl.remainCoins = drone.remainCoins;
        stl.remainPower = drone.remainPower;
        stl.remainSteps = drone.remainSteps;
        return stl.play();
    }

    public Direction moveRandomly() {
        while (true) {
            Direction d = Direction.values()[rnd.nextInt(16)];
            Position nextpos = this.position.nextPosition(d);
            if (nextpos.inPlayArea() && Drone.canReach(nextpos, badStations, goodStations)) {
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
            int b;
            Station nearest = findNearest(remaingood, this.position);
            ArrayList<Position> ressss = astarpath.findPath(this.position, nearest);
            if (ressss == null) {
                remaingood.remove(nearest);
                continue;
            }
            if(nearest.getCoins() == 0){
                remaingood.remove(nearest);
                continue;
            }
            if (ressss.size() <= 1) {
                Direction d = moveRandomly();
                Position prev = this.position;
                move(d);
                charge();
                String s = String.format("%s,%s,%s,%s,%s", prev, d, this.position, this.remainCoins,
                        this.remainPower);
                res.add(s);
                continue;
            }
            Collections.reverse(ressss);
            for (int i = 0; i < ressss.size() - 1; i++) {
                Direction dir = Position.nextDirection(ressss.get(i), ressss.get(i + 1));
                move(dir);
                charge();
                String s = String.format("%s,%s,%s,%s,%s", ressss.get(i), dir, ressss.get(i + 1), this.remainCoins,
                        this.remainPower);
                res.add(s);
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