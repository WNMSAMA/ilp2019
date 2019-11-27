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
     * This method calls findPath in AstarPath to find a shortest path from start position to the destination.
     * If a LIGHTHOUSE is surrounded by the DANGERs,the drone will charge at a DANGER station
     * then charge at the target LIGHTHOUSE(when the coin gain is greater than coin loss).
     * @param nearest The target station.
     * @return A ArrayList of Position(The drone's path).
     */
    private ArrayList<Position> findPath(Station nearest){
        AstarPath astarpath = new AstarPath(this.badStations, this.goodStations);
        ArrayList<Position> path;
        try {
            path = astarpath.findPath(this.position, nearest);
        } catch (AstarPath.PathNotFoundException e) {
            ArrayList<Station> tempbad = new ArrayList<>(this.badStations);
            ArrayList<Station> tempgood = new ArrayList<>(this.goodStations);
            int badidx = 0;
            ArrayList<Position> temppath = null;
            while(true){
                if(badidx >= tempbad.size()) break;
                Station assignGood = tempbad.get(badidx);
                if(assignGood.getCoins() + nearest.getCoins() < 0) {// If it worth to charge at a DANGER
                    badidx++;
                    continue;
                }
                tempgood.add(assignGood);
                tempbad.remove(badidx);
                try{
                    AstarPath tempAstar = new AstarPath(tempbad,tempgood);
                    temppath = tempAstar.findPath(this.position,nearest);
                    this.badStations.remove(assignGood);
                    this.goodStations.add(assignGood);
                    break;
                }catch (AstarPath.PathNotFoundException ee){
                    tempgood.remove(assignGood);
                    tempbad.add(assignGood);
                    badidx++;
                }
            }
            path = temppath;
        }
        return path;
    }

    /**
     * This method uses an HillClimbing algorithm to find the next Station.
     * The method calls the path finder with Astar search strategy to find a path from current position to a
     * destination station after found the permutation of stations.
     * After all LIGHTHOUSES have been visited, the drone just move randomly until the end of the game.
     *
     * @return An ArrayList of String, each String is in required output format.
     */
    @Override
    protected ArrayList<String> play() {
        ArrayList<String> res = new ArrayList<>();
        Station dummy = new Station("dummy" , 0,250, Station.Symbol.LIGHTHOUSE,this.position);
        ArrayList<Station> runs = new ArrayList<>(this.goodStations);
        runs.add(0,dummy);
        HillClimbing cli = new HillClimbing(7000,runs,rnd);
        ArrayList<Station> remaingood = cli.solve();
        while (remaingood.size() != 0) {// If all good stations have been visited, break the loop.
            Station nearest = remaingood.get(0);
            ArrayList<Position> path = findPath(nearest);
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