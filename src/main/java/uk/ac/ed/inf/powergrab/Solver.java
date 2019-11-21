package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

public class Solver {
    private final ArrayList<Station> stations;
    private final int n_stations;
    private static final double INI_TEMP = 2000;
    private static final double T_END = 1.0E-8d;
    private static final int MAX_ITER_PER_TEMP = 1000;
    private static final double q = 0.98;
    private double[][] dists;
    private double distance = 0;
    private final Random rnd;
    public Solver(ArrayList<Station> stations, Random rnd){
        this.stations = stations;
        this.rnd = rnd;
        this.n_stations = this.stations.size();
        this.dists = new double[this.stations.size()+1][this.stations.size()+1];
        for(int i = 0 ; i < this.stations.size();i++){
            dists[i][i] = 0;
            for(int j = i ; j < this.stations.size();j++){
                double dist = Drone.euclidDist(this.stations.get(i).getCorrdinate(),
                        this.stations.get(j).getCorrdinate());
                dists[i][j] = dist;
                dists[j][i] = dist;
            }
        }
    }

    public double[][] getDists() {
        return dists;
    }
    public double findPathLen(Station[] sts){
        double res = 0;
        for(int i = 0 ; i < sts.length-1 ;i++){
            res += Drone.euclidDist(sts[i].getCorrdinate(),sts[i+1].getCorrdinate());
        }
        return res;
    }
    public void shuffle(Station[] sts){
        int r1 = this.rnd.nextInt(n_stations);
        int r2 = this.rnd.nextInt(n_stations);
        Station temp = sts[r1];
        sts[r1] = sts[r2];
        sts[r2] = temp;
    }
    public Station[] findPermu(){
         Station[] path = new Station[this.stations.size()];
         double T = INI_TEMP;
         for(int i = 0 ; i < this.stations.size();i++){
             path[i] = this.stations.get(i);
         }
         Station[] init_copy = new Station[this.stations.size()];
         double df;
         double r;
         while (T > T_END){
             for(int j = 0 ; j < MAX_ITER_PER_TEMP;j++) {
                 for (int i = 0; i < init_copy.length; i++) {
                     init_copy[i] = path[i];
                 }
                 shuffle(path);
                 df = findPathLen(init_copy) - findPathLen(path);
                 if(df >= 0){
                     if(Math.exp(-df/T) <= Math.random()){
                         for (int i = 0; i < init_copy.length; i++) {
                             path[i] = init_copy[i];
                         }
                     }
                 }
             }
             T *= q;
         }
         return path;

    }


}
