package EDMStream;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class EDMStreamV5 {

	public DPTreeV5 dpTree;
	public OutlierReservoir outres;
	public Cache cache;
	public HashSet<Cluster> clusters;

	public boolean isInit = false;

	public double a;
	public double lamd;
	public double deltaT;
	public double alpha;
	public double beta;

	public int cacheNum;

	public int actCluMaxNum;

	public int dim;
	public double r;

	public double minRho;
	public double minDelta;

	public String bufferPath;
	public String decisionPath;

	public EDMStreamV5() {

	}

	public void set(double a, double lamd, int cacheNum, int dim, double r,
			double beta, double delta) {
		this.a = a;
		this.lamd = lamd;
		this.cacheNum = cacheNum;
		this.r = r;
		this.dim = dim;
		cache = new Cache(cacheNum, a, lamd, r);
		outres = new OutlierReservoir(r, a, lamd);
		actCluMaxNum = 100000;
		dpTree = new DPTreeV5(actCluMaxNum, r);
		this.beta = beta;
		this.minDelta = delta;
	}

	public void setBufferPath(String path) {
		this.bufferPath = path;
	}

	public void setDecisionPath(String path) {
		this.decisionPath = path;
	}

	public void setMinDelta(double minDelta) {
		this.minDelta = minDelta;
		dpTree.minDelta = minDelta;
	}

	public CluCell retrive(Point p, int opt) {

		Point curP = p;
		if (!isInit) {
			CluCell cc = cache.add(curP);
			if (cache.isFull()) {
				// draw decision graph
				clusters = new HashSet<Cluster>();
				InitDP(curP.startTime);
				alpha = computeAlpha();
//				alpha = 0.65;
				System.out.println("alpha=" + alpha);
				isInit = true;
			}
			return cc;
		} else {
			CluCell nn = streamProcess(curP, opt);
//			System.out.println(minDelta);
//			dpTree.trackCluster(clusters);
//			System.out.println(clusters.size());
			dpTree.adjustCluster(clusters, false);
			delCluster();
			return nn;
		}
	}

	public void InitDP(long time) {

		cache.compDeltaRho(time);
		// cache.drawDecision(bufferPath, decisionPath);
		// scan = new Scanner(System.in);
		System.out.println("beta=" + beta);
		minRho = beta / (1 - Math.pow(a, lamd));
		System.out.println("minRho=" + minRho);
		cache.outputBuffer(bufferPath, minRho);//for drawing the decision graph
		// minDelta = 2250;
		// System.out.println("minDelta=" + minDelta);
		// double upTime = (Math.log(1 - minRho * (1 - Math.pow(a, lamd))) /
		// Math
		// .log(a)) / lamd;
		// System.out.println("uptime=" + upTime);
		// deltaT = (Math.log(1 - (1 - Math.pow(a, lamd * (upTime - 1)))
		// / ((1 - Math.pow(a, lamd)) * minRho)) / Math.log(a))
		// / lamd;
		deltaT = (Math.log(1 - Math.pow(a, lamd)) / Math.log(a) - Math
				.log(beta) / Math.log(a))
				/ lamd;
//		double deltaT = 100;
		System.out.println("deltaT=" + deltaT);
		outres.setTimeGap(deltaT);

		cache.getDPTree(minRho, minDelta, dpTree, outres, clusters);
		System.out.println("dpTree size = " + dpTree.size);
		dpTree.lastTime = time;
	}

	public void bufferPoint(Point p) {
		cache.add(p);
	}

	public CluCell streamProcess(Point p, int opt) {
		// System.out.println(p.id);

		double coef = Math.pow(a, lamd * (p.startTime - dpTree.lastTime));
		dpTree.lastTime = p.startTime;
		CluCell nn = dpTree.findNN(p, coef, clusters, minRho, opt);
		if (nn == null || nn.dis > dpTree.CluR) {

			nn = outres.insert(p);
			if (nn.rho > minRho) {
//				System.out.println("change");
				outres.remove(nn);
//				System.out.println("merged by o_micro:" + nn.dis);
				dpTree.insert(nn, minRho, p, opt);
			}
		}
//		else{
//			System.out.println("merged by p_micro:" + nn.dis);
//		}

		dpTree.deleteInact(outres, coef, minRho, p.startTime);
//		dpTree.adjustCluster(clusters);
//		delCluster();
		return nn;
	}

	private boolean check() {
		return dpTree.check(outres);
	}

	private double computeAlpha() {
		return dpTree.computeAlpha(minDelta);
	}

	public double adjustMinDelta() {
		return dpTree.adjustMinDelta(alpha);
	}

	public void outResult(String outpath) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outpath));
			// minDelta = adjustMinDelta();
			// bw.write(minDelta + "\n");
			dpTree.print(bw, minDelta, clusters);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void delCluster() {
		Iterator<Cluster> itr = clusters.iterator();
		while (itr.hasNext()) {
			Cluster c = itr.next();
			if (c.cells.isEmpty()) {
				itr.remove();
			}
		}
	}

	public void checkCluster() {
		dpTree.check(clusters);
	}

}
