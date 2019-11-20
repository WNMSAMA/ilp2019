package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

/**
 * The Stateless class is a subclass of drone which indicates the drone is moving Statelessly.
 *
 * @author s1703367
 * @since 2019-10-27
 */
public class Stateless extends Drone {
    /**
     * The constructor of the Stateless drone.
     *
     * @param position  The initial position
     * @param droneType The drone type
     * @param stations  All the stations
     * @param rnd       The random seed
     */
    public Stateless(Position position, DroneType droneType, ArrayList<Station> stations, Random rnd) {
        super(position, droneType, stations, rnd);

    }


    /**
     * The stateless drone will only move to a positive station if it can reach that station with in one step.
     * If not, move randomly and avoid danger stations.
     *
     * @return An ArrayList of String, each String is in required output format.
     */
    @Override
    protected ArrayList<String> play() {
        ArrayList<String> res = new ArrayList<>();
        while (this.gameStatus) {// If game is over, break the loop.
            StringBuilder sb = new StringBuilder();// create an string builder to store the current step's output.
            sb.append(this.position.getLatitude()).append(",").append(this.position.getLongitude()).append(",");
            Direction nextdir = null;
            int flag = 0;
            ArrayList<Direction> dangers = new ArrayList<>();// Store all directions which will cause an invalid move.
            for (Direction d : Direction.values()) {
                Position pos = this.position.nextPosition(d);
                boolean b = canReach(pos, this.badStations, this.goodStations);
                if (!b) {
                    dangers.add(d);
                    continue;
                }
                Station nearest = findNearest(this.goodStations,pos);
                if(nearest != null && euclidDist(nearest.getCorrdinate(),pos)<= CHARGE_RANGE
                    && nearest.getSymbol() == Station.Symbol.LIGHTHOUSE ){
                    move(d);
                    charge();
                    nextdir = d;
                    flag = 1;
                    break;
                }
            }
            if(flag == 0){
            while (true) {
                nextdir = Direction.values()[rnd.nextInt(16)];
                if (dangers.contains(nextdir)) continue;
                move(nextdir);
                charge();
                break;
            }}

            sb.append(nextdir).append(",").append(this.position.getLatitude())
                    .append(",").append(this.position.getLongitude()).append(",")
                    .append(this.remainCoins).append(",").append(this.remainPower);
            res.add(sb.toString());//add status of the drone after the move.
        }
        return res;
    }

}