package DBSTREAM;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class DBSTREAMDriver {

	public static void main(String[] args) throws IOException {

		double r = 0;
		double lambda = 0;
		double a = 0;
		long gapTime = 0;
		double alpha = 0;
		double thres = 0;
		String input = null;// the data file
		String output = null; // the output fold
		String pointToMC = null;
		String MCToCluster = null;
		int dim = 0;

		input = "G:/Mypaper/dataset/dpcluster/EDMStream/ppa/data.txt";
		output = "G:/Mypaper/dataset/dpcluster/EDMStream/tmpResult";
		r = 5;
		dim = 51;
		
		lambda = 0.00288;
		a = 2;
		gapTime = 1000;
		alpha = 0;
		thres = 0; // the parameter of alpha in paper
		pointToMC = output + "/pointToMC.txt";
		MCToCluster = output + "/mcToCluster";

		if (r == 0 || lambda == 0 ||  gapTime == 0 || dim == 0 ) {
			System.err.println("Parameter Error!");
			System.exit(0);
		}

		Util.a = a;
		Util.lambda = lambda;

		BufferedReader br = new BufferedReader(new FileReader(input));
		BufferedWriter bw = new BufferedWriter(new FileWriter(pointToMC));
		DBSTREAM dbstream = new DBSTREAM(r, lambda, a, gapTime, alpha);
		String line = null;
		long start = System.currentTimeMillis();
		long time2 = 0;
		long time = start;
		long end = 0;
//		int id = 0;
		while ((line = br.readLine()) != null) {// while data stream is active
			StringTokenizer st = new StringTokenizer(line);
			int id = Integer.parseInt(st.nextToken());
//			st.nextToken();
			double[] vec = new double[dim];
			for (int i = 0; i < dim; i++) {
				vec[i] = Double.parseDouble(st.nextToken());
			}
//			if(st.hasMoreTokens()){
//				System.out.println("dim error");
//				System.exit(0);
//			}
			
			time2 = System.currentTimeMillis()-start;
			Point p = new Point(id, id, vec);
			id++;
			
			List<MicroCluster> nn = dbstream.update(p);
			
			Set<Cluster> result = dbstream.recluster(p.startTime, thres);
			
			bw.write(p.id + " : ");
			for (MicroCluster mc : nn) {
				bw.write(mc.id + " " + mc.dis + " || ");
			}
			bw.write("\n");
			if (id % 25000 == 0 && id != 0) {
				end = System.currentTimeMillis();
				System.out.print(id/1000 + " ");
				if(end-time > 25000){
					System.out.println("time out");
					br.close();
					bw.close();
					System.exit(0);
				}
				BufferedWriter bw2 = new BufferedWriter(new FileWriter(
						MCToCluster + (id / 1000) + ".txt"));
				for(Cluster C : result){
					for(MicroCluster mc : C.list){
						bw2.write(mc.id + " ");
					}
					bw2.write("\n");
				}
				bw2.close();
				end = System.currentTimeMillis();
				System.out.println((end-time)/25.0);
				time = end;
			}
		}
		br.close();
		bw.close();
		System.out.println("DBSTREAM");
	}
}
