package D_Stream;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Cluster {

	public int clusterId;
	public Set<Grid> gridSet;
	public Cluster next;

	public Cluster(int clusterId) {
		this.clusterId = clusterId;
		gridSet = new HashSet<Grid>();
	}

	public void initInsert(Grid g) {
		if(g.cluster == null ){
			g.cluster = this;
			gridSet.add(g);
		}else{
			if(g.cluster != this){
				g.cluster.delete(g);
				g.cluster = this;
				gridSet.add(g);
			}
		}
	}

	public boolean isEmpty() {
		return gridSet.isEmpty();
	}

	public void merge(Cluster hcluster) {
		gridSet.addAll(hcluster.gridSet);
		Iterator<Grid> itr = hcluster.gridSet.iterator();
		while(itr.hasNext()){
			itr.next().cluster = this;
		}
		hcluster.gridSet.clear();
	}

	public void delete(Grid g) {
		g.cluster = null;
		gridSet.remove(g);
	}

	public int getSize() {
		return gridSet.size();
	}

	public Cluster split(int label) {
		
		Queue<Grid> queue = new LinkedList<Grid>();
		HashSet<Grid> set2 = new HashSet<Grid>();

		Iterator<Grid> itr = gridSet.iterator();
		if (itr.hasNext()) {
			
			queue.add(itr.next());
			itr.remove();
		}
		while (!queue.isEmpty()) {
			Grid g = queue.remove();
			set2.add(g);
			itr = gridSet.iterator();
			while (itr.hasNext()) {
				Grid h = itr.next();
				if (g.isNeighbor(h) != -1) {
					queue.add(h);
					itr.remove();
				}
			}
		}
		if (!gridSet.isEmpty()) {
			Cluster nc = new Cluster(label);
			nc.gridSet = set2;
			return nc;
		} else {
			gridSet = set2;
			return null;
		}
	}

	public void setLabel() {
		Iterator<Grid> itr = gridSet.iterator();
		while(itr.hasNext()){
			itr.next().cluster = this;
		}
	}

	public boolean add(Grid h, Grid g) {
		initInsert(g);
		Iterator<Grid> itr = gridSet.iterator();
		while(itr.hasNext()){
			int index = h.isNeighbor(itr.next());
			if(index != -1){
				h.neighborVec[index]++;
				if(h.neighborVec[index] == 2){
					h.neighborNum++;
				}
			}
		}
		if(h.neighborNum == h.key.attr.length){
			delete(g);
			return false;
		}else{
			if(g.cluster != null){
				g.cluster.delete(g);
			}
			return true;
		}
	}
}
