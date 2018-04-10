package MR_Stream;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	
	public int id;
	public List<MRCell> list;
	public int size;
	public double weight;
	
	public Cluster(int id){
		this.id = id;
		list = new ArrayList<MRCell>();
	}
	
	public void add(MRCell mrcell){
		list.add(mrcell);
		size++;
		weight += mrcell.weight;
	}
}
