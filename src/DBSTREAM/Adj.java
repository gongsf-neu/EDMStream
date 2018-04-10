package DBSTREAM;

public class Adj {

	public double weight;
	public long lt;

	public Adj(Point p) {
		this.weight = 1;
		this.lt = p.startTime;
	}

	public void add(Point p) {
		if (p.startTime == lt) {
			weight++;
		} else {
			weight = weight * Util.decayFun(p.startTime, lt) + 1;
			lt = p.startTime;
		}
	}

	public double getCurWeight(long time) {
		return weight * Util.decayFun(time, lt);
	}

}
