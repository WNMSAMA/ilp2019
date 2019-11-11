package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;

public abstract class Drone {
    protected Position position;
    protected double remainCoins;
    protected double remainPower;
    protected int remainSteps;
    protected ArrayList<Station> stations;

    public enum DroneType {
        STATEFUL, STATELESS
    }

    protected DroneType droneType;
    protected boolean gameStatus;

    public Drone(Position position, DroneType droneType, ArrayList<Station> stations) {
        this.position = position;
        this.droneType = droneType;
        this.stations = stations;
        this.gameStatus = true;
        this.remainCoins = 0;
        this.remainPower = 250;
        this.remainSteps = 250;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public double getRemainCoins() {
        return remainCoins;
    }

    public void setRemainCoins(double remainCoins) {
        this.remainCoins = remainCoins;
    }

    public double getRemainPower() {
        return remainPower;
    }

    public void setRemainPower(double remainPower) {
        this.remainPower = remainPower;
    }

    public int getRemainSteps() {
        return remainSteps;
    }

    public void setRemainSteps(int remainSteps) {
        this.remainSteps = remainSteps;
    }

    public DroneType getDroneType() {
        return droneType;
    }

    public void setDroneType(DroneType droneType) {
        this.droneType = droneType;
    }

    public boolean isGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(boolean gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void statelessMove(Direction d) {
        this.position = this.position.nextPosition(d);
        this.remainPower -= 1.25;
        this.remainSteps -= 1;
        if (this.remainSteps == 0 || this.remainPower <= 0)
            this.gameStatus = false;
    }

    public void statefulMove(Position next) {
            this.position = next;
            this.remainSteps -= 1;
            this.remainPower -= 1.25;
            if (this.remainSteps == 0 || this.remainPower <= 0) {
                this.gameStatus = false;
            }
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

    public abstract ArrayList<String> play();
}
