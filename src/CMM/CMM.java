package CMM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

public class CMM {

	double a;
	double lambda;
	int dim;
	List<String> groundTruth;
	int k;

	List<Point> points;
	Map<String, Cluster> CL;
	List<Cluster> CLlist;
	Map<String, Cluster> C;
	Map<String, String> CToCL;
	List<Cluster> Clist;
	List<Point> faultSet;
	Set<Cluster> faultClu;

	// double[] pen;
	// double[][] penij;
	// double[][] conij;
	// double[] knhCDist;
	// double[] knhCLDist;

	public CMM(int dim, double a, double lambda, int k) {
		this.dim = dim;
		CL = new HashMap<String, Cluster>();
		C = new HashMap<String, Cluster>();
		CLlist = new ArrayList<Cluster>();
		Clist = new ArrayList<Cluster>();
		this.a = a;
		this.lambda = lambda;
		groundTruth = new ArrayList<String>();
		faultSet = new ArrayList<Point>();
		faultClu = new HashSet<Cluster>();
		this.k = k;
	}

	public void load(String resultInput, long time) throws IOException {
		points = new ArrayList<Point>();
		BufferedReader br = new BufferedReader(new FileReader(resultInput));
		String line = null;
		while ((line = br.readLine()) != null) {
			// line : id vec[0-1] CL C
			StringTokenizer st = new StringTokenizer(line);
			int id = Integer.parseInt(st.nextToken());
			double[] vec = new double[dim];
			for (int i = 0; i < dim; i++) {
				vec[i] = Double.parseDouble(st.nextToken());
			}
			String cl = st.nextToken();// parse the ground truth
			Point p = new Point(id, id, time, vec, a, lambda, cl);
			points.add(p);
			// add to ground truth cluster CLi
			if (CL.containsKey(cl)) {
				CL.get(cl).add(p);
			} else {
				Cluster c = new Cluster();
				c.groundTruth = cl;
				c.type = ClusterType.GTCluster;
				c.add(p);
				CL.put(cl, c);
				CLlist.add(c);
			}

			String ci = st.nextToken();// parse the label
			// group the points
			if (C.containsKey(ci)) {
				C.get(ci).add(p);
			} else {
				Cluster c = new Cluster();
				c.type = ClusterType.Cluster;
				c.add(p);
				C.put(ci, c);
				Clist.add(c);
			}
			// System.out.println(p.id + " " + p.truth + " " + ci);
		}
		br.close();
	}

	public void ClusterMap(String labelPath) throws IOException {
		if (labelPath == null || "".equals(labelPath)) {
			voteMap();
		} else {
			similarityMap(labelPath);
		}
	}

	private void voteMap() {
		int csize = Clist.size();
		for (int i = 0; i < csize; i++) {
			Cluster c = Clist.get(i);
			Map<String, Double> map = new HashMap<String, Double>();
			List<Point> list = c.points;
			int psize = list.size();
			for (int j = 0; j < psize; j++) {
				Point p = list.get(j);
				String truth = p.truth;
				if (map.containsKey(truth)) {
					map.put(truth, map.get(truth) + p.weight);
				} else {
					map.put(truth, p.weight);
				}
			}
			String label = null;
			double max = 0;
			Iterator<Entry<String, Double>> itr = map.entrySet().iterator();
			while (itr.hasNext()) {
				Entry<String, Double> entry = itr.next();
				String key = entry.getKey();
				double value = entry.getValue();
				if (value > max) {
					max = value;
					label = key;
				}
			}
			if (label == null) {
				System.out.println("the label is null, this is error");
			} else {
				c.groundTruth = label;
				// System.out.println(label);
			}
		}
	}

	private void similarityMap(String labelPath) throws IOException {
		CToCL = new HashMap<String, String>();
		BufferedReader br2 = new BufferedReader(new FileReader(labelPath));
		String line = null;
		while ((line = br2.readLine()) != null) {
			String[] ss = line.split("\\s+");
			if (ss.length == 1) {
				groundTruth.add(ss[0]);
			} else {
				groundTruth.add(ss[1]);
			}
		}
		br2.close();
		Map<String, Integer> idToTruthMap = new HashMap<String, Integer>();
		int length = groundTruth.size();
		for (int i = 0; i < length; i++) {
			idToTruthMap.put(groundTruth.get(i), i);
		}
		Iterator<Entry<String, Cluster>> itr = C.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, Cluster> entry = itr.next();
			Cluster c = entry.getValue();
			c.getDistribution(idToTruthMap);
		}

