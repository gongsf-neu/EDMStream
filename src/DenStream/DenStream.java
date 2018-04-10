package DenStream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class DenStream {

	double epsilon;
	double beta;
	double mu;
	double minw;
	double lambda;
	double a;
	long t_p;
	boolean isInitial = false;
	List<Point> initBuffer;
	int initSize;
	Set<MicroCluster> p_micro_cluster;
	Set<MicroCluster> o_micro_cluster;
	int mcid;
	List<Cluster> results;

	public DenStream(double epsilon, double beta, double mu, double lambda,
			double a, int initSize) {
		this.epsilon = epsilon;
		this.beta = beta;
		this.mu = mu;
		this.lambda = lambda;
		this.a = a;
		minw = beta * mu;
		t_p = (long) Math.floor((1 / lambda)
				* (Math.log((minw) / (minw - 1)) / Math.log(Util.a)));
		initBuffer = new ArrayList<Point>();
		this.initSize = initSize;
		p_micro_cluster = new HashSet<MicroCluster>();
		o_micro_cluster = new HashSet<MicroCluster>();
		mcid = 0;
		System.out.println("minw=" + minw);
		System.out.println("t_p=" + t_p);
	}

	public void process(Point p) {
		if (!isInitial) {
			initBuffer.add(p);
			if (initBuffer.size() > initSize) {
				init();
				isInitial = true;
			}
		} else {

			merge(p);

			if (p.id % t_p == 0 && p.id != 0) {
				Iterator<MicroCluster> itr = p_micro_cluster.iterator();
				while (itr.hasNext()) {
					MicroCluster mc = itr.next();
					if (mc.getWeight(p.startTime) < minw) {
						mc.to = p.startTime;
						itr.remove();
					}
				}
				Iterator<MicroCluster> itr2 = o_micro_cluster.iterator();
				while (itr2.hasNext()) {
					MicroCluster mc = itr2.next();
					if (mc.getWeight(p.startTime) < getWeight(p.startTime,
							mc.to)) {
						itr2.remove();
					}
				}
			}
			generateCluster(p.startTime);
		}
	}

	public void generateCluster(long time) {
		Iterator<MicroCluster> itr = p_micro_cluster.iterator();
		while (itr.hasNext()) {
			itr.next().visited = false;
		}
		double thres = 2 * Util.epsilon;
		int Cid = 0;
		results = new ArrayList<Cluster>();
		Iterator<MicroCluster> itr2 = p_micro_cluster.iterator();
		while (itr2.hasNext()) {
			MicroCluster mc = itr2.next();
			if (!mc.visited && mc.getWeight(time) > mu) {
				mc.visited = true;
				Cluster C = new Cluster(Cid);
				results.add(C);
				Cid++;
				C.add(mc);
				Queue<MicroCluster> queue = new LinkedList<MicroCluster>();
				queue.add(mc);
				while (!queue.isEmpty()) {
					MicroCluster seed = queue.poll();
					Iterator<MicroCluster> itr3 = p_micro_cluster.iterator();
					while (itr3.hasNext()) {
						MicroCluster cur = itr3.next();
						if (!cur.visited && cur != seed
								&& seed.getDisTo(cur) < thres) {
							if (cur.getWeight(time) > mu) {
								queue.add(cur);
								cur.visited = true;
								C.add(cur);
							} else {
								cur.visited = true;
								C.add(cur);
							}
						}// end if
					}// end while(query)
				}// end while(queue)
			}
		}
	}

	private double getWeight(long startTime, long to) {
		return (Math.pow(a, -(Util.lambda * (startTime - to + t_p))) - 1)
				/ (Math.pow(a, -(Util.lambda * (t_p))) - 1);
	}

	private void init() {
		int size = initBuffer.size();
		for (int i = 0; i < size; i++) {
			Point p = initBuffer.get(i);
			if (!p.visited) {
				p.visited = true;
				MicroCluster mc = new MicroCluster(mcid++, p);
				List<Point> NN = nearestNeighbor(mc);
				if (mc.getWeight() <= minw) {
					int nsize = NN.size();
					for (int j = 0; j < nsize; j++) {
						NN.get(j).visited = false;
						NN.get(j).mcid = -1;
					}
					p.visited = false;
					p.mcid = -1;
					mcid--;
				} else {
					p_micro_cluster.add(mc);
				}
			}
		}
	}

	private List<Point> nearestNeighbor(MicroCluster mc) {
		List<Point> list = new ArrayList<Point>();
		int size = initBuffer.size();
		for (int i = 0; i < size; i++) {
			Point p = initBuffer.get(i);
			if (!p.visited) {
				if (mc.near(p)) {
					p.visited = true;
					p.mcid = mc.id;
					list.add(p);
				}
			}
		}
		return list;
	}

	public void merge(Point p) {

		boolean merged = false;
		if (p_micro_cluster.size() > 0) {
			MicroCluster nn = nearestNeighbor(p, p_micro_cluster);
			if (nn != null && nn.insert(p)) {
				p.mcid = nn.id;
				merged = true;
				// System.out.println("merge into p_micro");
			}
		}
		if (!merged && o_micro_cluster.size() > 0) {
			MicroCluster nn = nearestNeighbor(p, o_micro_cluster);
			if (nn != null && nn.insert(p)) {
				p.mcid = nn.id;
				merged = true;
				if (nn.getWeight(p.startTime) > minw) {
					p_micro_cluster.add(nn);
					o_micro_cluster.remove(nn);
				}
			}
		}
		if (!merged) {
			MicroCluster mc = new MicroCluster(mcid++, p);
			o_micro_cluster.add(mc);
		}
	}

	private MicroCluster nearestNeighbor(Point p, Set<MicroCluster> set) {
		double dis = 0;
		double minDis = Double.POSITIVE_INFINITY;
		MicroCluster result = null;
		Iterator<MicroCluster> itr = set.iterator();
		while (itr.hasNext()) {
			MicroCluster mc = itr.next();
			dis = mc.getDisTo(p);
			if (minDis > dis) {
				minDis = dis;
				result = mc;
			}
		}
		return result;
	}

	public void test(boolean isMerged, boolean isMergedP, long time) {
		if (isMergedP && !isMergedP) {// merge by o_micro
			for (MicroCluster mc : o_micro_cluster) {
				if (mc.getWeight(time) > minw) {
					System.out.println("the o_micro is larger than min weight");
					System.exit(0);
				}
			}
		}
		if (!isMerged) {
			// System.out.println("new mc");
		}
	}
}
