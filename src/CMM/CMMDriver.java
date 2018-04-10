package CMM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CMMDriver {

	public static void main(String[] args) throws IOException {

		String dataPath = null; // input path
		String CMMPath = null; // result path
		String truthPath = null; // all the groundtruth for cluster map
		int dim = 0;
		double a = 0; // for fade function
		double lambda = 0; // for fade function
		int k = 0; // cmm knn
		int num = 0; // equals stream length

//		dataPath = "G:/Mypaper/dataset/dpcluster/EDMStream/tmpResult";
//		
//		dim = 34;
//		a = 0.998;
//		lambda = 1;
//		k = 3;
//		num = 451;
		// int truth = 0; //
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
			if (args[i].equals("-dim")) {
				dim = Integer.parseInt(args[++i]);
			}
			if (args[i].equals("-k")) {
				k = Integer.parseInt(args[++i]);
			}
			if (args[i].equals("-num")) {
				num = Integer.parseInt(args[++i]);
			}
			if (args[i].equals("-truth")) {
				truthPath = args[++i];
			}
//			if (args[i].equals("-out")) {
//				CMMPath = args[++i];
//			}
		}
		if (dataPath == null || a == 0 || lambda == 0
				|| dim == 0 || k == 0 || num == 0) {
			System.out.println("CMMDriver parameter is error");
			System.exit(0);
		}
		CMMPath = dataPath + "/cmm.txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter(CMMPath, true));
		for (int i = 25; i < num; i += 25) {
			// System.out.println(i);
			String input = dataPath + "/result" + i + ".txt";
			CMM cmm = new CMM(dim, a, lambda, k);
			cmm.load(input, i * 1000);
			cmm.ClusterMap(truthPath);
			double cmmValue = cmm.compCMM(k);
			bw.write(i + " " + cmmValue + "\n");
			bw.flush();
			System.out.println(i + " " + cmmValue);
		}
		bw.close();

	}
}
