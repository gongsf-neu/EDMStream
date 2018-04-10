package DBSTREAM;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Point {

	public int id;
	public long startTime;
	public int dim;
	public double[] vec;

	public double maxDis;
	public double minDis;
	
	public Map<Integer, Double> pidToCCDis;


	public Point(int id, long startTime, double[] vec) {
		this.id = id;
		this.startTime = startTime;
		this.dim = vec.length;
		this.vec = vec;
		pidToCCDis = new HashMap<Integer, Double>();
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
}
