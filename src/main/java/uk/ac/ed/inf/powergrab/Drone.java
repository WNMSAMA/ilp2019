package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

/**
 * The abstract Drone class has an abstract method play() which is the strategy of the drone.
 * The class will construct a drone type.
 *
 * @author s1703367
 * @since 2019-10-25
 */
public abstract class Drone {
    protected Position position;
    double remainCoins;
    double remainPower;
    int remainSteps;
    final Random rnd;
    final ArrayList<Station> stations;
    ArrayList<Station> badStations;
    ArrayList<Station> goodStations;
    static final double CHARGE_RANGE = 0.00025;

    public enum DroneType {
        STATEFUL, STATELESS
    }

    final DroneType droneType;
    boolean gameStatus;
    double perfectscore;// create this for debug convenience.

    /**
     * This is the constructor of the Drone class
     * Initialize the parameters.
     * Initialize coins to 0, power and steps to 250.
     * Separate Danger and LIGHTHOUSE.
     *
     * @param position  The initial position.
     * @param droneType The type of the drone.
     * @param stations  The stations on the map.
     * @param rnd       The random seed.
     */
    Drone(Position position, DroneType droneType, ArrayList<Station> stations, Random rnd) {
        this.position = position;
        this.droneType = droneType;
        this.stations = stations;
        this.rnd = rnd;
        this.gameStatus = true;
        this.remainCoins = 0;
        this.remainPower = 250;
        this.remainSteps = 250;
        this.goodStations = new ArrayList<>();
        this.badStations = new ArrayList<>();
        double score = 0;
        for (Station each : this.stations) {// Separate all lighthouses and dangers.
            if (each.getSymbol() == Station.Symbol.DANGER)
                badStations.add(each);
            if (each.getSymbol() == Station.Symbol.LIGHTHOUSE || each.getSymbol() == Station.Symbol.DEAD) {
                goodStations.add(each);
                score += each.getCoins();
            }
        }
        this.perfectscore = score;
    }

    /**
     * The abstract function will apply different strategy on different type of the drone.
     *
     * @return An ArrayList of String, each String is in the output format.
     * format =  55.944212867965646,-3.1881838679656442,NW,55.944425,-3.188396,63.775421544612854,247.8231837318418
     */
    protected abstract ArrayList<String> play();

    DroneType getDroneType(){
        return  this.droneType;
    }
    /**
     * The method which will move the drone in the give direction.
     * Move consumes the drone power and will reduce the remainsteps by 1.
     *
     * @param d The direction of the next move of the drone.
     */
    void move(Direction d) {
        this.position = this.position.nextPosition(d);
        this.remainPower -= 1.25;
        this.remainSteps -= 1;
        if (this.remainSteps == 0 || this.remainPower <= 0) {
            this.gameStatus = false;
        }
    }

    /**
     * This method will return the Station which is the closest to the current position.
     * If the input length of ArrayList is 0,return null.
     *
     * @param sts The ArrayList of all stations.
     * @param dp  The current drone position.
     * @return The Station which is closest to the current position.
     */
    static Station findNearest(ArrayList<Station> sts, Position dp) {
        if (sts.size() == 0) return null;
        sts.sort((s1, s2) -> {//First sort Stations by distance to the drone
            Position p1 = s1.getCorrdinate();
            Position p2 = s2.getCorrdinate();
            double res1 = Drone.euclidDist(dp, p1);
            double res2 = Drone.euclidDist(dp, p2);
            if (res1 == res2)
                return 0;
            return res1 < res2 ? -1 : 1;
        });
        return sts.get(0);// pick the closest.
    }

