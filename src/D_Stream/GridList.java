package D_Stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class GridList {

	public Map<Key, Grid> map;
	// public HashSet<Cluster> clusterSet;
	public int label = 0;

	public GridList() {
		map = new HashMap<Key, Grid>();
	}

	public void insert(Grid g) {
		Iterator<Entry<Key, Grid>> itr = map.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<Key, Grid> entry = itr.next();
			Grid eg = entry.getValue();
			if (g.isNeighbor(eg) != -1) {
				g.addNeighbor(eg);
			}
		}
		map.put(g.key, g);
	}

	public void delete(Grid g) {
//		Cluster c = g.cluster;
//		if (c != null) {
//			System.out.println(c.clusterId);
//		}
		Iterator<Grid> itr = g.neighbors.iterator();
		while (itr.hasNext()) {
			Grid eg = itr.next();
			eg.deleNeighbor(g);
		}
	}

	public boolean isContains(Key key) {
		return map.containsKey(key);
	}

	public void updateGrid(Key key, Point p, double lambda) {
		Grid g = map.get(key);
		g.isreceive = true;
		g.updateDensity(p.startTime, lambda);
	}

	public void detect(double lambda, double N, double cl, long now, double dm,
			double dl, double beta, Map<Key, Long> keyMap)
			throws MyException {
		Iterator<Entry<Key, Grid>> iter = map.entrySet().iterator();
		double pai = 0;
		while (iter.hasNext()) {
			Entry<Key, Grid> entry = iter.next();
			Grid g = entry.getValue();
			
			g.updateDensityForAll(now, lambda);
			if (g.density >= dm) {
				if (g.status != Status.Dense) {
					g.isChange = true;
				} else {
					g.isChange = false;
				}
				g.status = Status.Dense;
			} else if (g.density <= dl) {
				if (g.status != Status.Sparse) {
					g.isChange = true;
				} else {
					g.isChange = false;
				}
				g.status = Status.Sparse;
			} else {
				if (g.status != Status.Transitional) {
					g.isChange = true;
				} else {
					g.isChange = false;
				}
				g.status = Status.Transitional;
			}
			if (g.status == Status.Sparse) {
				// System.out.println("pai=" + pai);
				// System.out.println("density=" + g.density);
				pai = cl * (1 - Math.pow(lambda, now - g.tg + 1))
						/ (N * (1 - lambda));
				if (g.density < pai && now >= (1 + beta) * g.key.tm) {
					if (g.sporadic && !g.isreceive) {
						keyMap.put(g.key, now);
						iter.remove();
						delete(g);
						g.neighbors.clear();
					} else {
						g.sporadic = true;
					}
				} else {
					g.sporadic = false;
				}
			}
			g.isreceive = false;
		}
	}

	public void updateAllDensity(long time, double lambda, double dm, double dl) {
		Iterator<Entry<Key, Grid>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Key, Grid> entry = iter.next();
			Grid g = entry.getValue();
			g.updateDensityForAll(time, lambda);
			if (g.density >= dm) {
				if (g.status != Status.Dense) {
					g.isChange = true;
				} else {
					g.isChange = false;
				}
				g.status = Status.Dense;
			} else if (g.density <= dl) {
				if (g.status != Status.Sparse) {
					g.isChange = true;
				} else {
					g.isChange = false;
				}
				g.status = Status.Sparse;
			} else {
				if (g.status != Status.Transitional) {
					g.isChange = true;
				} else {
					g.isChange = false;
				}
				g.status = Status.Transitional;
			}
		}
	}

	private void assignLabel(ClusterSet clusterSet) throws MyException {

		Iterator<Entry<Key, Grid>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Key, Grid> entry = iter.next();
			Grid g = entry.getValue();
			if (g.status == Status.Dense) {
				Cluster c = new Cluster(label++);
				c.initInsert(g);
				clusterSet.add(c);
			}
		}
	}

	public void initialClustering(long time, double lambda, double dm,
			double dl, ClusterSet clusterSet) throws MyException {
		updateAllDensity(time, lambda, dm, dl);
		assignLabel(clusterSet);
		boolean change = true;
		while (change) {
			change = false;
			Iterator<Cluster> citr = clusterSet.iterator();
			while (citr.hasNext()) {
				Cluster ctmp = citr.next();
				if (ctmp.isEmpty()) {
					citr.remove();
					continue;
				}
				Iterator<Grid> itr = ctmp.gridSet.iterator();
				boolean merge = true;
				while (merge && itr.hasNext()) {
					Grid ctmpGrid = itr.next();
					Iterator<Grid> itr2 = ctmpGrid.neighbors.iterator();
					while (itr2.hasNext()) {
						Grid h = itr2.next();
						if (h.cluster == ctmp) {
							continue;
						} else if (h.cluster != null) {
							Cluster hcluster = h.cluster;
							// if (ctmp.getSize() > hcluster.getSize()) {
							ctmp.merge(hcluster);
							merge = false;
							// } else {
							// hcluster.merge(ctmp);
							// }
							change = true;
						} else if (h.status == Status.Transitional) {
							ctmp.initInsert(h);
							change = true;
						}
					}
				}
			}
		}
	}

	public void adjust(long time, double lambda, double dm, double dl,
			ClusterSet clusterSet) throws MyException {
		
		// updateAllDensity(time, lambda, dm, dl);
		Iterator<Entry<Key, Grid>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Key, Grid> entry = iter.next();
			Grid g = entry.getValue();
			if (g.isChange) {
//				System.out.println("finished");
//				System.out.println(g);
				Cluster cg = g.cluster;
				// System.out.println(g.status);
				if (g.status == Status.Sparse) {
					if (cg != null) {
						cg.delete(g);// delete g from cluster label g as
										// No-Class;
						if (!cg.isEmpty()) {
							Cluster nc = cg.split(label);// split c into two
							// clusters;
							if (nc != null) {
								label++;
								nc.setLabel();
								clusterSet.add(nc);
							}
						}
					}

				} else if (g.status == Status.Dense) {
					// among all neighbor grids of g find out the grid h whose
					// cluster ch has the largest size;
					
					List<Grid> list = g.getNeighbors();
					int size = list.size();
					for (int i = 0; i < size; i++) {
						Grid h = list.get(i);
						
						Cluster ch = h.cluster;
//						System.out.println(h.status);
//						System.out.println(ch);
						if (h.status == Status.Dense) {
							if (cg == null) {
								ch.initInsert(g);
							} else {
								if(cg != ch){
									if (cg.getSize() > ch.getSize()) {
										cg.merge(ch);
										clusterSet.remove(ch);
									} else {
										ch.merge(cg);
										clusterSet.remove(cg);
									}
								}
							}
							break;
						} else if (h.status == Status.Transitional) {
							if (g.cluster == null) {
								if (ch.add(h, g)) {
									break;
								}
							} else {
								if (cg.getSize() >= ch.getSize()) {
									ch.delete(h);
									cg.initInsert(h);
									if (ch.isEmpty()) {
										clusterSet.remove(ch);
									}
									break;
								}
							}
						}
					}

					if (size == 0 || g.cluster == null) {
						Cluster c = new Cluster(label++);
						c.initInsert(g);
						clusterSet.add(c);
					}
				} else {
					g.insertToCluster();
				}
				if (cg != null && cg.isEmpty()) {
					clusterSet.remove(cg);
				}
			}
		}
		
	}

	public void test(double dm) {

		Iterator<Entry<Key, Grid>> itr = map.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<Key, Grid> entry = itr.next();
			Grid g = entry.getValue();
			if (g.density >= dm) {
				if (g.status != Status.Dense) {
					System.out.println("error in density is large but not dense");
					System.exit(0);
				}
				
				Cluster c = g.cluster;
				
				if(c == null){
					System.out.println("error in density is large but it's is null");
					System.exit(0);
				}else if(!c.gridSet.contains(g)){
					System.out.println("error in density is large but not in cluster");
					System.out.println(g);
					System.out.println(c);
					System.exit(0);
				}
			}

			if (g.status == Status.Dense) {
				if (g.cluster == null) {
					System.out.println("error2");
					System.exit(0);
				}
			}
		}
	}

	public void checkNullNeighbor() {
		Iterator<Entry<Key, Grid>> itr = map.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<Key, Grid> entry = itr.next();
			Grid g = entry.getValue();
			Iterator<Grid> itr2 = g.neighbors.iterator();
			while (itr2.hasNext()) {

			}
		}
	}

	public void check() {
		Iterator<Entry<Key, Grid>> itr = map.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<Key, Grid> entry = itr.next();
			Grid g = entry.getValue();
			Iterator<Grid> itr2 = g.neighbors.iterator();
			while (itr2.hasNext()) {
				Grid h = itr2.next();
				if(!h.neighbors.contains(g)){
					System.out.println("the neighbor's neighbor is not me");
					System.exit(0);
				}
			}
		}
	}

}