		// simple set the CL = CLO
		Iterator<Entry<String, Cluster>> itr2 = CL.entrySet().iterator();
		while (itr2.hasNext()) {
			Entry<String, Cluster> entry = itr2.next();
			Cluster c = entry.getValue();
			c.getDistribution(idToTruthMap);
		}

		int cSize = C.size();
		int clSize = CL.size();
		int[][] delta = new int[C.size()][CL.size()];
		for (int i = 0; i < cSize; i++) {
			Cluster c = Clist.get(i);
			int truthNum = idToTruthMap.size();
			int[] rho = c.rho;
			// int labelNum = 0;
			int labelIndex = -1;
			for (int j = 0; j < truthNum; j++) {
				if (rho[j] > 0) {
					if (labelIndex == -1) {
						labelIndex = j;
					} else {
						labelIndex = -1;
						break;
					}
				}
			}
			List<Integer> list = new ArrayList<Integer>();
			if (labelIndex != -1) {
				c.groundTruth = groundTruth.get(labelIndex);
				continue;
			} else {
				double minError = Double.MAX_VALUE;
				for (int j = 0; j < clSize; j++) {
					delta[i][j] = getDelta(Clist.get(i), CLlist.get(j));
					if (delta[i][j] == 0) {
						list.add(j);
					}
					if (minError > delta[i][j]) {
						minError = delta[i][j];
						labelIndex = j;
					}
				}
			}
			if (list.size() == 0) {
				c.groundTruth = CLlist.get(labelIndex).groundTruth;
			} else {
				int max = 0;
				for (int j = 0; j < list.size(); j++) {
					if (max < rho[list.get(j)]) {
						max = rho[list.get(j)];
						labelIndex = list.get(j);
					}
				}
				c.groundTruth = CLlist.get(labelIndex).groundTruth;
			}
		}
	}

	private int getDelta(Cluster ci, Cluster cljo) {
		int sum = 0;
		int length = ci.rho.length;
		for (int i = 0; i < length; i++) {
			int value = ci.rho[i] - cljo.rho[i];
			sum += value > 0 ? value : 0;
		}
		return sum;
	}

	public void getFaultSet() {
		int csize = Clist.size();
		for (int i = 0; i < csize; i++) {
			Cluster c = Clist.get(i);
			String truth = c.groundTruth;
			List<Point> list = c.points;
			int psize = list.size();
			for (int j = 0; j < psize; j++) {
				Point p = list.get(j);
				if (!p.truth.equals(truth)) {
					faultSet.add(p);
					if (!faultClu.contains(c)) {
						faultClu.add(c);
						faultClu.add(CL.get(p.truth));
					}
				}
			}
		}
	}

	public double compCMM(int k) {
		getFaultSet();
		// System.out.println("get fault set");
		if (faultSet.size() == 0) {
			// System.out.println("faultSet == 0");
			return 1;
		}
		compCon(k);
		// System.out.println("comp con k");
		double totalPen = 0;
		double totalCon = 0;
		// double weight = 0;
		int faultPsize = faultSet.size();
		for (int i = 0; i < faultPsize; i++) {
			Point p = faultSet.get(i);
			// System.out.println(i + "=========++++++");
			// System.out.println("weight =" + p.weight);
			// System.out.println("conCl =" + p.conCL);
			// System.out.println("con =" + p.con);
			totalPen += p.weight * p.conCL * (1 - p.con);
			totalCon += p.weight * p.conCL;
		}

		return 1 - totalPen / totalCon;
	}

	private void compCon(int k) {
		Iterator<Cluster> itr = faultClu.iterator();
		// System.out.println(faultClu.size());
		while (itr.hasNext()) {
			Cluster c = itr.next();
			// System.out.println(c.groundTruth);
			c.getConn(k);
		}
	}

	private double pen(Point o, Cluster c) {
		double pen = 0;

		return pen;
	}

	private double con(Point o, Cluster c) {
		double con = 0;

		return con;
	}
}
