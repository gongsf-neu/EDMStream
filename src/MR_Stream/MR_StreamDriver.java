package MR_Stream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.StringTokenizer;

public class MR_StreamDriver {

	public static void main(String[] args) throws IOException {

		double CH = 0;
		double CL = 0;
		double lambda = 0;
		int H = 0;
		double epsilon = 0; // minDis of connected grid
		double beta = 0;
		double mu = 0;
		int dim = 0;
		String input = null;
		String output = null;
		String pointToCell = null;
		String cellToCluster = null;
		
		double a = 0;
		int h = 0;

		CH = 3.0;
		CL = 0.8;
		lambda = 1.002;
		H = 5;
		beta = 0;
		mu = 2;
		input = "G:/Mypaper/dataset/dpcluster/EDMStream/kddcup99/data.txt";
		dim = 34;
		epsilon = 100;//for connect the grid no impact with data.
		output = "G:/Mypaper/dataset/dpcluster/EDMStream/kddcup99/MR-StreamResult";
		a = 1;
		h = 6;

		if (CH == 0 || CL == 0 || lambda == 0 || H == 0 || epsilon == 0
				 || dim == 0 || input == null
				|| output == null || h == 0) {
			System.err.println("Invalid parameter");
			System.exit(0);
		} else {
			Util.a = a;
			System.out.println("Util.a=" + Util.a);
			Util.beta = beta;
			Util.mu = mu;
			Util.dim = dim;
			Util.eplislon = epsilon;
			Util.CL = CL;
//			Util.DL = CL / (Math.pow(2, dim*H) * (1 - Math.pow(lambda, -a)));
			Util.DL = CL / ((1 - Math.pow(lambda, -a)));
			System.out.println("CL=" + CL);
			System.out.println("Util.DL="+Util.DL);
			Util.DH = CH / (Math.pow(2, dim*H) * (1 - Math.pow(lambda, -a)));
			Util.DH = CH / ( (1 - Math.pow(lambda, -a)));
			System.out.println("Util.DH="+Util.DH);
			Util.H = H;
			Util.tp = (long) Math.ceil((1 / a)
					* (Math.log(CH / CL) / Math.log(lambda)));
			// Util.tp = 1;
			System.out.println("t_p=" + Util.tp);
			StringBuilder one = new StringBuilder("1");
			for(int i = 0; i < dim; i++){
				one.append("0");
			}
			Util.partNum = new BigInteger(one.toString(), 2);
			Util.lambda = lambda;
			System.out.println("Util.lambda=" + Util.lambda);
			pointToCell = output + "/pointToCell.txt";
			cellToCluster = output + "/cellToCluster";
			System.out.println("partNum=" + Util.partNum);
		}
		MR_Stream mrstream = new MR_Stream(dim, input, H, h);
		BufferedWriter bw = new BufferedWriter(new FileWriter(pointToCell));
		BufferedReader br = new BufferedReader(new FileReader(input));
		String line = null;
		long start = System.currentTimeMillis();
		long time = start;
		long end = 0;
		long time2 = 0;
		int id = 0;
//		System.out.println(id);
		while ((line = br.readLine()) != null) {// while data stream is active
			StringTokenizer st = new StringTokenizer(line);
//			int id = Integer.parseInt(st.nextToken());
//			st.nextToken();
			double[] vec = new double[dim];
			for (int i = 0; i < dim; i++) {
				vec[i] = Double.parseDouble(st.nextToken());
			}
			
			time2 = System.currentTimeMillis() - start;
			Point p = new Point(id, id, vec);
			id++;
			mrstream.process(p);
//			System.out.println(id);
			bw.write(p.id + "");
			for (int i : p.mrcellID) {
				bw.write(" " + i);
			}
			bw.write("\n");
//			bw.write(mrstream.mrtree.nodeNum+"\n");
			if (p.id % 25000 == 0 && p.id != 0) {
				String cellToClustertemp = cellToCluster + (id / 1000) + ".txt";
				BufferedWriter bw2 = new BufferedWriter(new FileWriter(
						cellToClustertemp));
				for (Cluster C : mrstream.result) {
					for (MRCell mrcell : C.list) {
						bw2.write(mrcell.id + " ");
					}
					bw2.write("\n");
				}
				bw2.close();
				// System.out.println(mrstream.mrtree.nodeNum);
				System.out.print(id/1000 + " ");
				end = System.currentTimeMillis();
//				if(end-time > 25000){
//					System.out.println("time out");
//					br.close();
//					bw.close();
//					System.exit(0);
//				}
				System.out.println(end-start);
				time = end;
			}
		}
		end = System.currentTimeMillis();
		System.out.println(end - start);
		br.close();
		bw.close();
		System.out.println("MR-Stream");
	}
	
}
