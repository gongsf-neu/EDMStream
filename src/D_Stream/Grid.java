package D_Stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Grid {

	public long tg;// last receive time
	public long tg2;
	// public long tm;// last time g is removed from list
	public double density;
	public Cluster cluster;// cluster label
	public Status status;// three status of grid : dense, transitional, sparse
	boolean sporadic;// judge for delete
	public Key key;
	public int[] neighborVec;
	public Set<Grid> neighbors;
	public int neighborNum;
	public boolean isChange;
	public boolean isreceive;
	public int id;

	// public Grid(int[] vec) {
	// this.key = new Key(vec);
	// }
	//
	public Grid(Key key, long time, int id) {
		this.key = key;
		this.tg2 = time;
		this.density = 1;
		this.neighborVec = new int[key.attr.length];
		neighbors = new HashSet<Grid>();
		neighborNum = 0;
		cluster = null;
		this.status = Status.Sparse;
		this.id = id;
	}

	public void updateDensityForAll(long now, double lambda) {
		density = density * Math.pow(lambda, now - tg2);
		tg2 = now;
	}

	public void updateDensity(long startTime, double lambda) {
		density = density * Math.pow(lambda, startTime - tg2) + 1;
		tg2 = startTime;
		tg = startTime;
		isreceive = true;
	}

	public void addNeighbor(Grid g) {
		if (g == null) {
			System.out.println("add a null grid as neighbor");
			System.exit(0);
		}
		neighbors.add(g);
		g.neighbors.add(this);
	}

	public void deleNeighbor(Grid g) {
		neighbors.remove(g);
		// g.neighbors.remove(this);
	}

	public int isNeighbor(Grid g) {
		int neighborIndex = -1;
		int[] attr = key.attr;
		int[] gattr = g.key.attr;
		int diff = 0;
		int dim = gattr.length;
		boolean isNeighbor = true;
		for (int i = 0; i < dim && isNeighbor ; i++) {
			diff = attr[i] - gattr[i];
			if (diff != 0) {
				if (diff == 1 || diff == -1) {
					if (neighborIndex == -1 && isNeighbor) {
						neighborIndex = i;
					} else {
						isNeighbor = false;
						neighborIndex = -1;
					}
				} else {
					isNeighbor = false;
					neighborIndex = -1;
				}
			}
		}
		return neighborIndex;
	}

	public List<Grid> getNeighbors() {
		List<Grid> list = new ArrayList<Grid>();
		Iterator<Grid> itr = neighbors.iterator();
		while (itr.hasNext()) {
			Grid g = itr.next();
			if (g.cluster != null) {
				list.add(g);
			}
		}

		Collections.sort(list, new GridComparator());
		return list;
	}

	public List<Cluster> getNeighobrClusters(Set<Cluster> clusterSet) {
		List<Cluster> list = new ArrayList<Cluster>();
		Iterator<Cluster> itr = clusterSet.iterator();
		while (itr.hasNext()) {
			Cluster tmpc = itr.next();
			Iterator<Grid> itr2 = tmpc.gridSet.iterator();
			while (itr2.hasNext()) {
				Grid g = itr2.next();
				if (isNeighbor(g) != -1) {
					list.add(g.cluster);
					break;
				}
			}
		}
		Collections.sort(list, new ClusterComparator());
		return list;
	}

	public void insertToCluster() {
		this.neighborNum = 0;
		this.neighborVec = new int[key.attr.length];
		List<Grid> list = getNeighbors();
		int size = list.size();
		HashSet<Cluster> set = new HashSet<Cluster>();
		for (int i = 0; i < size; i++) {
			Cluster c = list.get(i).cluster;
			if (set.contains(c)) {
				continue;
			} else {
				set.add(c);
			}
			Iterator<Grid> itr2 = c.gridSet.iterator();
			while (itr2.hasNext()) {
				Grid g = itr2.next();
				int index = isNeighbor(g);
				if (index != -1) {
					this.neighborVec[index]++;
					if (this.neighborVec[index] == 1) {
						this.neighborNum++;
					}
					// if(this.neighborVec[index] > 2){
					// System.out.println("error" + this.neighborVec[index]);
					// System.exit(0);
					// }
				}
			}
			if (this.neighborNum != key.attr.length) {

				if (this.cluster == null) {
//					System.out.println("insert to antoher cluster");
					c.initInsert(this);
				} else {
					if (this.cluster != c
							&& c.getSize() > this.cluster.getSize()) {

						this.cluster.delete(this);
						c.initInsert(this);
					}
				}
			}
		}
	}

	class GridComparator implements Comparator<Grid> {
		@Override
		public int compare(Grid g1, Grid g2) {
			return -(g1.cluster.getSize() - g2.cluster.getSize());
		}
	}

	class ClusterComparator implements Comparator<Cluster> {
		@Override
		public int compare(Cluster c1, Cluster c2) {
			return -(c1.getSize() - c2.getSize());
		}
	}

	// public static void main(String[] args) {
	// int[] a = new int[5];
	// int[] b = new int[5];
	// for(int i = 0; i < 5; i++){
	// a[i] = i;
	// b[i] = i;
	// }
	// b[3] = 2;
	// // b[4] = 5;
	// Key keya = new Key(a);
	// Key keyb = new Key(b);
	// Grid ga = new Grid(keya, -1);
	// Grid gb = new Grid(keyb, -1);
	// int index = ga.isNeighbor(gb);
	// System.out.println(index);
	// }

}
