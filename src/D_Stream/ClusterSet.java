package D_Stream;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ClusterSet {
	
	public Set<Cluster> sets;
	
	public ClusterSet(){
		sets = new HashSet<Cluster>();
	}
	
	public Iterator<Cluster> iterator(){
		return sets.iterator();
	}

	public void add(Cluster c) throws MyException {
		if(c.isEmpty()){
			throw new MyException("cluster is empty");
		}
		sets.add(c);
	}
	
	public int size(){
		return sets.size();
	}

	public void remove(Cluster c) {
		sets.remove(c);
	}
	
	public void test(){
//		System.out.println("before test the size of cluster" + sets.size());
		boolean flag = false;
		Iterator<Cluster> itr = sets.iterator();
		while(itr.hasNext()){
			Cluster c = itr.next();
			if(c.isEmpty()){
				System.out.println("there are empty cluster");
				itr.remove();
				flag = true;
			}
			Iterator<Grid> itr2 = c.gridSet.iterator();
			while(itr2.hasNext()){
				Grid g = itr2.next();
				if(g.cluster == null){
					System.out.println("there are grid's cluster is null:" + g);
					System.exit(0);
				}
				Iterator<Cluster> itr3 = sets.iterator();
				while(itr3.hasNext()){
					Cluster oc = itr3.next();
					if(oc != c){
						if(oc.gridSet.contains(g)){
							System.out.println("there are two cluster contain the same grid:" + g);
							System.out.println(oc);
							System.out.println(c);
							System.out.println(g.cluster);
							flag = true;
							System.exit(0);
						}
					}
				}
//				if(g.status == Status.Sparse){
//					if(c.gridSet.contains(g))
//						System.out.println(g);
//					System.out.println("there are sparse grid:" + g + " : " + g.cluster);
//					flag = true;
//				}
			}
		}
		if(flag){
//			System.out.println("after test the size of cluster" + sets.size());
			System.exit(0);
		}
	}
	
	public void check(double dl){
		if(sets.isEmpty()){
			System.out.println("zen me ke nneg kong");
			System.exit(0);
		}
		Iterator<Cluster> itr = sets.iterator();
		while(itr.hasNext()){
			Cluster c = itr.next();
			Iterator<Grid> itr2 = c.gridSet.iterator();
			while(itr2.hasNext()){
				Grid g = itr2.next();
				if(g.cluster == null){
					System.out.println("g.cluster == null in cluster");
					System.exit(0);
				}
				if(g.density < dl){
					System.out.println("there is sparse grid in cluster");
					System.exit(0);
				}
			}
		}
		
	}
}
