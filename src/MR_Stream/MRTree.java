package MR_Stream;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

public class MRTree {

	public MRCell root;
	public long nodeNum;
	public Set<MRCell> cellSet;

	public MRTree(double[] min, double[] max) {
		root = new MRCell(Util.mcid++, min, max, 0);
		root.weight = 0;
		root.h = 0;
		nodeNum = 1;
	}

	public MRCell add(Point p) {
		return root.add(p, this);
	}

	public void prTreeUp(MRCell g) {
		if (!g.isRoot()) {
			g.upPoint.prTreeUp(g, this);
		}
	}

	public void mergeDown(Type type) {
		root.mergeDown(type);
	}

	public long NodeNum() {
		return nodeNum;
	}

	public void updateTreeWeight(long time) {
		root.updateWeight(time);
	}

	public void pruneTreeDown(long time) {
		root.prTreeDown(time, this);
	}

	public void generateSetForClustering(int h) {
		cellSet = new HashSet<MRCell>();
		root.generateSetForClustering(h, cellSet);
		// System.out.println("cellSetSize=" + cellSet.size());
	}

	public void cluster(int h, List<Cluster> result) {
		generateSetForClustering(h);
		// System.out.println(cellSet.size());
		int id = 0;
		Queue<MRCell> queue = new LinkedList<MRCell>();
		while (!cellSet.isEmpty()) {
			Iterator<MRCell> itr = cellSet.iterator();
			MRCell cur = itr.next();
			itr.remove();
			Cluster c = new Cluster(id);
			queue.add(cur);
			while (!queue.isEmpty()) {
				MRCell curCell = queue.poll();
				c.add(curCell);
				Iterator<MRCell> itr2 = cellSet.iterator();
				while (itr2.hasNext()) {
					MRCell temp = itr2.next();
					// if(curCell.getDisTo(temp) < Util.eplislon*Math.pow(2,
					// -Util.H)){
					if (curCell.getDisTo(temp) < Util.eplislon) {
						itr2.remove();
						queue.add(temp);
					}
				}
			}
			if (c.size < Util.mu && c.weight < Util.beta) {
				c.id = -1;
			} else {
				result.add(c);
				id++;
			}
		}
	}

	public void test() {
		int num = 0;
		Queue<MRCell> queue = new LinkedList<MRCell>();
		queue.add(root);
		while (!queue.isEmpty()) {
			MRCell mrcell = queue.poll();
			num++;
			if (!mrcell.downPoint.isEmpty()) {
				Iterator<Entry<String, MRCell>> itr = mrcell.downPoint
						.entrySet().iterator();
				while (itr.hasNext()) {
					Entry<String, MRCell> entry = itr.next();
					queue.add(entry.getValue());
				}
			}
			if (mrcell.weight == Double.POSITIVE_INFINITY) {
				System.exit(0);
			}
		}
		if (num != nodeNum) {
			System.out.println("num=" + num + " nodeNum=" + nodeNum);
			System.exit(0);
		}
	}

}
