package EDMStream;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CluCell {

	public int cid;
	public int Cid;
	public int num;
	public double rho;
	public double delta;
	public CluCell dep;
	public Point center;
	public long lastTime;
	public boolean active;
	public Set<CluCell> sucs;
	public Cluster cluster;
	// public double sumDelta;
	// public int sucNum;
	public long inactiveTime;

	/**
	 * we will use dis to quickly update the delta of CluCell
	 */
	public double dis;
	public static int id = 0;

	public CluCell() {

	}

	public CluCell(Point p) {
		this.cid = id++;
		this.rho = 1;
		this.delta = Float.MAX_VALUE;
		this.dep = null;
		this.center = p;
		this.lastTime = p.startTime;
		this.active = false;
		this.sucs = new HashSet<CluCell>();
	}

	public void insert(Point p) {
		rho++;
		lastTime = p.startTime;
	}

	public void add(Point p, double coef) {
		rho = coef * rho + 1;
		lastTime = p.startTime;
	}

	public void addSuccessor(CluCell cc) {
		sucs.add(cc);
	}

	public void removeSucor(CluCell cc) {
		sucs.remove(cc);
	}

	public double getDisTo(CluCell cc) {
		return center.getDisTo(cc.center);
	}

	public boolean hasSuccessor() {
		return !sucs.isEmpty();
	}

	public void print(BufferedWriter bw) {
		try {
			bw.write(cid + " " + Cid);
//			center.print(bw);
//			if (dep != null) {
//				bw.write(dep.cid + "\n");
//			} else {
//				bw.write("null\n");
//			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
