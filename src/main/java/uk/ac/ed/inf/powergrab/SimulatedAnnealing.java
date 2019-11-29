package uk.ac.ed.inf.powergrab;

import java.util.*;

/**
 * This class uses Simulated Annealing to decided the order of the stations.
 * @author s1703367
 * @since 2019-11-20
 */
class SimulatedAnnealing {

    private int Nstations;
    private double[][] distance;
    private int[] bestPath;
    private double bestLength;
    private ArrayList<Station> stations;
    private Random rnd;
    private static final double d = 0.999993;
    private static final double INIT_TEMP = 10000;
    private static final double T0 = 0.01;

    /**
     * The constructor.
     * Initialise the distance matrix.
     * Initialise bestLength to Double.MAX_VALUE
     *
     * @param stations The stations in the map.
     * @param rnd
     */
    SimulatedAnnealing(ArrayList<Station> stations, Random rnd) {
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
    }

    /**
     * This method will initialise the order of the stations by randomly shuffle them.
     *
     * @return The initialised order of stations.
     */
    private int[] shuffel(){
        int[] temp = new int[Nstations-1];
        for(int i =1 ; i < Nstations; i++){
            temp[i-1] = i;
        }
        for(int i =0 ; i < temp.length;i++){
            int n1 = rnd.nextInt(temp.length);
            int n2 = rnd.nextInt(temp.length);
            while(n1 == n2){
                n2 = rnd.nextInt(temp.length);
            }
            int tempint = temp[n1];
            temp[n1] = temp[n2];
            temp[n2] = tempint;
        }
        return temp;
    }

    /**
     * The method will calculate the path length of the input station permutation.
     *
     * @param permutation The current station permutation.
     * @return The path length.
     */
    private double evaluate(int[] permutation) {
        double len = 0;
        for (int i = 0; i < Nstations-2; i++) {
            len += distance[permutation[i]][permutation[i+1]];
        }
        len += distance[0][permutation[0]];
        return len;
    }

    /**
     * The method takes the initialised permutation and apply SimulatedAnnealing to find a solution
     * which is close to the optimal solution.
     *
     * @param permutation The initialised permutation.
     */
    private void annealing(int[] permutation) {
        int temp;
        int ran1, ran2;
        int[] tempPermu = new int[Nstations-1];
        bestLength = evaluate(permutation);
        double T = INIT_TEMP;
        while ( T>T0) {
            if (Nstations - 1 >= 0) System.arraycopy(permutation, 0, tempPermu, 0, Nstations - 1);
            ran1 = rnd.nextInt(Nstations-1);
            ran2 = rnd.nextInt(Nstations-1);
            while (ran1 == ran2) {
                ran2 = rnd.nextInt(Nstations-1);
            }

            temp = tempPermu[ran1];
            tempPermu[ran1] = tempPermu[ran2];
            tempPermu[ran2] = temp;

            double e = evaluate(tempPermu);
            double delE = 1000000*(bestLength-e);
            if (e < bestLength) {
                bestLength = e;
                if (Nstations - 1 >= 0) System.arraycopy(tempPermu, 0, permutation, 0, Nstations - 1);
            }
            else if(rnd.nextDouble() < Math.exp(delE/T)){
                bestLength = e;
                if (Nstations - 1 >= 0) System.arraycopy(tempPermu, 0, permutation, 0, Nstations - 1);
            }
            T *= d;

        }

    }

    /**
     * The method calls the SA algorithm, finds the order and display it.
     * Then return an ArrayList<Station> with stations in the new order.
     * @return An ArrayList of Station.
     */
    ArrayList<Station> solve() {
        bestPath = shuffel();
        annealing(bestPath);
        ArrayList<Station> res = new ArrayList<>();
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
