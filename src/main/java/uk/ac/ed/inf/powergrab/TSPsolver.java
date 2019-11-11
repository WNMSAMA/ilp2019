package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;

public class TSPsolver {
    private ArrayList<Station> stations;
    private Position start;
    private double[][] dists;
    private int[] row;
    private int[] colable;

    public TSPsolver(ArrayList<Station> stations, Position start) {
        this.start = start;
        this.stations = stations;
        Station dummy = new Station("start", 0, 0, Station.Symbol.LIGHTHOUSE, this.start);
        this.stations.add(0, dummy);
        dists = new double[this.stations.size()][this.stations.size()];
        row = new int[this.stations.size()];
        colable = new int[this.stations.size()];
        for (int i = 0; i < this.stations.size() - 1; i++) {
            dists[i][i] = 0;
            for (int j = i + 1; j < this.stations.size(); j++) {
                double rij = Drone.euclidDist(this.stations.get(i).getCorrdinate(),
                        this.stations.get(j).getCorrdinate());
                dists[i][j] = rij;
                dists[i][j] = rij;
            }
            dists[this.stations.size() - 1][this.stations.size() - 1] = 0;
        }
        colable[0] = 0;
        for (int i = 1; i < this.stations.size(); i++) {
            colable[i] = 1;
        }
        for (int i = 0; i < this.stations.size(); i++) {
            row[i] = 1;
        }

    }

    public ArrayList<Position> solve() {
        ArrayList<Position> res = new ArrayList<>();
        res.add(this.start);
        double[] temp = new double[this.stations.size()];
        String path = "0";
        double s = 0;
        int i = 0;
        int j = 0;
        while (row[i] == 1) {
            for (int k = 0; k < this.stations.size(); k++) {
                temp[k] = dists[i][k];
                // System.out.print(temp[k]+" ");
            }
            j = selectMin(temp);
            row[i] = 0;
            colable[j] = 0;
            path += "-->" +this.stations.get(j).getId();
            res.add(this.stations.get(j).getCorrdinate());
            s = s + dists[i][j];  
            i = j;
        }
        System.out.println("路径:" + path);  
        System.out.println("总距离为:" + s);  
        return res;
    }

    public int selectMin(double[] p) {
        int j = 0,  k = 0;
        double m = p[0];
        while (colable[j] == 0) {
            j++;
            if (j >= this.stations.size()) {
                m = p[0];
                break;
            } else {
                m = p[j];
            }
        }
        for (; j < this.stations.size(); j++) {
            if (colable[j] == 1) {
                if (m >= p[j]) {
                    m = p[j];
                    k = j;
                }
            }
        }
        return k;
    }

}
