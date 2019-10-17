package uk.ac.ed.inf.powergrab;

public class Drone {
    private Position position;
    private double remainCoins;
    private double remainPower;
    private int remainSteps;
    public enum DroneType{
        STATEFUL,STATELESS
    }
    private DroneType droneType;
    private boolean gameStatus;
    public Drone(Position position, DroneType droneType) {
        this.position = position;
        this.droneType = droneType;
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
    public boolean move(Direction d) {
        if (this.position.nextPosition(d).inPlayArea()) {
            this.position  = this.position.nextPosition(d);
            this.remainPower -= 1.25;
            this.remainSteps -= 1;
            if (this.remainSteps == 0 || this.remainPower <= 0) this.gameStatus = false;
            return true;
        }
        return false;
    }
    public void charge(Station s) {
        if (s.getCoins() < 0 ) {
            double coins = this.remainCoins;
            double power = this.remainPower;
            this.remainCoins = this.remainCoins + s.getCoins() <=0 ?  0 : this.remainCoins + s.getCoins();
            this.remainPower = this.remainPower + s.getPower() <=0 ?  0 : this.remainPower + s.getPower();
            s.setCoins(s.getCoins()+coins);
            s.setPower(s.getPower()+power);
            if(this.remainPower == 0) this.gameStatus = false;
            }
        else {
            this.remainCoins += s.getCoins();
            this.remainPower += s.getPower();
            s.setPower(0);
            s.setCoins(0);
            s.setSymbol(Station.Symbol.DEAD);}
    }
}
