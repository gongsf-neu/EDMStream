package CMM;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Point {
	
	public int id;
	public long startTime;
	public int dim;
	public double[] vec;
	public double weight;
	public String truth;
	public double conCL;
	public double con;


	public Point(int id, long startTime, long time, double[] vec, double a, double lambda, String truth) {
		this.id = id;
		this.startTime = startTime;
		this.dim = vec.length;
		this.vec = vec;
		this.weight = Math.pow(a, lambda*(time - startTime));
		this.truth = truth;
	}
	
	public double getDisTo(Point p) {
		double dis = 0;
		double temp = 0;
		double[] pvec = p.vec;
		for (int i = 0; i < dim; i++) {
			temp = pvec[i] - vec[i];
			dis += temp * temp;
		}
		return Math.sqrt(dis);
	}

	public void print(BufferedWriter bw) {

		try {
			for (double var : vec) {
				bw.write(var + " ");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public double knnDis(int k, Cluster c){
		List<Point> list = c.points;
		int size = list.size();
		double[] diss = new double[size];
		for(int i = 0; i < size; i++){
			Point p = list.get(i);
			if(p != this){
				diss[i] = getDisTo(list.get(i));
			}
		}
		Arrays.sort(diss);
		double sum = 0;
		int num = 0;
		for(int i = 0; i < k && i < size; i++){
			sum += diss[i];
			num++;
		}
		return sum/num;
	}

}
