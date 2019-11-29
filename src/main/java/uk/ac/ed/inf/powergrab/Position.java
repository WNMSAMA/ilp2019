package uk.ac.ed.inf.powergrab;

/**
 * The position class.
 * Has methods finding positions and directions.
 * @author s1703367
 * @since 2019-09-23
 */
public class Position {
    private double latitude;
    private double longitude;
    // the straight-line distance of each travel.
    private static final double dist = 0.0003;
    // the x-axis and y-axis displacement when travel in NW NE SW SE
    private static final double dist45deg = dist / Math.sqrt(2);

    // the x-axis and y-axis displacement when travel in other directions
    private static final double long225 = Math.cos(Math.PI / 8) * dist;
    private static final double short225 = Math.sin(Math.PI / 8) * dist;

    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    double getLatitude() {
        return latitude;
    }

    double getLongitude() {
        return longitude;
    }

    /**
     * This method will return next position of the drone when it makes a move in the specified compass direction
     * @param direction The direction which the drone is moving at.
     * @return The position after the move.
     */
    Position nextPosition(Direction direction) {
        switch (direction) {
            case N:
                return new Position(this.latitude + dist, this.longitude);
            case E:
                return new Position(this.latitude, this.longitude + dist);
            case W:
                return new Position(this.latitude, this.longitude - dist);
            case S:
                return new Position(this.latitude - dist, this.longitude);
            case NW:
                return new Position(this.latitude + dist45deg, this.longitude - dist45deg);
            case NE:
                return new Position(this.latitude + dist45deg, this.longitude + dist45deg);
            case SW:
                return new Position(this.latitude - dist45deg, this.longitude - dist45deg);
            case SE:
                return new Position(this.latitude - dist45deg, this.longitude + dist45deg);
            case WNW:
                return new Position(this.latitude + short225, this.longitude - long225);
            case NNW:
                return new Position(this.latitude + long225, this.longitude - short225);
            case NNE:
                return new Position(this.latitude + long225, this.longitude + short225);
            case ENE:
                return new Position(this.latitude + short225, this.longitude + long225);
            case ESE:
                return new Position(this.latitude - short225, this.longitude + long225);
            case SSE:
                return new Position(this.latitude - long225, this.longitude + short225);
            case SSW:
                return new Position(this.latitude - long225, this.longitude - short225);
            case WSW:
                return new Position(this.latitude - short225, this.longitude - long225);
            default:
                return null;
        }
    }

    /**
     * Check if the drone is in play area.
     * @return true if in play area.
     */
    boolean inPlayArea() {
        if (this.latitude < 55.946233 && this.latitude > 55.942617) {
            return this.longitude < -3.184319 && this.longitude > -3.192473;
        }
        return false;
    }

    /**
     * This method is used to compare two double values.
     * @param a A double value.
     * @param b A double value.
     * @return True if they are equal.
     */
    private static boolean isEqu(double a, double b) {
        return Math.abs(a - b) <= 1.0E-12d;
    }

    /**
     * Given two positions, this method will return the direction which the drone was moving in.
     *
     * @param prev The previous position of the drone.
     * @param next The next position of the drone.
     * @return The direction which the drone is moving.
     */
    static Direction nextDirection(Position prev, Position next) {
        double x = next.longitude - prev.longitude;
        double y = next.latitude - prev.latitude;
        if (isEqu(x, 0) && isEqu(y, dist))
            return Direction.N;
        if (isEqu(x, dist) && isEqu(y, 0))
            return Direction.E;
        if (isEqu(x, 0) && isEqu(y, -dist))
            return Direction.S;
        if (isEqu(x, -dist) && isEqu(y, 0))
            return Direction.W;
        if (isEqu(x, -dist45deg) && isEqu(y, -dist45deg))
            return Direction.SW;
        if (isEqu(x, dist45deg) && isEqu(y, dist45deg))
            return Direction.NE;
        if (isEqu(x, -dist45deg) && isEqu(y, dist45deg))
            return Direction.NW;
        if (isEqu(x, dist45deg) && isEqu(y, -dist45deg))
            return Direction.SE;
        if (isEqu(x, -long225) && isEqu(y, short225))
            return Direction.WNW;
        if (isEqu(x, -short225) && isEqu(y, long225))
            return Direction.NNW;
        if (isEqu(x, short225) && isEqu(y, long225))
            return Direction.NNE;
        if (isEqu(x, long225) && isEqu(y, short225))
            return Direction.ENE;
        if (isEqu(x, long225) && isEqu(y, -short225))
            return Direction.ESE;
        if (isEqu(x, short225) && isEqu(y, -long225))
            return Direction.SSE;
        if (isEqu(x, -short225) && isEqu(y, -long225))
            return Direction.SSW;
        if (isEqu(x, -long225) && isEqu(y, -short225))
            return Direction.WSW;
        return null;
    }

    @Override
    public String toString() {
        return "" + this.latitude + "," +  this.longitude+ "";
    }

}