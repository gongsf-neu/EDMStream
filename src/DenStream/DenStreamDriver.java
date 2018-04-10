package DenStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class DenStreamDriver {

	public static void main(String[] args) throws IOException {
		double epsilon = 0;
		double beta = 0;
		double mu = 0;
		double lambda = 0;
		double a = 0;
		String input = null; // data file path
		String output = null; // result fold path
		String pointToMC = null;// generated from output
		String MCToCluster = null;// generated from output
		int dim = 0;
		int initSize = 0;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-e")) {
				epsilon = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-b")) {
				beta = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-m")) {
				mu = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-l")) {
				lambda = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-a")) {
				a = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-in")) {
				input = args[++i];
			}
			if (args[i].equals("-out")) {
				output = args[++i];
			}
		}
		input = "G:/Mypaper/dataset/dpcluster/EDMStream/kddcup99/data.txt";
		output = "G:/Mypaper/dataset/dpcluster/EDMStream/tmpResult";
		epsilon = 100;
		dim = 34;

		beta = 0.6;
		mu = 1.6667;
		lambda = 0.00288;
		a = 2;
		pointToMC = output + "/pointToMC.txt";
		MCToCluster = output + "/mcToCluster";
		initSize = 1000;
		Util.a = a;
		Util.epsilon = epsilon;
		Util.lambda = lambda;
		System.out.println("epsilon=" + Util.epsilon);

		if (epsilon == 0 || beta == 0 || mu == 0 || lambda == 0 || a == 0
				|| input == null || dim == 0 || initSize == 0) {
			System.err.println("Error parameter!");
			System.exit(0);
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(pointToMC));
		System.out.println(pointToMC);
		DenStream denstream = new DenStream(epsilon, beta, mu, lambda, a,
				initSize);
		BufferedReader br = new BufferedReader(new FileReader(input));
		String line = null;

		long start = System.currentTimeMillis();
		long time = start;
		long end = 0;
		long time2 = 0;

		int id = 0;
		boolean outInit = false;
		while ((line = br.readLine()) != null) {// while data stream is active
			StringTokenizer st = new StringTokenizer(line);
//			id = Integer.parseInt(st.nextToken());
			st.nextToken();
			double[] vec = new double[dim];
			for (int i = 0; i < dim; i++) {
				vec[i] = Double.parseDouble(st.nextToken());
			}
			time2 = System.currentTimeMillis() - start;
			Point p = new Point(id, id, vec);
			id++;

			denstream.process(p);

			if (outInit) {
				bw.write(p.id + " " + p.mcid + "\n");
			} else if (denstream.isInitial) {
				System.out.println("+++++++++++++++" + p.id);
				for (Point curp : denstream.initBuffer) {
					bw.write(curp.id + " " + curp.mcid + "\n");
				}
				for (MicroCluster mc : denstream.p_micro_cluster) {
					mc.lt = 0;
					mc.to = 0;
				}
				outInit = true;
			}

			if (p.id % 25000 == 0 && p.id != 0) {
				BufferedWriter bw2 = new BufferedWriter(new FileWriter(
						MCToCluster + (id / 1000) + ".txt"));
				for (Cluster C : denstream.results) {
					for (MicroCluster mc : C.list) {
						bw2.write(mc.id + " ");
					}
					bw2.write("\n");
				}
				bw2.close();
				System.out.print(id / 1000 + " ");
				end = System.currentTimeMillis();
				if(end-time > 25000){
					System.out.println("time out");
					br.close();
					bw.close();
					System.exit(0);
				}
				System.out.println((end - time)/25.0);
				time = end;
			}
		}
		end = System.currentTimeMillis();
		System.out.println(end - start);
		br.close();
		bw.close();
		System.out.println("DenStream");
	}
}
