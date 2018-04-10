package CMM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cluster {

	public List<Point> points;
	public String groundTruth;
	public int[] rho;
	public double knhDis;
	public ClusterType type;

	public Cluster() {
		points = new ArrayList<Point>();
	}

	public void add(Point p) {
		points.add(p);
	}

	public void getDistribution(Map<String, Integer> map) {
		rho = new int[map.size()];
		int size = points.size();
		for (int i = 0; i < size; i++) {
			Point p = points.get(i);
			rho[map.get(p.truth)]++;
		}
	}

	public void getConn(int k) {
		int size = points.size();
//		System.out.println(size);
		double[] knhPDis = new double[size];
		double sum = 0;
		for (int i = 0; i < size; i++) {
//			System.out.println(i);
			Point p = points.get(i);
			knhPDis[i] = p.knnDis(k, this);
			sum += knhPDis[i];
		}
		knhDis = sum/size;
		for(int i = 0; i < size; i++){
//			System.out.println(i);
			if(type == ClusterType.GTCluster){
				if(knhPDis[i] < knhDis){
					points.get(i).conCL = 1;
				}else{
					if(points.size() == 1){
						points.get(i).conCL = 1;
					}else{
						points.get(i).conCL = knhDis/knhPDis[i];
					}
				}
			}else{
				if(knhPDis[i] < knhDis){
					points.get(i).con = 1;
				}else{
					points.get(i).con = knhDis/knhPDis[i];
				}
			}
		}
	}
}
