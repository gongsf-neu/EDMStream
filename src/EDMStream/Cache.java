package EDMStream;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;

public class Cache {

	int num;
	int size;
	double a;
	double lamd;
	double r;
	public CluCell[] buffer;
	public CluCell[] clus;
	int pnum;

	public Cache(int num, double a, double lamd, double r) {
		this.num = num;
		pnum = 0;
		buffer = new CluCell[10000];
		size = 0;
		this.a = a;
		this.lamd = lamd;
		this.r = r;
	}

	public CluCell add(Point p) {
		pnum++;
		double dis = Float.MAX_VALUE;
		double minDis = Float.MAX_VALUE;
		CluCell nn = null;
		for (int i = 0; i < size; i++) {
			dis = p.getDisTo(buffer[i].center);
			if (dis < minDis) {
				minDis = dis;
				nn = buffer[i];
			}
		}
		if (minDis <= r) {
			double coef = Math.pow(a, lamd * (p.startTime - nn.lastTime));
			nn.add(p, coef);
			return nn;
		} else {
			CluCell c = new CluCell(p);
			buffer[size] = c;
			size++;
			return c;
		}
	}

	public boolean isFull() {
		return pnum == num;
	}

	public void compDeltaRho(long time) {
		clus = new CluCell[size];
		for (int i = 0; i < size; i++) {
			buffer[i].rho = (float) (Math.pow(a, lamd * (time - buffer[i].lastTime)) * buffer[i].rho);
			clus[i] = buffer[i];
		}
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		Arrays.sort(clus, new MyComprator());

		double dis = 0;
		clus[0].delta = 0;
		for (int i = 1; i < size; i++) {
			CluCell cc = clus[i];
			double minDis = Double.POSITIVE_INFINITY;
			for (int j = i - 1; j >= 0; j--) {
				dis = cc.center.getDisTo(clus[j].center);
				if (minDis > dis) {
					minDis = dis;
					cc.dep = clus[j];
				}
			}
			cc.delta = minDis;
			if (clus[0].delta < minDis) {
				clus[0].delta = minDis;
			}
		}
	}

	public void getDPTree(double minRho, double minDelta, DPTreeV5 dpTree,
			OutlierReservoir outs, HashSet<Cluster> clusters) {
		dpTree.init(clus, size, minRho, minDelta, outs, clusters);
	}

	public void outputBuffer(String outpath, double minRho) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outpath));
			for (int i = 0; i < size && clus[i].rho > minRho; i++) {
				if (clus[i].dep != null) {
					bw.write(clus[i].cid + "  " + clus[i].rho + " "
							+ clus[i].delta + "\n");
				} else {
					bw.write(clus[i].cid + "  " + clus[i].rho + " "
							+ clus[i].delta + "\n");
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	class MyComprator implements Comparator<CluCell> {

		@Override
		public int compare(CluCell c1, CluCell c2) {

			return c1.rho > c2.rho ? -1 : 1;
		}
	}
}
