package D_Stream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class D_Stream {

	int gap = 0;
	// long tc = 0;
	int dim = 0;
	double len = 0;
	double lambda = 0;
	double dm = 0;
	double dl = 0;
	double cm = 0;
	double cl = 0;
	double N = 0;
	double beta = 0;

	public D_Stream(double len, double scope, int dim, double lambda,
			double cm, double cl, double beta) {
		this.dim = dim;
		this.len = len;// length of grid
		this.lambda = lambda;
		this.cm = cm;
		this.cl = cl;
		this.beta = beta;
		double size = Math.ceil(scope / len);
		for (int i = 0; i < dim; i++) {
			this.N *= size;
		}
		N = 2000;
		this.dm = cm / (N * (1 - lambda));
		this.dl = cl / (N * (1 - lambda));
		this.gap = (int) Math.floor(Math.log(Math.max(cl / cm, (N - cm)
				/ (N - cl)))
				/ Math.log(lambda));
		if (gap <= 0) {
			gap = 1;
		}
		if (this.gap == 0 || this.dim == 0 || this.len == 0 || this.lambda == 0
				|| this.dm == 0 || this.dl == 0 || this.cm == 0 || this.cl == 0
				|| this.N == 0 || this.beta == 0 || this.gap == 0) {
			System.out.println("parameter error");
			System.exit(0);
		}
		System.out.println("gap=" + gap);
		System.out.println("dim=" + dim);
		System.out.println("len=" + len);
		System.out.println("lambda=" + lambda);
		System.out.println("dm=" + dm);
		System.out.println("dl=" + dl);
		System.out.println("cm=" + cm);
		System.out.println("cl=" + cl);
		System.out.println("N=" + N);
		System.out.println("beta=" + beta);
		//
	}

	public void process(String datapath, String output) throws MyException {
		try {
			String input = datapath;
			String pointToGrid = output + "/pointToGrid.txt";
			BufferedReader br = new BufferedReader(new FileReader(input));
			BufferedWriter bw = new BufferedWriter(new FileWriter(pointToGrid));
			String line = null;
			// tc = 0;
			GridList gridList = new GridList();
			ClusterSet clusterSet = new ClusterSet();
			Map<Key, Long> keyMap = new HashMap<Key, Long>();
			int gridId = 0;
			String result = output + "/gridToCluster";
			long start = System.currentTimeMillis();
			long time = start;
			long end = 0;
			long time2 = 0;

//			int id = 0;
			while ((line = br.readLine()) != null) {
				StringTokenizer stk = new StringTokenizer(line);
				int id = Integer.parseInt(stk.nextToken());
//				stk.nextToken();
				double[] data = new double[dim];
				int[] vec = new int[dim];
				for (int i = 0; i < dim; i++) {
					data[i] = Double.parseDouble(stk.nextToken());
					vec[i] = (int) Math.floor(data[i] / len);
				}
//				if(stk.hasMoreTokens()){
//					System.out.println("dim error");
//					System.exit(0);
//				}
				time2 = id;
				Point p = new Point(id, id, data);
				id++;
				Key key = new Key(vec);
				// System.out.println(id);
				boolean isContains = gridList.isContains(key);

				if (!isContains) {
					if (keyMap.containsKey(key)) {
						key.tm = keyMap.get(key);
					}

					Grid g = new Grid(key, p.startTime, gridId);
					bw.write(p.id + " " + gridId + "\n");
					gridId++;
					gridList.insert(g);
				}
				bw.write(p.id + " " + gridList.map.get(key).id + "\n");
				gridList.updateGrid(key, p, lambda);

				if (id == 0) {
					gridList.initialClustering(time2, lambda, dm, dl,
							clusterSet);
				}

				// detect and remove sporadic grids from grid list;
				gridList.detect(lambda, N, cl, time2, dm, dl, beta, keyMap);
				gridList.adjust(time2, lambda, dm, dl, clusterSet);
				
				if (id % 25000 == 0 && id != 0) {
					// System.out.println("grid num = " + gridList.map.size());
					BufferedWriter bw2 = new BufferedWriter(new FileWriter(
							result + id / 1000 + ".txt"));
					Iterator<Cluster> itr = clusterSet.iterator();
					while (itr.hasNext()) {
						Cluster c = itr.next();
						Iterator<Grid> itr2 = c.gridSet.iterator();
						while (itr2.hasNext()) {
							Grid g = itr2.next();
							bw2.write(g.id + " ");
						}
						bw2.write("\n");
					}
					bw2.close();
					System.out.print(id / 1000 + " ");
					end = System.currentTimeMillis();
					if(end-time > 25000){
						System.out.println("time out");
						br.close();
						bw.close();
						System.exit(0);
					}
					System.out.println((end - time)/25.0);

					time = end;
				}
			}
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(result
					+ "final" + ".txt"));
			Iterator<Cluster> itr = clusterSet.iterator();
			while (itr.hasNext()) {
				Cluster c = itr.next();
				Iterator<Grid> itr2 = c.gridSet.iterator();
				while (itr2.hasNext()) {
					Grid g = itr2.next();
					bw2.write(g.id + " ");
				}
				bw2.write("\n");
			}
			System.out.println("final ");
			end = System.currentTimeMillis();
			System.out.println(end - start);
			bw2.close();
			bw.close();
			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws MyException {
		double lambda = 0.998;
		double cm = 3;
		double cl = 0.8;
		double beta = 0.3;
		double scope = 1;
		String input = "G:/Mypaper/dataset/dpcluster/EDMStream/ppa/data.txt";
		int dim = 51;
		double len = 5;
		String output = "G:/Mypaper/dataset/dpcluster/EDMStream/tmpResult";
		// int dim = 0;
		// double len = 0;
		// double lambda = 0;
		// double cm = 3;
		// double cl = 0.8;
		// double beta = 0.3;
		// double scope = 1;
		// String input = null;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-in")) {
				input = args[++i];
			}
			if (args[i].equals("-lambda")) {
				lambda = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-len")) {
				len = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-dim")) {
				dim = Integer.parseInt(args[++i]);
			}
		}
		if (input == null || lambda == 0 || len == 0 || dim == 0) {
			System.out.println("paremeter is error");
			System.exit(0);
		}
		D_Stream ds = new D_Stream(len, scope, dim, lambda, cm, cl, beta);
		// System.exit(0);
		long start = System.currentTimeMillis();
		ds.process(input, output);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println("D-Stream");
	}
}
