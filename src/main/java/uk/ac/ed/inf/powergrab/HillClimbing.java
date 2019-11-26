package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

public class HillClimbing {

    private int MAX_ITER;
    private int Nstations;
    private double[][] distance;
    private int bestT;
    private int[] bestPath;
    private double bestLength;
    private ArrayList<Station> stations;
    private Random rnd;


    /**
     **/
    public HillClimbing(int maxiter, ArrayList<Station> stations, Random rnd) {
        this.MAX_ITER = maxiter;
        this.stations = stations;
        this.Nstations = this.stations.size();
        this.rnd = rnd;
        this.distance = new double[Nstations][Nstations];
        for (int i = 0; i < Nstations - 1; i++) {
            distance[i][i] = 0;
            for (int j = i + 1; j < Nstations; j++) {
                double rij = Drone.euclidDist(this.stations.get(i).getCorrdinate(), this.stations.get(j).getCorrdinate());
                distance[i][j] = rij;
                distance[j][i] = distance[i][j];

            }
        }
        distance[Nstations - 1][Nstations - 1] = 0;

        bestLength = Double.MAX_VALUE;
        bestPath = new int[Nstations-1];
        bestT = 0;
    }
    public int minimum(double[] input){
        double res = 100;
        int idx = 0;
        for(int i = 0 ; i < input.length;i++){
            if(res > input[i] && input[i] != 0){
                idx = i;
                res = input[i];}
        }
        return idx;
    }
    public int[] initgreedy(){
        int[] res = new int[Nstations-1];

        double[][] distcpy = new double[Nstations][Nstations];
        for(int i = 0 ; i < Nstations;i++){
            for(int j = 0 ; j< Nstations ;j++){
                distcpy[i][j] = this.distance[i][j];
            }
        }
        ArrayList<Integer> remainidx = new ArrayList<>();
        for(int i = 1 ; i < Nstations;i++){
            remainidx.add(i);
        }
        res[0] = minimum(distcpy[0]);
        remainidx.remove((Integer) res[0]);
        for(int i = 0 ; i < Nstations;i++){
            distcpy[0][i] = 0;
            distcpy[res[0]][0] = 0;
            distcpy[i][0] = 0;
            distcpy[0][res[0]] = 0;
        }

        int idx = 1;
        while(remainidx.size()!= 0){
            res[idx] = minimum(distcpy[res[idx-1]]);
            remainidx.remove((Integer) res[idx]);
            for(int i = 0 ; i < Nstations;i++){
                distcpy[res[idx-1]][i] = 0;
                distcpy[i][res[idx-1]] = 0;
                distcpy[res[idx-1]][res[idx]] = 0;
            }
            idx++;
        }
        return res;
    }


    void initGroup() {
        bestPath = initgreedy();
    }

    public double evaluate(int[] permutation) {
        double len = 0;
        for (int i = 0; i < Nstations-2; i++) {
            len += distance[permutation[i]][permutation[i+1]];
        }
        len += distance[0][permutation[0]];
        return len;
    }


    public void climb(int[] permutation, int maxiter) {
        int temp;
        int ran1, ran2;
        int[] tempPermu = new int[Nstations-1];
        bestLength = evaluate(permutation);

        for (int iter = 0; iter < maxiter; iter++) {
            for (int i = 0; i < Nstations-1; i++) {
                tempPermu[i] = permutation[i];
            }
            ran1 = rnd.nextInt(Nstations-1);
            ran2 = rnd.nextInt(Nstations-1);
            while (ran1 == ran2) {
                ran2 = rnd.nextInt(Nstations-1);
            }

            temp = tempPermu[ran1];
            tempPermu[ran1] = tempPermu[ran2];
            tempPermu[ran2] = temp;

            double e = evaluate(tempPermu);

            if (e < bestLength) {
                bestT = iter;
                bestLength = e;
                for (int j = 0; j < Nstations-1; j++) {
                    permutation[j] = tempPermu[j];
                }
            }
        }

    }

    public ArrayList<Station> solve() {
        initGroup();
        climb(bestPath, MAX_ITER);
        ArrayList<Station> res = new ArrayList<>();
        System.out.println("Best at :");
        System.out.println(bestT);
        System.out.println("Best Path length: ");
        System.out.println(bestLength);
        System.out.println("Best Path: ");
        for (int i = 0; i < Nstations-1; i++) {
            System.out.print(bestPath[i] + ",");
            res.add(this.stations.get(bestPath[i]));
        }
        System.out.println();
        return res;
    }
}
