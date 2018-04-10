package EDMStream;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class DPTreeV5 {

	public long lastTime;
	public int size;
	public int num;
	public CluCell[] Clus;

	public double a;
	public double lamd;
	public double CluR;

	public int cluLabel;
	public double minDelta;

	public DPTreeV5(int num, double CluR) {
		this.num = num;
		this.Clus = new CluCell[num];
		this.CluR = CluR;
		cluLabel = 0;
	}

	public void init(CluCell[] clus, int size, double minRho, double minDelta,
			OutlierReservoir outs, HashSet<Cluster> clusters) {
		this.minDelta = minDelta;
		Clus[0] = clus[0];
		Cluster cluster = new Cluster(cluLabel++);
		cluster.add(Clus[0]);
		clusters.add(cluster);
		int i = 1;

		for (; i < size && clus[i].rho >= minRho; i++) {
			Clus[i] = clus[i];
			// System.out.println(Clus[i]);
			Clus[i].dep.sucs.add(Clus[i]);
			if (Clus[i].delta > minDelta) {
				Cluster c = new Cluster(cluLabel++);
				c.add(Clus[i]);
				clusters.add(c);
			} else {
				Clus[i].dep.cluster.add(Clus[i]);
			}

		}
		double maxDelta = 0;
		for (int j = 1; j < size && j < i; j++) {
			// System.out.println(j);
			// System.out.println(size);
			// System.out.println(Clus[j]);
			if (maxDelta < Clus[j].delta) {
				maxDelta = Clus[j].delta;
			}
		}
		Clus[0].delta = maxDelta;
		this.size = i;
		for (; i < size; i++) {
			outs.insert(clus[i]);
		}
	}

	// public void update(CluCell cc, HashSet<Cluster> clusters, double minRho,
	// Point p, int opt) {
	// insert(cc, minRho, p, opt);
	// }

	public void insert(CluCell cc, double minRho, Point p, int opt) {

		cc.active = true;
		Clus[size] = cc;
		size++;

		if (opt == 0) {
			adjustNoOpt(size - 1, minRho, p);
		}
		if (opt == 1) {
			adjustOpt1(size - 1, minRho, p);
		}

		if (opt == 2) {
			adjust(size - 1, minRho, p);
		}

		if (opt == -1) {
			adjustNoDelta(size - 1, minRho, p);
		}

		if (size == num) {
			System.err.println("lack of DPTree nodes");
		}
	}

	public CluCell findNN(Point p, double coef, 
			HashSet<Cluster> clusters, double minRho, int opt) {
		int index = 0;
		double dis = 0;
		double minDis = Double.POSITIVE_INFINITY;
		// double maxDis = 0;
		for (int i = 0; i < size; i++) {
			Clus[i].rho = Clus[i].rho * coef;
			dis = p.getDisTo(Clus[i].center);
			Clus[i].dis = dis;
			if (dis < minDis) {
				minDis = dis;
				index = i;
			}
		}

		p.minDis = minDis;
		// p.maxDis = maxDis;
		CluCell cc = Clus[index];
		if (minDis <= CluR) {
			Clus[index].insert(p);
			if (opt == 0) {
				adjustNoOpt(index, minRho, p);
			}
			if (opt == 1) {
				adjustOpt1(index, minRho, p);
			}

			if (opt == 2) {
				adjust(index, minRho, p);
			}

			if (opt == -1) {
				adjustNoDelta(index, minRho, p);
			}
		}
		return cc;
	}

	private void adjustNoDelta(int index, double minRho, Point p) {

		Clus[0].delta = Double.POSITIVE_INFINITY;
		CluCell clu = Clus[index];

		if (index > 0) {
			for (int i = index - 1; i >= 0; i--) {
				if (clu.rho > Clus[i].rho) {
					Clus[i + 1] = Clus[i];
					Clus[i] = clu;
				} else {
					break;
				}
			}
		}
	}

	// without optimize
	private void adjustNoOpt(int index, double minRho, Point p) {
		Clus[0].delta = Double.POSITIVE_INFINITY;
		CluCell clu = Clus[index];
		int position = index;

		if (index > 0) {
			for (int i = index - 1; i >= 0; i--) {
				if (clu.rho > Clus[i].rho) {
					Clus[i + 1] = Clus[i];
					Clus[i] = clu;
					position = i;
				} else {
					break;
				}
			}
		}
		// if(p.id == 14731){
		// System.out.println(position);
		// }
		if (Clus[0] == clu) {
			clu.dep = null;
			clu.delta = Double.POSITIVE_INFINITY;
		}

		computeDeltaNoOpt(position);
		computeHeadDelta();
	}

	public void computeDeltaNoOpt(int index) {
		CluCell clu = Clus[index];
		if (clu.dep != null) {
			clu.dep.removeSucor(clu);
			clu.dep = null;
		}
		double dis = 0;
		clu.delta = Double.POSITIVE_INFINITY;
		for (int i = size - 1; i >= 0; i--) {
			if (i < index) {
				dis = clu.center.getDisTo(Clus[i].center);
				if (clu.delta > dis) {
					if (clu.dep != null) {
						clu.dep.removeSucor(clu);
					}
					clu.delta = dis;
					clu.dep = Clus[i];
					Clus[i].addSuccessor(clu);
				}
			}
			if (i > index) {
				if (Clus[i].delta > dis) {
					if (Clus[i].dep != null) {
						Clus[i].dep.removeSucor(Clus[i]);
					}
					Clus[i].dep = clu;
					clu.addSuccessor(Clus[i]);
					Clus[i].delta = dis;
				}
			}
		}
	}

	private void adjustOpt1(int index, double minRho, Point p) {
		Clus[0].delta = Double.POSITIVE_INFINITY;
		CluCell clu = Clus[index];
		if (clu.dep != null && clu.dep.rho < clu.rho) {
			clu.dep.removeSucor(clu);
			clu.delta = Double.POSITIVE_INFINITY;
			clu.dep = null;
		}
		int position = index;
		double dis = 0;

		if (index > 0) {
			for (int i = index - 1; i >= 0; i--) {
				if (clu.rho > Clus[i].rho) {
					dis = Clus[i].getDisTo(clu);
					if (dis <= Clus[i].delta) {
						if (Clus[i].dep != null) {
							Clus[i].dep.removeSucor(Clus[i]);
						}
						Clus[i].dep = clu;
						clu.addSuccessor(Clus[i]);
						Clus[i].delta = dis;
					}
					Clus[i + 1] = Clus[i];
					Clus[i] = clu;
					position = i;
				} else {
					break;
				}
			}
		}
		if (Clus[0] == clu) {
			clu.dep = null;
			clu.delta = Double.POSITIVE_INFINITY;
			position = 0;
		}

		if (position != 0 && (clu.dep == null || clu.rho > clu.dep.rho)) {

			clu.delta = Double.POSITIVE_INFINITY;

			computeDeltaF1(position);
		}
		computeHeadDelta();
	}

	private void computeDeltaF1(int index) {
		CluCell clu = Clus[index];
		if (clu.dep != null) {
			clu.dep.removeSucor(clu);
			clu.dep = null;
		}
		clu.delta = Double.POSITIVE_INFINITY;
		if (index == 0) {
			return;
		}
		double dis = 0;

		// with one optimization

		for (int i = index - 1; i >= 0; i--) {
			dis = clu.center.getDisTo(Clus[i].center);
			if (dis < clu.delta) {
				clu.dep = Clus[i];
				clu.delta = dis;
			}
		}

		if (clu.dep != null) {
			clu.dep.addSuccessor(clu);
		}
	}

	// with optimize
	private void adjust(int index, double minRho, Point p) {
		Clus[0].delta = Double.POSITIVE_INFINITY;
		CluCell clu = Clus[index];
		if (clu.dep != null && clu.dep.rho < clu.rho) {
			clu.dep.removeSucor(clu);
			clu.delta = Double.POSITIVE_INFINITY;
			clu.dep = null;
		}
		int position = index;
		double dis = 0;

		if (index > 0) {
			for (int i = index - 1; i >= 0; i--) {
				if (clu.rho > Clus[i].rho) {
					if (Clus[i].delta > Clus[i].dis - clu.dis) {
						dis = Clus[i].getDisTo(clu);
						if (dis < Clus[i].delta) {
							if (Clus[i].dep != null) {
								Clus[i].dep.removeSucor(Clus[i]);
							}
							Clus[i].dep = clu;
							clu.addSuccessor(Clus[i]);
							Clus[i].delta = dis;
						}
					}
					Clus[i + 1] = Clus[i];
					Clus[i] = clu;
					position = i;
				} else {
					break;
				}
			}
		}
		if (Clus[0] == clu) {
			clu.dep = null;
			clu.delta = Double.POSITIVE_INFINITY;
		}
		// if(p.id == 29585){
		// System.out.println(" positino" + position);
		// }
		if (position != 0 && (clu.dep == null || clu.rho > clu.dep.rho)) {

			clu.delta = Double.POSITIVE_INFINITY;

			computeDelta(position);
		}
		computeHeadDelta();
	}

	private void computeHeadDelta() {
		CluCell clu = Clus[0];
		if (clu.dep != null) {
			clu.dep.removeSucor(clu);
			clu.dep = null;
		}

		double maxValue = 0;
		double secondValue = 0;
		for (int i = 1; i < size; i++) {
			if (maxValue < Clus[i].delta) {
				secondValue = maxValue;
				maxValue = Clus[i].delta;
			} else if (secondValue < Clus[i].delta) {
				secondValue = Clus[i].delta;
			}
		}
		if (maxValue > 3 * secondValue) {
			clu.delta = maxValue;
		} else {
			clu.delta = (maxValue + secondValue) / 2;
		}

		double dis = 0;
		// for (int i = 1; i < size; i++) {
		// dis = clu.getDisTo(Clus[i].delta);
		// if (dis > maxValue) {
		// maxValue = dis;
		// }
		// }
		// clu.delta = maxValue;

//		for (int i = 1; i < size; i++) {
//			dis = Clus[i].delta;
//			if (dis > maxValue) {
//				maxValue = dis;
//			}
//		}
//		clu.delta = maxValue;

		return;

	}

	private void computeDelta(int index) {

		CluCell clu = Clus[index];
		if (clu.dep != null) {
			clu.dep.removeSucor(clu);
			clu.dep = null;
		}
		clu.delta = Double.POSITIVE_INFINITY;
		if (index == 0) {
			return;
		}
		double dis = 0;

		for (int i = index - 1; i >= 0; i--) {
			if (clu.delta > Clus[i].dis - clu.dis) {
				dis = clu.center.getDisTo(Clus[i].center);

				if (dis < clu.delta) {
					clu.dep = Clus[i];
					clu.delta = dis;
				}
			}
		}

		if (clu.dep != null) {
			clu.dep.addSuccessor(clu);
		}
	}

	public void deleteInact(OutlierReservoir outres, double coef,
			double minRho, long time) {
		// check(outres);
		for (int i = size - 1; i > 0; i--) {

			if (Clus[i].rho < minRho) {
				CluCell cc = Clus[i];
				Clus[i] = null;
				size--;
				cc.active = false;
				cc.inactiveTime = time;
				cc.dep.sucs.remove(cc);
				cc.cluster.remove(cc);
				outres.insert(cc);
			} else {
				break;
			}
		}
		if (size > 0 && Clus[0].rho < minRho) {
			CluCell cc = Clus[0];
			Clus[0] = null;
			size--;
			cc.active = false;
			cc.inactiveTime = time;
			cc.cluster.remove(cc);
			outres.insert(cc);
		}
	}

	public double computeAlpha(double minDelta) {

		double[] deltas = new double[size];
		for (int i = 0; i < size; i++) {
			deltas[i] = Clus[i].delta;
		}
		Arrays.sort(deltas);
		double delta1;
		double delta2;
		int m = 0;
		int n = 0;
		double avg;
		double up = 0;
		double down = 0;
		int i = 0;
		for (i = 0; i < size - 1 && deltas[i] < minDelta; i++) {
			n++;
			down += deltas[i];

		}
		delta1 = deltas[i - 1];
		delta2 = deltas[i];
		for (; i < size; i++) {
			m++;
			up += deltas[i];
		}
		avg = up + down;
		avg = avg / (m + n);
		up = up / m;
		down = down / n;
		double alpha = (up * (down - delta1) * (m * up + delta1))
				/ (avg * avg * (delta1 - up) * (n - 1) + (down - delta1) * up
						* (m * up + delta1));
		double alpha2 = ((delta2 - down) * up * (m * up - delta2))
				/ ((delta2 - down) * up * (m * up - delta2) + avg * avg
						* (up - delta2) * (n + 1));
		if (alpha < alpha2) {
			return (alpha + alpha2) / 2;
		} else {
			return 0;
		}
	}

	public double adjustMinDelta(double alpha) {
		if (size < 2) {
			return 0;
		}
		double[] deltas = new double[size];
		for (int i = 0; i < size; i++) {
			deltas[i] = Clus[i].delta;
		}
		Arrays.sort(deltas);
		int m = 0;
		int n = 0;
		double avg = 0;
		double up = 0;
		double down = 0;
		// double max = deltas[size - 1];
		// deltas[size - 1] = deltas[size - 2];
		up = deltas[size - 1];
		for (int i = 0; i < size - 1; i++) {
			down += deltas[i];
		}
		n = size - 1;

		m = 1;
		avg = (up + down) / (m + n);
		double score = fun(alpha, up / m, down / n, avg);
		int index = size - 2;
		up += deltas[index];
		m++;
		down -= deltas[index];
		n--;
		double scoredown = fun(alpha, up / m, down / n, avg);
		while (score > scoredown && index > 0) {
			score = scoredown;
			index--;
			up += deltas[index];
			m++;
			down -= deltas[index];
			n--;
			scoredown = fun(alpha, up / m, down / n, avg);
		}
		return (deltas[index + 1] + deltas[index]) / 2;

		// if (size - index + 1 > Util.maxCluNum) {
		// deltas[size - 1] = max;
		// m = 0;
		// n = 0;
		// avg = 0;
		// up = 0;
		// down = 0;
		// for (int i = 0; i < size - 1; i++) {
		// down += deltas[i];
		// }
		// n = size - 1;
		// // if (i >= size) {
		// // return deltas[i - 1];
		// // }
		//
		// up = deltas[size - 1];
		// m = 1;
		// avg = (up + down) / (m + n);
		// score = fun(alpha, up / m, down / n, avg);
		// index = size - 2;
		// up += deltas[index];
		// m++;
		// down -= deltas[index];
		// n--;
		// scoredown = fun(alpha, up / m, down / n, avg);
		// while (score > scoredown && index > 0) {
		// score = scoredown;
		// index--;
		// up += deltas[index];
		// m++;
		// down -= deltas[index];
		// n--;
		// scoredown = fun(alpha, up / m, down / n, avg);
		// }
		// if (index == 0) {
		// return deltas[0];
		// } else {
		// return (deltas[index + 1] + deltas[index]) / 2;
		// }
		// } else {
		// if (index == 0) {
		// return deltas[index + 1] + deltas[index] / 2;
		// } else {
		// return (deltas[index + 1] + deltas[index]) / 2;
		// }
		// }
	}

	public double djustMinDelta(double alpha, double minDelta) {
		if (size < 2) {
			return 0;
		}
		double[] deltas = new double[size];
		for (int i = 0; i < size; i++) {
			deltas[i] = Clus[i].delta;
		}
		Arrays.sort(deltas);
		double max = deltas[size - 1];
		deltas[size - 1] = deltas[size - 2];
		int m = 0;
		int n = 0;
		double avg = 0;
		double up = 0;
		double down = 0;
		for (int i = 0; i < size - 1; i++) {
			down += deltas[i];
		}
		n = size - 1;
		// if (i >= size) {
		// return deltas[i - 1];
		// }

		up = deltas[size - 1];
		m = 1;
		avg = (up + down) / (m + n);
		double score = fun(alpha, up / m, down / n, avg);
		int index = size - 2;
		up += deltas[index];
		m++;
		down -= deltas[index];
		n--;
		double scoredown = fun(alpha, up / m, down / n, avg);
		while (score > scoredown && index > 0) {
			score = scoredown;
			index--;
			up += deltas[index];
			m++;
			down -= deltas[index];
			n--;
			scoredown = fun(alpha, up / m, down / n, avg);
		}
		if (size - index + 1 > Util.maxCluNum) {
			System.out.println("真的有大于的");
			deltas[size - 1] = max;
			m = 0;
			n = 0;
			avg = 0;
			up = 0;
			down = 0;
			for (int i = 0; i < size - 1; i++) {
				down += deltas[i];
			}
			n = size - 1;
			// if (i >= size) {
			// return deltas[i - 1];
			// }

			up = deltas[size - 1];
			m = 1;
			avg = (up + down) / (m + n);
			score = fun(alpha, up / m, down / n, avg);
			index = size - 2;
			up += deltas[index];
			m++;
			down -= deltas[index];
			n--;
			scoredown = fun(alpha, up / m, down / n, avg);
			while (score > scoredown && index > 0) {
				score = scoredown;
				index--;
				up += deltas[index];
				m++;
				down -= deltas[index];
				n--;
				scoredown = fun(alpha, up / m, down / n, avg);
			}
			if (index == 0) {
				return deltas[0];
			} else {
				return (deltas[index + 1] + deltas[index]) / 2;
			}
		} else {
			return (deltas[index + 1] + deltas[index]) / 2;
		}

	}

	private double fun(double alpha, double upavg, double downavg, double avg) {
		return alpha * (avg / upavg) + (1 - alpha) * (downavg / avg);
	}

	private int binarySearch(double[] array, double value) {
		int min = 0;
		int max = size - 1;
		while (min < max) {

		}
		int length = array.length;
		int middle = (0 + size) / 2;
		if (array[middle] > value) {

		} else {

		}
		return 0;
	}

	public void print(BufferedWriter bw, double minDelta,
			HashSet<Cluster> clusters) throws IOException {
		// check(clusters);
		Iterator<Cluster> itr = clusters.iterator();
		while (itr.hasNext()) {
			Cluster c = itr.next();
			Iterator<CluCell> itr2 = c.cells.iterator();
			while (itr2.hasNext()) {
				CluCell cc = itr2.next();
				bw.write(cc.cid + " ");
			}
			bw.write("\n");
		}
	}

	private void recPrint(CluCell clu, BufferedWriter bw, double minRho) {
		clu.print(bw);

		if (clu.hasSuccessor()) {
			Iterator<CluCell> itr = clu.sucs.iterator();
			while (itr.hasNext()) {
				CluCell temp = itr.next();
				recPrint(temp, bw, minRho);
			}
		}
	}

	public void checkNull(CluCell cc) {
		for (int i = 1; i < size; i++) {
			if (cc != Clus[i] && Clus[i].dep == null) {
				System.out.println("有空指针 " + i);
				System.exit(0);
			}
		}
	}

	public boolean check(OutlierReservoir outres) {
		boolean flag = true;
		HashSet<Integer> set = new HashSet<Integer>();
		// check density ralationship
		set.add(Clus[0].cid);
		if (Clus[0].dep != null) {
			flag = false;
			System.out.println("头不空 " + 0 + " " + Clus[0].cid);
			return flag;
		}
		if (Clus[0].cluster == null) {
			System.out.println("there are cluCell not in cluster!!");
			System.exit(0);
		}
		for (int i = 1; i < size; i++) {
			if (Clus[i].cluster == null) {
				System.out.println("there are cluCell not in cluster!!");
				System.exit(0);
			}
			if (set.contains(Clus[i].cid)) {
				flag = false;
				System.out.println("有重复元素 " + i + " " + Clus[i].cid);
				return flag;
			} else {
				set.add(Clus[i].cid);
			}
			if (Clus[i].dep == null) {
				flag = false;
				System.out.println("有空指针 " + i + " " + Clus[i].cid + " "
						+ Clus[i]);
				System.exit(0);
				return flag;
			}
			if (Clus[i - 1].rho < Clus[i].rho) {
				flag = false;
				System.out.println("密度关系错误  " + i + " " + Clus[i].cid);
				return flag;
			}
		}
		Iterator<CluCell> itr = outres.outliers.iterator();
		while (itr.hasNext()) {
			CluCell temp = itr.next();
			if (set.contains(temp.cid)) {
				flag = false;
				System.out.println("outres有重复  " + temp.cid);
				outres.print();
				return flag;
			} else {
				set.add(temp.cid);
			}
		}
		return flag;
	}

	public void check(HashSet<Cluster> clusters) {

		int num = 0;
		HashSet<CluCell> set = new HashSet<CluCell>();
		Iterator<Cluster> itr = clusters.iterator();
		while (itr.hasNext()) {
			Cluster c = itr.next();
			Iterator<CluCell> citr = c.cells.iterator();
			while (citr.hasNext()) {
				CluCell cc = citr.next();
				if (set.contains(cc)) {
					System.out.println("there is chong fu shuju");
					System.exit(0);
				} else {
					set.add(cc);
				}
				num++;
			}
		}

		// System.out.println("num2 = " + num2);

		if (num != size) {
			System.out.println("activeCell num = " + size);
			System.out.println("clusterCell num = " + num);
			System.out.println("hehe le ++++++++");
			System.exit(0);
		}
	}

	public void trackCluster(HashSet<Cluster> clusters) {
		System.out.println("++++++++++++++++++++");
		HashSet<Cluster> set = new HashSet<Cluster>();
		if (Clus[0] == null) {
			System.out.println(size);
			System.out.println("there is no cluser-cell, r is very small");
			System.out.println("please adjust your r parameter larger");
			System.exit(0);
		}
		if (Clus[0].cluster == null) {
			Cluster cluster = new Cluster(cluLabel++);
			cluster.add(Clus[0]);
			clusters.add(cluster);
			set.add(cluster);
		} else {
			set.add(Clus[0].cluster);
			System.out.println(Clus[0].cluster);
		}

		for (int i = 1; i < size; i++) {
			if (Clus[i].delta >= minDelta) {// clusters split or new cluster
											// appears
				if (Clus[i].dep == null) {
					System.err.println("error contains null dep");
				}
				if (Clus[i].cluster == Clus[i].dep.cluster) {// clusters split
					Cluster c1 = Clus[i].cluster;
					Cluster c2 = new Cluster(cluLabel++);
					if (c1 != null) {
						c1.remove(Clus[i]);
					}
					c2.add(Clus[i]);
					clusters.add(c2);
					set.add(c2);
					System.out.println(c2);
				} else if (Clus[i].cluster == null) {// new cluster appear
					Cluster cluster = new Cluster(cluLabel++);
					clusters.add(cluster);
					cluster.add(Clus[i]);
					set.add(cluster);
					System.out.println(cluster);
				} else {
					if (set.contains(Clus[i].cluster)) {
						System.err.println("why there is cluster?");
						System.out.println(Clus[i].cluster);
						System.out.println(Clus[i].dep.cluster);
						System.exit(0);
					}
				}
			} else {
				if (Clus[i].dep == null) {
					System.err.println("error contains null dep");
				}
				if (Clus[i].cluster != Clus[i].dep.cluster) {
					Cluster c1 = Clus[i].cluster;
					Cluster c2 = Clus[i].dep.cluster;
					if (c1 != null) {
						c1.remove(Clus[i]);
					}
					c2.add(Clus[i]);
				}
			}
		}
	}

	public void adjustCluster(HashSet<Cluster> clusters, boolean minChed) {
		
		HashSet<Cluster> set = new HashSet<Cluster>();//

		if (Clus[0] == null) {
			System.out.println(size);
			System.out.println("there is no cluser-cell, r is very small");
			System.out.println("please adjust your r parameter larger");
			System.exit(0);
		}
		if (Clus[0].cluster == null) {// there is new cluster center appearing
			Cluster cluster = new Cluster(cluLabel++);
			clusters.add(cluster);
			cluster.add(Clus[0]);
			set.add(cluster);
		} else {
			set.add(Clus[0].cluster);
		}

		for (int i = 1; i < size; i++) {
			if (Clus[i].delta >= minDelta) {// clusters split or new cluster
											// appears
				if (Clus[i].dep == null) {
					System.err.println("error contains null dep");
				}
				if (Clus[i].cluster == Clus[i].dep.cluster) {// clusters split
					Cluster c1 = Clus[i].cluster;
					Cluster c2 = new Cluster(cluLabel++);
					if (c1 != null) {
						c1.remove(Clus[i]);
					}
					c2.add(Clus[i]);
					clusters.add(c2);
					set.add(c2);
				}
				if (Clus[i].cluster == null) {// new cluster appears
					Cluster cluster = new Cluster(cluLabel++);
					clusters.add(cluster);
					cluster.add(Clus[i]);
					set.add(cluster);
				} else {
					if (set.contains(Clus[i].cluster)) { // this is because when
															// minDelta become
															// smaller
						Clus[i].cluster.remove(Clus[i]);
						Cluster cluster = new Cluster(cluLabel++);
						clusters.add(cluster);
						cluster.add(Clus[i]);
						set.add(cluster);
					} else {
						set.add(Clus[i].cluster);
					}
				}

			} else {
				if (Clus[i].dep == null) {
					System.err.println("error contains null dep");
				}
				if (Clus[i].cluster != Clus[i].dep.cluster) {
					Cluster c1 = Clus[i].cluster;
					Cluster c2 = Clus[i].dep.cluster;
					if (c1 != null) {
						c1.remove(Clus[i]);
					}
					c2.add(Clus[i]);
				}
			}
		}
	}

	public void writeInfo(String string) throws IOException {

		BufferedWriter bw = new BufferedWriter(new FileWriter(string));
		for (int i = 0; i < size; i++) {
			bw.write(Clus[i].rho + "\t" + Clus[i].delta + "\n");
		}
		bw.close();
	}

	public void testCluster(HashSet<Cluster> clusterSet, double minRho)
			throws IOException {
		if (!Clus[0].cluster.cells.contains(Clus[0])) {
			System.out.println("not in cluster");
			System.exit(0);
		}
		int clusterNum = 1;
		// BufferedWriter bw = new BufferedWriter(new FileWriter(
		// "./src/EDMStream/kdd/test.txt"));
		for (int i = 1; i < size; i++) {
			if (!Clus[i].cluster.cells.contains(Clus[i])) {
				System.out.println("not in cluster");
				System.exit(0);
			}
			if (Clus[i].delta < minDelta
					&& Clus[i].cluster != Clus[i].dep.cluster) {
				System.out.println("not in same cluster");
				System.exit(0);
			}
			if (Clus[i].delta >= minDelta
					&& Clus[i].cluster == Clus[i].dep.cluster) {
				System.out.println("in same cluster");
				System.exit(0);
			}
			if (Clus[i].delta >= minDelta) {
				clusterNum++;
			}
			// bw.write(i + " " + Clus[i] + " " + Clus[i].cluster + "\n");
		}
		// bw.close();
		if (clusterNum != 0 && clusterNum != clusterSet.size()) {
			System.out.println("clusterNum = " + clusterNum);
			System.out.println("clusterSet size = " + clusterSet.size());

			System.out.println("clusterNum is error");
			Iterator<Cluster> itr = clusterSet.iterator();
			while (itr.hasNext()) {
				boolean b = false;
				Cluster c = itr.next();
				if (!c.cells.isEmpty()) {
					int i = 0;
					Iterator<CluCell> itr2 = c.cells.iterator();
					HashSet<CluCell> set = new HashSet<CluCell>();
					while (itr2.hasNext()) {
						CluCell cc = itr2.next();
						if (cc.delta >= minDelta) {
							i++;
							set.add(cc);
							b = true;
						}
						if (cc.cluster != c) {
							System.out.println("not belong to ++++++");
							System.exit(0);
						}
					}
					if (b) {
						System.out.println("okok");
					} else {
						System.out.println("nonono");
						System.out.println(c);
						System.exit(0);
					}
					if (i != 1) {
						System.out.println(i);
						System.out.println(set.size());
						System.out.println(minDelta);
						System.out.println(minRho);
						for (CluCell cccc : set) {
							System.out.println(cccc.rho + " " + cccc.delta
									+ " " + cccc.cluster);
						}
						System.out.println("no is one++++++");
						System.out.println(c);
						System.exit(0);
					}
				} else {
					System.out.println("is empty");
				}

			}

			System.exit(0);
		}
	}

	public void checkLoop() {
		System.out.println("checkLoop+++++++++++++=");
		for (int i = 0; i < size - 1; i++) {
			// if (Clus[i].rho < Clus[i + 1].rho) {
			// System.out.println("not in sort" + i);
			// System.exit(0);
			// }
			// if (i != 0 && Clus[i].rho > Clus[i].dep.rho) {
			// System.out.println(i);
			// System.out.println("there is loop" + i);
			// System.exit(0);
			// }
			Iterator<CluCell> itr = Clus[i].sucs.iterator();
			while (itr.hasNext()) {
				CluCell c = itr.next();
				if (c.dep != Clus[i]) {
					System.out.println(c.dep);
					System.out.println(c);
					System.out.println(i);
					for (int j = 0; j < size; j++) {
						if (c.dep == Clus[j]) {
							System.out.println("dep is " + j);
						}
					}
					System.out.println("dep relationship is error++++");
					System.exit(0);
				}
			}
		}

		HashSet<CluCell> set = new HashSet<CluCell>();
		Queue<CluCell> queue = new LinkedList<CluCell>();
		Iterator<CluCell> itr = Clus[0].sucs.iterator();
		while (itr.hasNext()) {
			CluCell itrc = itr.next();
			queue.add(itrc);
		}
		if (queue.isEmpty()) {
			// System.out.println("head's success is null");
			itr = Clus[1].sucs.iterator();
			while (itr.hasNext()) {
				CluCell itrc = itr.next();
				queue.add(itrc);
			}
		}
		int i = 0;
		while (!queue.isEmpty()) {
			i++;
			CluCell cc = queue.remove();
			if (set.contains(cc)) {
				System.out.println(i);
				System.out.println("there is loop+++++++++");
				System.exit(0);
			} else {
				set.add(cc);
			}
			// System.out.println(cc);
			Iterator<CluCell> itr2 = cc.sucs.iterator();
			while (itr2.hasNext()) {
				CluCell itrc = itr2.next();
				queue.add(itrc);
			}
		}

		// System.exit(0);
	}

}
