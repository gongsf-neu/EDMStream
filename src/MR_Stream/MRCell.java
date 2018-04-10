package MR_Stream;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MRCell {

	double weight;
	double density;
	int h;// height of the node
	int id; // identification of mrcell
	long lt; // last update time
	long la; // last absorb time
	double[] minD; // min dimension of this node
	double[] maxD; // max dimension of this node
	double[] middleD;
	double wi; // implicit weight
	MRCell upPoint = null;
	Map<String, MRCell> downPoint = new HashMap<String, MRCell>(); //
	Map<MRCell, String> childMap = new HashMap<MRCell, String>(); //

	public MRCell() {

	}

	public MRCell(int id, double[] minD, double[] maxD, long lt) {
		int dim = minD.length;
		this.minD = minD;
		this.maxD = maxD;
		middleD = new double[dim];
		for (int i = 0; i < dim; i++) {
			middleD[i] = (minD[i] + maxD[i]) / 2;
		}
		weight = 0;
		this.id = id;
		this.lt = lt;
	}

	public MRCell add(Point p, MRTree mrtree) {
		decay(p.startTime);
		add(p);
		if (h == Util.H) {
			return this;
		} else {
			int dim = Util.dim;
			int[] vec = new int[dim];
			double[] pvec = p.vec;
			for (int i = 0; i < dim; i++) {
				if (pvec[i] < middleD[i]) {
					vec[i] = 0;
				} else {
					vec[i] = 1;
				}
			}
			String hg = Util.hashFun(vec);
			if (downPoint.containsKey(hg)) {
				return downPoint.get(hg).add(p, mrtree);
			} else {
				double[] min = new double[dim];
				double[] max = new double[dim];
				for (int i = 0; i < dim; i++) {
					if (vec[i] == 0) {
						min[i] = minD[i];
						max[i] = middleD[i];
					} else {
						min[i] = middleD[i];
						max[i] = maxD[i];
					}
				}
				MRCell mrcell = new MRCell(Util.mcid++, min, max, p.startTime);

				if (wi != 0) {
					mrcell.weight += wi / Util.partNum.doubleValue();

					mrcell.lt = lt;
				}

				addChild(mrcell, hg, mrtree);// add child
				return mrcell.add(p, mrtree);
			}
		}
	}

	private void add(Point p) {
		weight++;
		la = p.startTime;
		p.mrcellID[h] = id;
	}

	public void addChild(MRCell mrcell, String hg, MRTree mrtree) {
		mrcell.upPoint = this;
		downPoint.put(hg, mrcell);
		childMap.put(mrcell, hg);
		mrcell.h = this.h + 1;
		mrtree.nodeNum++;
	}

	public void removeChild(MRCell mrcell) {
		downPoint.remove(childMap.get(mrcell));
		childMap.remove(mrcell);
	}

	public void prTreeUp(MRCell mrcell, MRTree mrtree) {
		if (isRoot()) {
			return;
		}
		
		if (!mrcell.isDense()) {
			return;
		}
		
		BigInteger size = new BigInteger(downPoint.size() + "", 10);
		if (size.compareTo(Util.partNum) != 0) {
			return;
		}
		Iterator<Entry<String, MRCell>> itr = downPoint.entrySet().iterator();
		while (itr.hasNext()) {
			MRCell cur = itr.next().getValue();
			cur.decay(mrcell.lt);
			if (!cur.isDense()) {
				return;
			}
		}
//		System.out.println("really merge");
		downPoint.clear();
		mrtree.nodeNum -= Util.partNum.longValue();
		wi = weight;
		upPoint.prTreeUp(this, mrtree);
	}

	public void updateWeight(long time) {
		decay(time);
		if (downPoint.isEmpty()) {
			return;
		} else {
			Iterator<Entry<String, MRCell>> itr = downPoint.entrySet()
					.iterator();
			while (itr.hasNext()) {
				MRCell cur = itr.next().getValue();
				cur.updateWeight(time);
			}
		}
	}

	public void prTreeDown(long time, MRTree mrtree) {
		if (!downPoint.isEmpty()) {
			// Iterator<Entry<Long, MRCell>> itr =
			// downPoint.entrySet().iterator();
			// while (itr.hasNext()) {
			// MRCell cur = itr.next().getValue();
			// cur.prTreeDown(time, mrtree);
			// }
			ArrayList<MRCell> list = new ArrayList<MRCell>(downPoint.values());
			for (int i = 0; i < list.size(); i++) {
				MRCell cur = list.get(i);
				cur.prTreeDown(time, mrtree);
			}
		}
		if (isLeaf() && !isRoot()) {
			if (getDensity() < Util.p(time, la, h)) {
				upPoint.removeChild(this);
				upPoint = null;
				mrtree.nodeNum--;
			}
		}
	}

	public void mergeDown(Type type) {
		if (isLeaf()) {
			return;
		}
		Iterator<Entry<String, MRCell>> itr = downPoint.entrySet().iterator();
		while (itr.hasNext()) {
			MRCell cur = itr.next().getValue();
			cur.mergeDown(type);
		}
		BigInteger size = new BigInteger(downPoint.size() + "", 10);
		if (size.compareTo(Util.partNum) != 0 || getType() != type) {
			return;
		}
		System.out.println("merge");
		Iterator<Entry<String, MRCell>> itr2 = downPoint.entrySet().iterator();
		while (itr2.hasNext()) {
			MRCell cur = itr2.next().getValue();
			if ((cur.getType() != type) || !cur.isLeaf()) {
				return;
			}
		}
		downPoint.clear();
		wi = weight;
	}

	public void generateSetForClustering(int hight, Set<MRCell> cellSet) {

		if (wi > Util.DL) {
			cellSet.add(this);
			return;
		}
		if (h == hight || isLeaf()) {
			cellSet.add(this);
			return;
		}
		Iterator<Entry<String, MRCell>> itr = downPoint.entrySet().iterator();
		while (itr.hasNext()) {
			MRCell cur = itr.next().getValue();
			cur.generateSetForClustering(hight, cellSet);
		}
	}

	public double getDisTo(MRCell mrcell) {
		double dis = 0;
		double sum = 0;
		int dim = maxD.length;
		double cur = 0;
		for (int i = 0; i < dim; i++) {
			cur = Math.min(Math.abs(maxD[i] - mrcell.minD[i]),
					Math.abs(minD[i] - mrcell.maxD[i]));
			sum += cur * cur;
		}
		dis = Math.sqrt(sum);
		return dis;
	}

	public Type getType() {
		if (isDense()) {
			return Type.DENSE;
		} else {
			return Type.SPARSE;
		}
	}

	public boolean isRoot() {
		return upPoint == null;
	}

	public boolean isLeaf() {
		return downPoint.isEmpty();
	}

	public boolean isDense() {
		return getDensity() > Util.DH;
	}

	public void decay(long curTime) {
		weight = weight * Math.pow(Util.lambda, -(Util.a * (curTime - lt)));
		lt = curTime;
	}

	public double getDensity() {
//		density = weight / Math.pow(2, Util.dim * (Util.H - h));
		// System.out.println("weight=" + weight);
		// System.out.println("Util.a" + Util.a);
		// System.out.println("Util.dim" + Util.dim);
		// System.out.println("Util.H" + Util.H);
		return weight;
	}

}
