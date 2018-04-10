package MR_Stream;

import java.io.BufferedWriter;
import java.io.IOException;

public class Point {

	public int id;
	public long startTime;
	public int dim;
	public double[] vec;

	public double maxDis;
	public double minDis;

	public int[] mrcellID;

	// public Point(long st, int dim, float[] vec){
	// this.startTime = st;
	// this.dim = dim;
	// this.vec = vec;
	// }

	public Point(int id, long startTime, double[] vec) {
		this.id = id;
		this.startTime = startTime;
		this.dim = vec.length;
		this.vec = vec;
		mrcellID = new int[Util.H + 1];
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
}
