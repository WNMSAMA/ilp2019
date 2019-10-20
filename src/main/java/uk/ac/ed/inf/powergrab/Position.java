package uk.ac.ed.inf.powergrab;

public class Position {
    private double latitude;
    private double longitude;
    //the straight-line distance of each travel.
    private static double dist = 0.0003;
    //the x-axis and y-axis displacement when travel in NW NE SW SE
    private static double dist45deg = dist / Math.sqrt(2);
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    //the x-axis and y-axis displacement when travel in other directions
    private static double long225 = Math.cos(Math.PI / 8) * dist;
    private static double short225 = Math.sin(Math.PI / 8) * dist;
    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public Position nextPosition(Direction direction) {        
        switch (direction) {
            case N:
                return new Position(this.latitude + dist,this.longitude);
            case E:
                return new Position(this.latitude,this.longitude + dist);
            case W:
                return new Position(this.latitude,this.longitude - dist);
            case S:
                return new Position(this.latitude - dist,this.longitude);
            case NW:
                return new Position(this.latitude + dist45deg,this.longitude - dist45deg);
            case NE:
                return new Position(this.latitude + dist45deg,this.longitude + dist45deg);
            case SW:
                return new Position(this.latitude - dist45deg,this.longitude - dist45deg);
            case SE:
                return new Position(this.latitude - dist45deg,this.longitude + dist45deg);
            case WNW:
                return new Position(this.latitude + short225,this.longitude - long225);
            case NNW:
                return new Position(this.latitude + long225,this.longitude - short225);
            case NNE:
                return new Position(this.latitude + long225,this.longitude + short225);
            case ENE:
                return new Position(this.latitude + short225,this.longitude + long225);
            case ESE:
                return new Position(this.latitude - short225,this.longitude + long225);
            case SSE:
                return new Position(this.latitude - long225,this.longitude + short225);
            case SSW:
                return new Position(this.latitude - long225,this.longitude - short225);
            case WSW:
                return new Position(this.latitude - short225,this.longitude - long225);
            default: return null;
            }        
    }
    public boolean inPlayArea() {
        if(this.latitude <  55.946233 && this.latitude > 55.942617) {
            if(this.longitude < -3.184319 && this.longitude > -3.192473)
                return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "" + this.latitude + "  " +  this.longitude;
    }

}