    /**
     * This method let the drone charge at the nearest station(If the drone is in the range of the Station).
     * If no station is in range, doing nothing.
     * If the station has positive power and coin, the drone will gain all the power and coin,the Station
     * will have 0 power and 0 coins remain. If a Station has negative power and coin, the drone will lose
     * power and coins.
     *
     * The Drone will always try to charge after each move.
     */
    void charge() {
        Station s = findNearest(this.stations, this.position);
        if(s == null) return;
        if (euclidDist(s.getCorrdinate(), this.position) <= CHARGE_RANGE) {
            if (s.getCoins() < 0) {// If Station is negatively charged.
                double coins = this.remainCoins;
                double power = this.remainPower;
                this.remainCoins = this.remainCoins + s.getCoins() <= 0 ? 0 : this.remainCoins + s.getCoins();
                this.remainPower = this.remainPower + s.getPower() <= 0 ? 0 : this.remainPower + s.getPower();
                s.setCoins(s.getCoins() + coins > 0 ? 0 : s.getCoins() + coins);
                s.setPower(s.getPower() + power > 0 ? 0 : s.getPower() + power);
                if (s.getCoins() == 0) s.setSymbol(Station.Symbol.DEAD);
                if (this.remainPower == 0)
                    this.gameStatus = false;
            } else {// If the Station is positively charged.
                this.remainCoins += s.getCoins();
                this.remainPower += s.getPower();
                s.setPower(0);
                s.setCoins(0);
                s.setSymbol(Station.Symbol.DEAD);
            }
        }
    }

    /**
     * This static method will calculate the Euclidean Distance between two positions.
     *
     * @param x The first position
     * @param y The second position
     * @return The Euclidean Distance between x and y.
     */
    static double euclidDist(Position x, Position y) {
        double sq1 = Math.pow(x.getLatitude() - y.getLatitude(), 2);
        double sq2 = Math.pow(x.getLongitude() - y.getLongitude(), 2);
        return Math.sqrt(sq1 + sq2);
    }

    /**
     * This method returns a boolean value indicates whether a position the drone can reach.
     * The method will return false when the input position is not in play area.
     * If the drone is going to charge at a DANGER station at the input position(The drone is closer to DANGER than LIGHTHOUSE),
     * return false. Otherwise return true.
     *
     * @param pos          The position where the drone is going to.
     * @param badstations  ArrayList of Station with initial label DANGER
     * @param goodstations ArrayList of Station with initial label LIGHTHOUSE
     * @return boolean value indicates whether the move is valid.
     */
    static boolean canReach(Position pos, ArrayList<Station> badstations, ArrayList<Station> goodstations) {
        if (!pos.inPlayArea())
            return false;
        ArrayList<Station> rangebad = new ArrayList<>();
        ArrayList<Station> rangegood = new ArrayList<>();
        badstations.forEach(s -> {
            if (inRange(pos, s))
                rangebad.add(s);
        });//get all bad stations which are roughly in the charge range to reduce computation cost.
        goodstations.forEach(s -> {
            if (inRange(pos, s))
                rangegood.add(s);
        });//get all good stations which are roughly in the charge range to reduce computation cost.
        if (rangebad.size() != 0) {//If no bad stations in range, return true.
            Station bad = findNearest(rangebad, pos);
            double disttobad = euclidDist(pos, bad.getCorrdinate());
            if (disttobad <= CHARGE_RANGE) {
                if (rangegood.size() != 0) {
                    for (Station good : rangegood) {// If a good station is closer than a bad station, return true.
                        double disttogood = euclidDist(pos, good.getCorrdinate());
                        if (disttobad > disttogood)
                            return true;

                    }

                }
                return false;//else return false.

            }
        }
        return true;

    }

    /**
     * Check if a station is roughly in charge range of the drone
     * If a station is in the square area centered by the drone, return true.
     *           0.0005
     *         -----------
     *         |         |
     * 0.0005  |    D    | 0.0005
     *         |         |
     *         -----------
     *           0.0005
     *
     * @param currpos current position of the drone.
     * @param s       The station needs to be checked.
     * @return boolean value indicates whether the station is in the range.
     */
    private static boolean inRange(Position currpos, Station s) {
        double x1 = currpos.getLatitude() + CHARGE_RANGE;
        double x2 = currpos.getLatitude() - CHARGE_RANGE;
        double y1 = currpos.getLongitude() + CHARGE_RANGE;
        double y2 = currpos.getLongitude() - CHARGE_RANGE;
        return s.getCorrdinate().getLatitude() <= x1 && s.getCorrdinate().getLatitude() >= x2
                && s.getCorrdinate().getLongitude() <= y1 && s.getCorrdinate().getLongitude() >= y2;
    }
}