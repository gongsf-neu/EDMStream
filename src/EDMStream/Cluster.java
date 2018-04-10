package EDMStream;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Cluster {
	
	public int label;
	public Set<CluCell> cells;
	
	public Cluster(int label){
		this.label = label;
		cells = new HashSet<CluCell>();
	}
	
	public void add(CluCell c){
		c.cluster = this;
		cells.add(c);
	}
	
	public void remove(CluCell c){
		c.cluster = null;
		cells.remove(c);
	}
	
//	public void split(Cluster cluster, CluCell c, double minDelta){
////		if(cluster.cells.size()==107 && c.sucs.size()==3)
////			System.out.println(c.sucs.size());;
//		
//		remove(c);
//		cluster.add(c);
//		Queue<CluCell> queue = new LinkedList<CluCell>();
//		Iterator<CluCell> itr = c.sucs.iterator();
//		while(itr.hasNext()){
//			CluCell itrc = itr.next();
//			if(itrc.delta <= minDelta){
//				queue.add(itrc);
//			}
//		}
//		
//		while(!queue.isEmpty()){
//			CluCell cc = queue.remove();
//			remove(cc);
//			cluster.add(cc);
//			Iterator<CluCell> itr2 = cc.sucs.iterator();
//			while(itr2.hasNext()){
//				CluCell itrc = itr2.next();
//				if(itrc.cluster == this){
//					queue.add(itrc);
//				}
//			}
//		}
////		System.exit(0);
//	}
	
//	public void merge(CluCell cc){
//		Set<CluCell> cc = cluster.cells;
//		Iterator<CluCell> itr = cc.iterator();
//		while(itr.hasNext()){
//			CluCell c = itr.next();
//			add(c);
//		}
//		cluster.cells.clear();
//	}
}
