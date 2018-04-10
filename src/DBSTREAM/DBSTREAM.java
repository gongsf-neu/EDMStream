package DBSTREAM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

public class DBSTREAM {

	double r;
	double lambda;
	double a;
	long gapTime;// t_gap
	double alpha;
	double weak;
	long lt;
	Set<MicroCluster> MC;
	Map<MyKey, Adj> S;
	double aweak;
	double wmin;
	int mcid;

	public DBSTREAM(double r, double lambda, double a, long gapTime,
			double alpha) {
		this.gapTime = gapTime;
		this.a = a;
		this.r = r;
		this.lambda = lambda;
		this.alpha = alpha;
		weak = Math.pow(a, -lambda * gapTime);
		aweak = alpha * weak;
		MC = new HashSet<MicroCluster>();
		S = new HashMap<MyKey, Adj>();
		mcid = 0;
		lt = 0;
		System.out.println("r=" + r);
		System.out.println("weak=" + weak);
		System.out.println("aweak=" + aweak);
		System.out.println("gaptime=" + gapTime);
	}

	public List<MicroCluster> update(Point p) {
		if (p.id == 0) {
			lt = p.startTime;
		}
		double defactor = Util.decayFun(p.startTime, lt);
		lt = p.startTime;
		List<MicroCluster> NN = findRNN(p, defactor);
		int size = NN.size();
//		System.out.println(size);
		if (NN.size() < 1) {
			MicroCluster mc = new MicroCluster(mcid++, p, r);
			MC.add(mc);
			NN.add(mc);
		} else {
			for (int i = 0; i < size; i++) {
				MicroCluster mc = NN.get(i);
				mc.add(p); // just update weight

				// update shared density
				for (int j = i + 1; j < size; j++) {
					MyKey mk = new MyKey(mc, NN.get(j));
					if (S.containsKey(mk)) {
						S.get(mk).add(p);
					} else {
						Adj adj = new Adj(p);
						S.put(mk, adj);
					}
				}
			}
			if (checkMove(NN)) {
				for (MicroCluster MC : NN) {
					MC.move();
				}
			}
		}

		if (p.id % gapTime == 0 && p.id != 0) {
			cleanUp(p.startTime);
			// test(p.startTime);
		}
		// test2(p.startTime, NN);
		return NN;
	}

