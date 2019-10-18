package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

public class Stateful extends Drone {
    private Random rnd;
    public Stateful(Position position, DroneType droneType,ArrayList<Station> stations,Random rnd) {
        super(position, droneType, stations);
        this.rnd = rnd;
    }

    @Override
    public ArrayList<String> play() {
        return null;
    }

}
