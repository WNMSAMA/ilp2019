package uk.ac.ed.inf.powergrab;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * @author s1703367
 * @since 2019-11-02
 */
public class Stateful extends Drone {
    private final AstarPath astarpath;

    /**
     * The constructor of a Stateful drone.
     * Initialize the path finder with DANGERs and LIGHTHOUSEs.
     *
     * @param position The initial position.
     * @param droneType The type of the drone.
     * @param stations The stations on the map.
     * @param rnd The random seed.
     */
    Stateful(Position position, DroneType droneType, ArrayList<Station> stations, Random rnd) {
        super(position, droneType, stations, rnd);
        this.astarpath = new AstarPath(this.badStations, this.goodStations);
    }

    /**
     * This method will let the stateful drone move randomly(behaves as a stateless drone)
     * after all reachable lighthouses have been visited.
     *
     * @return An ArrayList of String , each String is in required output format.
     */
    private ArrayList<String> goStateless() {
        Stateless stl = new Stateless(this.position, Drone.DroneType.STATELESS, this.stations, this.rnd);
        stl.remainCoins = this.remainCoins;
        stl.remainPower = this.remainPower;
        stl.remainSteps = this.remainSteps;
        return stl.play();
    }

    /**
     * This method is called when the drone is already in range of the next station without moving.
     * Since the path finder will return a single node, an random move is needed to get rid of the stuck situation.
     * @return A Direction select randomly.
     */
    private Direction moveRandomly() {
        while (true) {
            Direction d = Direction.values()[rnd.nextInt(16)];// randomly select a direction.
            Position nextpos = this.position.nextPosition(d);
            if (nextpos.inPlayArea() && Drone.canReach(nextpos, badStations, goodStations)) {
                return d;
            }
        }
    }

    /**
     * This method uses an Greedy search strategy to find the next Station.
     * The drone will always looks for the closest station after each successful charge.
     * Then calls the path finder with Astar search strategy to find a path from current position
     * to the destination Station.
     * After all LIGHTHOUSES have been visited, the drone just move randomly until the end of the game.
     *
     * @return An ArrayList of String, each String is in required output format.
     */
    @Override
    protected ArrayList<String> play() {
        ArrayList<String> res = new ArrayList<>();
        ArrayList<Station> remaingood = new ArrayList<>(this.goodStations);
        while (remaingood.size() != 0) {// If all good stations have been visited, break the loop.
            Station nearest = findNearest(remaingood, this.position);
            ArrayList<Position> path = astarpath.findPath(this.position, nearest);
            if (path == null) {// If the station cannot be reached, ignore it.
                remaingood.remove(nearest);
                continue;
            }
            if(nearest.getCoins() == 0){
                remaingood.remove(nearest);
                continue;
            }
            if (path.size() <= 1) {// If the drone is already in range of a new station without moving, move randomly.
                Direction d = moveRandomly();
                Position prev = this.position;
                move(d);
                charge();
                String s = String.format("%s,%s,%s,%s,%s", prev, d, this.position, this.remainCoins,
                        this.remainPower);
                res.add(s);
                if (!this.gameStatus)// If game overs, break loop.
                    break;
                continue;
            }
            Collections.reverse(path);
            //Reverse the order of the path(since the A star algorithm back tracks the point to get a track.)
            for (int i = 0; i < path.size() - 1; i++) {
                Direction dir = Position.nextDirection(path.get(i), path.get(i + 1));//move the drone by the track given.
                move(dir);
                charge();
                String s = String.format("%s,%s,%s,%s,%s", path.get(i), dir, path.get(i + 1), this.remainCoins,
                        this.remainPower);
                res.add(s);// save to the result ArrayList.
                if (!this.gameStatus)
                    break;
            }
            remaingood.remove(nearest);
        }
        if (this.gameStatus) {// if the game is still not over, move randomly.
            System.out.println("All LIGHTHOUSE have been visited, steps = " + res.size() + ", Going stateless move.");
            ArrayList<String> finalmoves = goStateless();
            res.addAll(finalmoves);
        }
        DecimalFormat f = new DecimalFormat("##.00");
        System.out.println("Stateful Percentage = " + f.format((this.remainCoins/this.perfectscore)*100) +"%");
        return res;
    }
}