	private boolean checkMove(List<MicroCluster> NN) {
		List<MicroCluster> list = NN;
		int size = list.size();
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				if (list.get(i).getVecDisTo(list.get(j)) < r) {
					return false;
				}
			}
		}
		return true;
	}

	private List<MicroCluster> findRNN(Point p, double defactor) {
		List<MicroCluster> result = new ArrayList<MicroCluster>();
		Iterator<MicroCluster> itr = MC.iterator();
		while (itr.hasNext()) {
			MicroCluster mc = itr.next();
			mc.decay(defactor);
			double dis = mc.getDisTo(p);
			if (dis < r) {
				result.add(mc);
			}
		}
		return result;
	}

	public void cleanUp(long time) {
		Set<MicroCluster> removes = new HashSet<MicroCluster>();
		Iterator<MicroCluster> itr = MC.iterator();
		while (itr.hasNext()) {
			MicroCluster mc = itr.next();
			if (mc.getCurWeight() <= weak) {
				removes.add(mc);
				itr.remove();
			}
		}
		Iterator<Entry<MyKey, Adj>> itrs = S.entrySet().iterator();
		while (itrs.hasNext()) {
			Entry<MyKey, Adj> entry = itrs.next();
			MyKey mk = entry.getKey();
			if (removes.contains(mk.mc1) || removes.contains(mk.mc2)) {
				itrs.remove();
			} else {
				Adj adj = entry.getValue();
				if (adj.getCurWeight(time) < aweak) {
					itrs.remove();
				}
			}
		}
	}

	public Set<Cluster> recluster(long time, double thres) {
		Map<MicroCluster, HashSet<MicroCluster>> C = new HashMap<MicroCluster, HashSet<MicroCluster>>();
		Iterator<Entry<MyKey, Adj>> itrs = S.entrySet().iterator();
		while (itrs.hasNext()) {
			Entry<MyKey, Adj> entry = itrs.next();
			MyKey mk = entry.getKey();
			MicroCluster mc1 = mk.mc1;
			MicroCluster mc2 = mk.mc2;
			if (mc1.getCurWeight() >= wmin && mc2.getCurWeight() >= wmin) {
				double val = entry.getValue().weight * 2
						/ (mc1.weight + mc2.weight);
				if (val > thres) {
					if (C.containsKey(mc1)) {
						C.get(mc1).add(mc2);
					} else {
						mc1.visited = false;
						HashSet<MicroCluster> set = new HashSet<MicroCluster>();
						C.put(mc1, set);
						set.add(mc2);
					}

					if (C.containsKey(mc2)) {
						C.get(mc2).add(mc1);
					} else {
						mc2.visited = false;
						HashSet<MicroCluster> set = new HashSet<MicroCluster>();
						C.put(mc2, set);
						set.add(mc1);
					}
				} else {
					if (!C.containsKey(mc1)) {
						mc1.visited = false;
						HashSet<MicroCluster> set = new HashSet<MicroCluster>();
						C.put(mc1, set);
					}

					if (!C.containsKey(mc2)) {
						mc2.visited = false;
						HashSet<MicroCluster> set = new HashSet<MicroCluster>();
						C.put(mc2, set);
					}
				}
			}
		}
		return findConnectedComponents(C, thres);
	}

	public Set<Cluster> findConnectedComponents(
			Map<MicroCluster, HashSet<MicroCluster>> C, double thres) {
		int cid = 0;
		Set<Cluster> clusters = new HashSet<Cluster>();
		Iterator<Entry<MicroCluster, HashSet<MicroCluster>>> itr = C.entrySet()
				.iterator();
		while (itr.hasNext()) {
			Entry<MicroCluster, HashSet<MicroCluster>> entry = itr.next();
			if (!entry.getKey().visited) {
				Cluster CS = new Cluster(cid);
				clusters.add(CS);
				cid++;
				Queue<MicroCluster> queue = new LinkedList<MicroCluster>();
				queue.add(entry.getKey());
				while (!queue.isEmpty()) {
					MicroCluster mc = queue.poll();
					CS.add(mc);
					mc.visited = true;
					Iterator<MicroCluster> itrr = entry.getValue().iterator();
					while (itrr.hasNext()) {
						MicroCluster mcc = itrr.next();
						if (!mcc.visited) {
							queue.add(mcc);
						}
					}
				}
			}
		}
		return clusters;
	}

	public void test2(long time, List<MicroCluster> list) {
		MicroCluster mc1 = null;
		MicroCluster mc2 = null;
		Iterator<MicroCluster> itr = MC.iterator();
		while (itr.hasNext()) {
			MicroCluster mc = itr.next();
			if (mc.id == 1) {
				mc1 = mc;
			}
			if (mc.id == 5) {
				mc2 = mc;
			}
		}

		// System.out.print("the nearest mc : ");
		// for(MicroCluster mc : list){
		// System.out.print(mc.id + " ");
		// }
		// System.out.println();
		// if(mc1 != null && mc2 != null && S.containsKey(new MyKey(mc1, mc2))){
		// System.out.println(mc1.id + " " +mc1.getCurWeight());
		// System.out.println(mc2.id + " " +mc2.getCurWeight());
		// System.out.println("adj weight" + S.get(new MyKey(mc1,
		// mc2)).getCurWeight(time));
		// }

		// System.out.println("mc number=" + MC.size());
		// System.out.println("adj number=" + S.size());
		Iterator<Entry<MyKey, Adj>> itrs = S.entrySet().iterator();
		while (itrs.hasNext()) {
			Entry<MyKey, Adj> entry = itrs.next();
			MyKey mk = entry.getKey();
			if (!MC.contains(mk.mc1) || !MC.contains(mk.mc2)) {
				System.out.println("invalid adj");
				System.exit(0);
			}
			if (mk.mc1 == mk.mc2) {
				System.out.println("invalid adj2");
				System.exit(0);
			}

			Adj adj = entry.getValue();
			if (mk.mc1.getCurWeight() < adj.getCurWeight(time)
					|| mk.mc2.getCurWeight() < adj.getCurWeight(time)) {
				System.out.println(mk.mc1.id + " " + mk.mc1.getCurWeight());
				System.out.println(mk.mc2.id + " " + mk.mc2.getCurWeight());
				System.out.println(adj.getCurWeight(time));
				System.out
						.println("invalid adj3 the weight of two mc is smaller than their adj");
				System.exit(0);
			}
		}
	}

	public void test(long time) {
		Iterator<MicroCluster> itr = MC.iterator();
		while (itr.hasNext()) {
			MicroCluster mc = itr.next();
			if (mc.getCurWeight() <= weak) {
				System.out.println(mc.getCurWeight());
				System.out.println("there are weak mc");
				System.exit(0);
			}
		}
		Iterator<Entry<MyKey, Adj>> itrs = S.entrySet().iterator();
		while (itrs.hasNext()) {
			Entry<MyKey, Adj> entry = itrs.next();
			MyKey mk = entry.getKey();
			if (!MC.contains(mk.mc1) || !MC.contains(mk.mc2)) {
				System.out.println("invalid adj");
				System.exit(0);
			}
			if (mk.mc1 == mk.mc2) {
				System.out.println("invalid adj2");
				System.exit(0);
			}

			Adj adj = entry.getValue();
			if (mk.mc1.getCurWeight() < adj.getCurWeight(time)
					|| mk.mc2.getCurWeight() < adj.getCurWeight(time)) {
				System.out.println(mk.mc1.getCurWeight());
				System.out.println(mk.mc2.getCurWeight());
				System.out.println(adj.getCurWeight(time));
				System.out.println("invalid adj3");
				System.exit(0);
			}
		}
	}
}
