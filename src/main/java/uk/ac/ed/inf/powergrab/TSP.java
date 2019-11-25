package uk.ac.ed.inf.powergrab;

import java.util.*;
import java.util.stream.IntStream;

/**
 * 遗传算法求解TSP问题
 */
public class TSP {
    private ArrayList<Station> station;
    private int point;//起始点
    private int scale;// 种群规模
    private int cityNum; // 城市数量，染色体长度
    private int MAX_GEN; // 运行代数
    private double[][] distance; // 距离矩阵
    private int bestT;// 最佳出现代数
    private double bestLength; // 最佳长度
    private int[] bestTour; // 最佳路径

    // 初始种群，父代种群，行数表示种群规模，一行代表一个个体，即染色体，列表示染色体基因片段
    private int[][] oldPopulation;
    private int[][] newPopulation;// 新的种群，子代种群
    private double[] fitness;// 种群适应度，表示种群中各个个体的适应度

    private double[] Pi;// 种群中各个个体的累计概率
    private double Pc;// 交叉概率
    private double Pm;// 变异概率
    private int t;// 当前代数

    private Random random;

    /**
     * constructor of GA
     *
     * @param p 初始点
     * @param s 种群规模
     * @param g 运行代数
     * @param c 交叉率
     * @param m 变异率
     **/
    public TSP(int p, int s, int g, double c, double m, ArrayList<Station> station,Random random) {
        point = p;
        scale = s;
        this.station = station;
        cityNum = this.station.size();
        MAX_GEN = g;
        Pc = c;
        Pm = m;
        this.random = random;
        distance = new double[cityNum][cityNum];
        // 计算距离矩阵
        for (int i = 0; i < cityNum - 1; i++) {
            distance[i][i] = 0; // 对角线为0
            for (int j = i + 1; j < cityNum; j++) {
                double rij = Drone.euclidDist(this.station.get(i).getCorrdinate(), this.station.get(j).getCorrdinate());
                distance[i][j] = rij;
                distance[j][i] = distance[i][j];

            }
        }
        distance[cityNum - 1][cityNum - 1] = 0;

        bestLength = Integer.MAX_VALUE;
        bestTour = new int[cityNum];
        bestT = 0;
        t = 0;

        newPopulation = new int[scale][cityNum - 1];
        oldPopulation = new int[scale][cityNum - 1];
        fitness = new double[scale];
        Pi = new double[scale];
    }
    // 初始化种群
    void initGroup() {
        int i, j, k;
        // Random random = new Random(System.currentTimeMillis());
        for (k = 1; k < scale; k++)// 种群数
        {
            oldPopulation[k][0] = random.nextInt(cityNum);
            while (oldPopulation[k][0] == point) {
                oldPopulation[k][0] = random.nextInt(cityNum);
            }
            for (i = 1; i < cityNum - 1; )// 染色体长度
            {
                oldPopulation[k][i] = random.nextInt(this.cityNum);
                while (oldPopulation[k][i] == point) {
                    oldPopulation[k][i] = random.nextInt(cityNum);
                }
                for (j = 0; j < i; j++) {
                    if (oldPopulation[k][i] ==  oldPopulation[k][j]) {
                        break;
                    }
                }
                if (j == i) {
                    i++;
                }
            }
        }
    }
    public int[] initgreedy(){
        int[] res = new int[cityNum-1];

        double[][] distcpy = new double[cityNum][cityNum];
        for(int i = 0 ; i < cityNum;i++){
            for(int j = 0 ; j< cityNum ;j++){
                distcpy[i][j] = this.distance[i][j];
            }
        }
        ArrayList<Integer> remainidx = new ArrayList<>();
        for(int i = 1 ; i < cityNum;i++){
            remainidx.add(i);
        }
        res[0] = minimum(distcpy[0]);
        remainidx.remove((Integer) res[0]);
        for(int i = 0 ; i < cityNum;i++){
            distcpy[0][i] = 0;
            distcpy[res[0]][0] = 0;
            distcpy[i][0] = 0;
            distcpy[0][res[0]] = 0;
        }

        int idx = 1;
        while(remainidx.size()!= 0){
            res[idx] = minimum(distcpy[res[idx-1]]);
            remainidx.remove((Integer) res[idx]);
            for(int i = 0 ; i < cityNum;i++){
                distcpy[res[idx-1]][i] = 0;
                distcpy[i][res[idx-1]] = 0;
                distcpy[res[idx-1]][res[idx]] = 0;
            }
            idx++;
        }
        return res;
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
    public static void copyArr(int[] target,int[] data){
        for(int i = 0 ; i < target.length;i++){
            target[i] = data[i];
        }
    }
    public double evaluate(int[] chromosome) {
        // 0123
        double len = 0;
        // 染色体，起始城市,城市1,城市2...城市n,计算长度（代价）
        for (int i = 1; i < cityNum-1; i++) {
            len += distance[chromosome[i - 1]][chromosome[i]];
        }
        // 起始城市到第一个城市的距离
        len += distance[point][chromosome[0]];
        return len;
    }

    // 计算种群中各个个体的累积概率，前提是已经计算出各个个体的适应度fitness[max]，作为赌轮选择策略一部分，Pi[max]
    void countRate() {
        int k;
        double sumFitness = 0;// 适应度总和

        double[] tempf = new double[scale];

        for (k = 0; k < scale; k++) {
            tempf[k] = fitness[k];
            sumFitness += tempf[k];
        }

        Pi[0] = tempf[0] / sumFitness;//0-pi[0]表示第一个个体被选到的累计概率区域
        for (k = 1; k < scale; k++) {
            Pi[k] = tempf[k] / sumFitness + Pi[k - 1];
        }
    }

    // 挑选某代种群中适应度最高的个体，直接复制到子代中
    // 前提是已经计算出各个个体的适应度Fitness[max]
    public void selectBestGh() {
        int k, i, maxid;
        double maxevaluation;

        maxid = 0;
        maxevaluation = fitness[0];
        for (k = 1; k < scale; k++) {
            if (maxevaluation > fitness[k]) {
                maxevaluation = fitness[k];
                maxid = k;
            }
        }

        if (bestLength > maxevaluation) {
            bestLength = maxevaluation;
            bestT = t;// 最好的染色体出现的代数;
            for (i = 0; i < cityNum - 1; i++) {
                bestTour[i] = oldPopulation[maxid][i];
            }
        }

        // System.out.println("代数 " + t + " " + maxevaluation);
        // 复制染色体，k表示新染色体在种群中的位置，kk表示旧的染色体在种群中的位置
        copyGh(0, maxid);// 将当代种群中适应度最高的染色体k复制到新种群中，排在第一位0
    }

    // 复制染色体，k表示新染色体在种群中的位置，kk表示旧的染色体在种群中的位置
    public void copyGh(int k, int kk) {
        copyArr(this.newPopulation[k],this.oldPopulation[kk]);
    }

    // 赌轮选择策略挑选
    public void select() {
        int k, i, selectId;
        double ran1;
        for (k = 1; k < scale; k++) {
            ran1 =random.nextDouble();
            // 产生方式
            for (i = 0; i < scale; i++) {
                if (ran1 <= Pi[i]) {
                    break;
                }
            }
            selectId = i;
            copyGh(k, selectId);
        }
    }


    //进化函数，保留最好染色体不进行交叉变异
    public void evolution1() {
        // 挑选某代种群中适应度最高的个体
        selectBestGh();

        // 赌轮选择策略挑选scale-1个下一代个体
        select();

        double r;
        for (int i = 0 ; i < scale/2;i++) {
            r = random.nextDouble();// /产生概率
            int k1 =0,k2 = 0;
            while(k1 == k2 || k1 == 0  || k2 ==0){
                k1 = random.nextInt(scale);
                k2 = random.nextInt(scale);
            }
            if (r < Pc) {
                OXCross2(k1, k2);// 进行交叉
            } else {
                r = random.nextDouble();// /产生概率
                // 变异
                if (r < Pm) {
                    OnCVariation(k1);
                }
                r = random.nextDouble();// /产生概率
                // 变异
                if (r < Pm) {
                    OnCVariation(k2);
                }
            }
        }
//            if (k == scale / 2 - 1)// 剩最后一个染色体没有交叉L-1
//            {
//                r = random.nextDouble();// /产生概率
//                if (r < Pm) {
//                    OnCVariation(k);
//                }
//            }

    }

    // 交叉算子,相同染色体交叉产生不同子代染色体
    public void OXCross1(int k1, int k2) {
        int[] Gh1 = new int[this.newPopulation[k1].length];
        int[] Gh2 = new int[this.newPopulation[k2].length];
        copyArr(Gh1,this.newPopulation[k1]);
        copyArr(Gh2,this.newPopulation[k2]);
        int ran1 = random.nextInt(cityNum-1);
        int ran2 = random.nextInt(cityNum-1);
        while (ran1 == ran2) {
            ran2 = random.nextInt(cityNum - 1);
        }
        if (ran1 > ran2)
        {
            int temp = ran1;
            ran1 = ran2;
            ran2 = temp;
        }
        int idx1 = 0;
        int idx2 = 0;
        int[] swp1 = Arrays.copyOfRange(Gh1,ran1,ran2+1);
        int[] swp2 = Arrays.copyOfRange(Gh2,ran1,ran2+1);
        for(int i = 0;i < cityNum-1; i++){
            int n = i;
            while(idx1 >= ran1 && idx1 <= ran2){
                idx1 ++;
            }
            while(idx2 >= ran1 && idx2 <= ran2){
                idx2++;
            }
            if(IntStream.of(swp1).noneMatch(x -> x == Gh2[n])){
                this.newPopulation[k1][idx1] = Gh2[n];
                idx1++;
            }
            if(IntStream.of(swp2).noneMatch(x -> x == Gh1[n])){
                this.newPopulation[k2][idx2] = Gh1[n];
                idx2++;
            }
        }
    }
    public void OXCross2(int k1,int k2){
        ArrayList<Integer> idxs = new ArrayList<>();
        int[] S1 = new int[cityNum-1];
        int[] S2 = new int[cityNum-1];
        int[] Gh1 = new int[this.newPopulation[k1].length];
        int[] Gh2 = new int[this.newPopulation[k2].length];
        copyArr(Gh1,this.newPopulation[k1]);
        copyArr(Gh2,this.newPopulation[k2]);
        int ran = random.nextInt(cityNum-1);
        for (int i = 0; i < ran; i++) {
            int rnd1 = random.nextInt(cityNum-1);
            while (idxs.contains((Integer) rnd1)) {
                rnd1 = random.nextInt(cityNum-1);
            }
            idxs.add(rnd1);
        }
        for (Integer i : idxs) {
            S1[i] = Gh1[i];
            S2[i] = Gh2[i];
        }
        int idx1 = 0;
        int idx2 = 0;
        for (int i = 0; i < cityNum-1; i++) {
            while (S1[i] == 0) {
                int n = idx2;
                if (Arrays.stream(S1).anyMatch(x -> x == Gh2[n])) {
                    idx2++;
                    continue;
                }
                S1[i] = Gh2[idx2];
                idx2++;
                break;
            }

        }
        for (int i = 0; i < cityNum-1; i++) {
            while(S2[i] == 0) {
                int n = idx1;
                if (Arrays.stream(S2).anyMatch(x -> x == Gh1[n])) {
                    idx1++;
                    continue;
                }
                S2[i] = Gh1[idx1];
                idx1++;
                break;
            }
        }
        copyArr(this.newPopulation[k1],S1);
        copyArr(this.newPopulation[k2],S2);
    }
    // 多次对换变异算子
    public void OnCVariation(int k) {
        int ran1, ran2, temp;
        int count;// 对换次数

        //count = random.nextInt(cityNum - 1);

        // for (int i = 0; i < count; i++) {

        ran1 = random.nextInt(cityNum - 1);
        ran2 = random.nextInt(cityNum - 1);
        while (ran1 == ran2) {
            ran2 = random.nextInt(cityNum - 1);
        }
        temp = newPopulation[k][ran1];
        newPopulation[k][ran1] = newPopulation[k][ran2];
        newPopulation[k][ran2] = temp;
        //}
    }

    public ArrayList<Station> solve() {
        int i;
        int k;
        ArrayList<Station> res = new ArrayList<>();

        // 初始化种群
        initGroup();
        this.oldPopulation[0] = initgreedy();
        // 计算初始化种群适应度，Fitness[max]
        for (k = 0; k < scale; k++) {
            fitness[k] = evaluate(oldPopulation[k]);
            // System.out.println(fitness[k]);
        }
        // 计算初始化种群中各个个体的累积概率，Pi[max]
        countRate();
//            System.out.println("初始种群...");
//            for (k = 0; k < scale; k++) {
//                for (i = 0; i < cityNum - 1; i++) {
//                    System.out.print(oldPopulation[k][i] + ",");
//                }
//                System.out.println();
//                System.out.println("----" + fitness[k] + " " + Pi[k]);
//            }

        for (t = 0; t < MAX_GEN; t++) {
            evolution1();
            // 将新种群newGroup复制到旧种群oldGroup中，准备下一代进化
            for (k = 0; k < scale; k++) {
                for (i = 0; i < cityNum-1; i++) {
                    oldPopulation[k][i] = newPopulation[k][i];
                }
            }
            // 计算种群适应度
            for (k = 0; k < scale; k++) {
                fitness[k] = evaluate(oldPopulation[k]);
            }
            // 计算种群中各个个体的累积概率
            countRate();
        }

        System.out.println("最后种群...");
        for (k = 0; k < scale; k++) {
            for (i = 0; i < cityNum - 1; i++) {
                System.out.print(oldPopulation[k][i] + ",");
            }
            System.out.println();
            System.out.println("---" + fitness[k] + " " + Pi[k]);
        }

        System.out.println("Best at GEN：");
        System.out.println(bestT);
        System.out.println("Best length");
        System.out.println(bestLength);
//            System.out.println("最佳路径：");
//            System.out.print(point + "-->");
        for (i = 0; i < cityNum - 1; i++) {
//                System.out.print(bestTour[i] + "-->");
            res.add(this.station.get(bestTour[i]));
        }
//            System.out.print(point);
//            Arrays.sort(bestTour);
//            System.out.println(Arrays.toString(bestTour));
        return res;
    }

}