package EDMStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class EDMStreamDriver {

	public void process(String dataPath, String output, double a,
			double lambda, double r, double beta, int dim, int opt, double delta) {

		String inputPath = dataPath;
		String pointToCluCell = output + "/pointToCell.txt";
		String bufferPath = output + "/bufferPath.txt";
		String informationPath = output + "/information.txt";
		String CellToCluster = output + "/cellTocluster";
		
		System.out.println("dim=" + dim);

		int cacheNum = 1000;

		EDMStreamV5 edm = new EDMStreamV5();
		edm.set(a, lambda, cacheNum, dim, r, beta, delta);
		edm.setBufferPath(bufferPath);
		edm.setDecisionPath("");
		long start = System.currentTimeMillis();
		long time = start;
		long end = 0;
		long time2 = 0;
		boolean isInited = false;
//		int id = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputPath));
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					pointToCluCell));
			BufferedWriter infoBw = new BufferedWriter(new FileWriter(
					informationPath));
			BufferedWriter numBw = new BufferedWriter(new FileWriter(output
					+ "/num.txt"));
			String line = null;
			while ((line = br.readLine()) != null) {
				StringTokenizer stk = new StringTokenizer(line);
				int id = Integer.parseInt(stk.nextToken());
//				stk.nextToken();
				double[] data = new double[dim];
				for (int i = 0; i < dim; i++) {
					data[i] = Double.parseDouble(stk.nextToken());
				}
				
				time2 = System.currentTimeMillis()-start;
				Point p = new Point(id, id/10, data);
				
				CluCell c = edm.retrive(p, opt);
				bw.write(p.id + " " + c.cid + "\n");
				
//				for (int i = 0; i < edm.dpTree.size; i++) {
//					bw.write(edm.dpTree.Clus[i].cid + " "
//							+ edm.dpTree.Clus[i].rho + " ");
//				}
//				bw.write("\n");

				if (p.id % 100 == 0 && edm.isInit) {
					edm.setMinDelta(edm.adjustMinDelta());
					// edm.setMinDelta(50);
					// edm.dpTree.trackCluster(edm.clusters);
					edm.dpTree.adjustCluster(edm.clusters, true);
					edm.delCluster();
				}

				if (p.id % 25000 == 0 && p.id != 0) {
//					System.out.println(p.dim);
					System.out.print(p.id / 1000 + " ");
					infoBw.write(p.id / 1000 + " ");
					infoBw.write("\t minDelta = " + edm.minDelta);
					infoBw.write("\t cluster num = " + edm.clusters.size());
					infoBw.write("\t dptree size = " + edm.dpTree.size + "\n");
					infoBw.flush();
					edm.outResult(CellToCluster + (id / 1000) + ".txt");
					edm.dpTree.writeInfo(CellToCluster + "info" + (id / 1000)
							+ ".txt");
					end = System.currentTimeMillis();
					System.out.println((end - start));
//					System.out.println(edm.dpTree.size);
					time = end;
				}
			}
			br.close();

//			edm.dpTree.writeInfo(CellToCluster + "infofinal" + ".txt");
//			edm.outResult(CellToCluster + "450.txt");
			bw.close();
//			infoBw.write("450 ");
//			infoBw.write("\t minDelta = " + edm.minDelta);
//			infoBw.write("\t cluster num = " + edm.clusters.size());
//			infoBw.write("\t dptree size = " + edm.dpTree.size + "\n");
			infoBw.close();
			numBw.close();

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		end = System.currentTimeMillis();
		System.out.print("final ");
		System.out.println(end - time);
		System.out.println(end - start);
	}

	public static void main(String[] args) {
		int opt = 3;
		String dataPath = null;
		String outputPath = null;
		double a = 0;
		double lambda = 0;
		double r = 0;
		double beta = 0;
		int dim = 0;
		double delta = 3;

		opt = 2; //level of optimization
		dataPath = "G:/Mypaper/experiment/dpcluster/EDMStream/covtype/data.txt";
		outputPath = "G:/Mypaper/experiment/dpcluster/EDMStream/tmpResult";
		dim = 54;
		r = 250;
		a = 0.998;
		lambda = 1;
		beta = 0.0021;
		delta = 1500;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-in")) {
				dataPath = args[++i];
			}
			if (args[i].equals("-a")) {
				a = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-lambda")) {
				lambda = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-r")) {
				r = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-beta")) {
				beta = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-dim")) {
				dim = Integer.parseInt(args[++i]);
			}
			if (args[i].equals("-delt")) {
				delta = Double.parseDouble(args[++i]);
			}
			if (args[i].equals("-opt")) {
				opt = Integer.parseInt(args[++i]);
			}
		}
		if (dim == 0 || beta == 0 || r == 0 || lambda == 0 || a == 0
				|| dataPath == null || opt == 3 || delta == 0) {
			System.out.println("parameter is error");
			System.exit(0);
		}
		EDMStreamDriver ed = new EDMStreamDriver();
		ed.process(dataPath, outputPath, a, lambda, r, beta, dim, opt, delta);
	}
}
