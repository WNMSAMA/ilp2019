package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

public abstract class Drone {
    protected Position position;
    protected double remainCoins;
    protected double remainPower;
    protected int remainSteps;
    protected final Random rnd;
    protected final ArrayList<Station> stations;

    public enum DroneType {
        STATEFUL, STATELESS
    }

    protected final DroneType droneType;
    protected boolean gameStatus;

    public Drone(Position position, DroneType droneType, ArrayList<Station> stations , Random rnd) {
        this.position = position;
        this.droneType = droneType;
        this.stations = stations;
        this.rnd = rnd;
        this.gameStatus = true;
        this.remainCoins = 0;
        this.remainPower = 250;
        this.remainSteps = 250;
    }

    public abstract ArrayList<String> play();

    public void move(Direction d) {
        this.position = this.position.nextPosition(d);
        this.remainPower -= 1.25;
        this.remainSteps -= 1;
        if (this.remainSteps == 0 || this.remainPower <= 0)
            this.gameStatus = false;
    }

    public void charge(Station s) {
        if (s.getCoins() < 0) {
            double coins = this.remainCoins;
            double power = this.remainPower;
            this.remainCoins = this.remainCoins + s.getCoins() <= 0 ? 0 : this.remainCoins + s.getCoins();
            this.remainPower = this.remainPower + s.getPower() <= 0 ? 0 : this.remainPower + s.getPower();
            s.setCoins(s.getCoins() + coins);
            s.setPower(s.getPower() + power);
            if (this.remainPower == 0)
                this.gameStatus = false;
        } else {
            this.remainCoins += s.getCoins();
            this.remainPower += s.getPower();
            s.setPower(0);
            s.setCoins(0);
            s.setSymbol(Station.Symbol.DEAD);
        }
    }

    public static double euclidDist(Position x, Position y) {
        double sq1 = Math.pow(x.getLatitude() - y.getLatitude(), 2);
        double sq2 = Math.pow(x.getLongitude() - y.getLongitude(), 2);
        return Math.sqrt(sq1 + sq2);
    }
    public static boolean canReach(Position pos, ArrayList<Station> badStations,ArrayList<Station> goodStations) {
        if (!pos.inPlayArea())
            return false;
        ArrayList<Station> rangebad = new ArrayList<>();
        ArrayList<Station> rangegood = new ArrayList<>();
        badStations.forEach(s -> {
            if (inRange(pos, s))
                rangebad.add(s);
        });
        goodStations.forEach(s -> {
            if (inRange(pos, s))
                rangegood.add(s);
        });
        if (rangebad.size() != 0) {
            for (Station each : rangebad) {
                double disttobad = euclidDist(pos, each.getCorrdinate());
                if ( disttobad <= 0.00025) {
                    if(rangegood.size() != 0) {
                        for(Station good:rangegood) {
                            if(disttobad > euclidDist(pos, good.getCorrdinate()))
                                return true;
                        }
                    }
                    return false;
                }

            }
        }
        return true;

    }
    public static boolean inRange(Position currpos, Station s) {
        double x1 = currpos.getLatitude() + 0.00025;
        double x2 = currpos.getLatitude() - 0.00025;
        double y1 = currpos.getLongitude() + 0.00025;
        double y2 = currpos.getLongitude() - 0.00025;
        if (s.getCorrdinate().getLatitude() <= x1 && s.getCorrdinate().getLatitude() >= x2
                && s.getCorrdinate().getLongitude() <= y1 && s.getCorrdinate().getLongitude() >= y2)
            return true;
        return false;
    }
}