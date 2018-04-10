package DBSTREAM;

public class MicroCluster {
	int id;
	double weight;
	Point center;
	double[] vec;
	double dis;
	double r;
	boolean visited;

	public MicroCluster() {

	}

	public MicroCluster(int id, Point center, double r) {
		this.id = id;
		this.center = center;
		weight = 1;
		vec = new double[center.vec.length];
		this.r = r;
	}

	public double getDisTo(Point p) {
		dis = center.getDisTo(p);
		return dis;
	}

	public double getVecDisTo(MicroCluster mc) {
		double dis = 0;
		double temp = 0;
		int dim = vec.length;
		double[] cvec = mc.vec;
		for (int i = 0; i < dim; i++) {
			temp = cvec[i] - vec[i];
			dis += temp * temp;
		}
		return Math.sqrt(dis);
	}

	public void add(Point p) {
		weight = weight + 1;
		int dim = p.dim;
		double[] cvec = center.vec;
		double[] pvec = p.vec;
		double val = Math.exp(-(Math.pow(3 * dis / r, 2) / 2));
		for (int i = 0; i < dim; i++) {
			vec[i] = cvec[i] + val * (pvec[i] - cvec[i]);
		}
	}
	
	public void decay(double defactor){
		weight *= defactor;
	}

	public double getCurWeight() {
		return weight;
	}

	public void move() {
		center.vec = vec;
	}
}
