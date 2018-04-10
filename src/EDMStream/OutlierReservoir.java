package EDMStream;

import java.io.BufferedWriter;
import java.util.HashSet;
import java.util.Iterator;

public class OutlierReservoir {

	public double r;
	public double timeGap;
	public long lastDelTime;

	public double a;
	public double lamd;

	public HashSet<CluCell> outliers;

	public OutlierReservoir(double r, double a, double lamd) {
		outliers = new HashSet<CluCell>();
		this.r = r;
		this.a = a;
		this.lamd = lamd;
	}

	public void setTimeGap(double timeGap) {
		this.timeGap = timeGap;
	}

	public void insert(CluCell c) {
		c.delta = Double.POSITIVE_INFINITY;
		if(c.dep != null){
			c.dep.sucs.remove(c);
		}
		c.dep = null;
		outliers.add(c);
	}

	public CluCell insert(Point p) {
		Iterator<CluCell> ite = outliers.iterator();

		double dis = 0;
		double minDis = Double.POSITIVE_INFINITY;
		CluCell nn = null;
		CluCell temp = null;
		while (ite.hasNext()) {
			temp = ite.next();
			if (p.startTime - temp.lastTime > timeGap) {
				ite.remove();
				continue;
			}
			dis = p.getDisTo(temp.center);
			if (dis < minDis) {
				minDis = dis;
				nn = temp;
			}
		}
		if (nn == null || minDis > r) {
			CluCell c = new CluCell(p);
			outliers.add(c);
			return c;
		} else {
			double coef = Math.pow(a, lamd * (p.startTime - nn.lastTime));
//			System.out.println(p.startTime + " " + nn.lastTime);
//			System.out.println(minDis);
//			System.out.println(coef);
			nn.add(p, coef);
			return nn;
		}
	}
	
	public void print(){
		Iterator<CluCell> ite = outliers.iterator();
		CluCell temp = null;
		while (ite.hasNext()) {
			temp = ite.next();
			System.out.println(temp.cid + " " + temp.rho);
		}
	}

	public void print(BufferedWriter bw) {
		try {
			Iterator<CluCell> ite = outliers.iterator();
			CluCell temp = null;
			while (ite.hasNext()) {
				temp = ite.next();
				bw.write(temp.cid);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void remove(CluCell nn) {
		outliers.remove(nn);
	}
}
