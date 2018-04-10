package EDMStream;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class EstimateRadius {

	public static void main(String[] args) throws IOException {

		String dataPath = null;

		int dim = 0;
		int sampleNum = 0;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-dim")) {
				dim = Integer.parseInt(args[++i]);
			}
			if (args[i].equals("-in")) {
				dataPath = args[++i];
			}
			if (args[i].equals("-sam")) {
				sampleNum = Integer.parseInt(args[++i]);
			}
		}

		if (dataPath == null || dataPath.equals("") || dim == 0
				|| sampleNum == 0) {
			System.out.println("invalie parameter");
			System.exit(0);
		}

		String inputPath = dataPath;
		Point[] points = new Point[sampleNum];

		BufferedReader br = new BufferedReader(new FileReader(inputPath));
		List<Double> disList = new ArrayList<Double>();
		String line = null;
		for (int i = 0; (line = br.readLine()) != null; i++) {
			StringTokenizer stk = new StringTokenizer(line);
			stk.nextToken();
			int id = 1;
			double[] data = new double[dim];
			for (int j = 0; j < dim; j++) {
				data[j] = Double.parseDouble(stk.nextToken());
			}
			Point p = new Point(id, id, data);

			if (i < sampleNum) {
				points[i] = p;
			} else {
				int j = (int) (Math.random() * i);
				if (j < sampleNum) {
					points[j] = p;
				}
			}
			if (i % 1000 == 0) {
				System.out.println(i);
			}
		}
		br.close();
		for (int i = 0; i < sampleNum; i++) {
			for (int j = 0; j < i; j++) {
				double dis = points[i].getDisTo(points[j]);
				disList.add(dis);
			}
		}
		Collections.sort(disList);
		int size = disList.size();
		System.out.println(size);
		int index = (int) (size * 0.005);
		System.out.println("0.5% = " + disList.get(1));
		index = (int) (size * 0.01);
		System.out.println("1% = " + disList.get(index));
		index = (int) (size * 0.015);
		System.out.println("1.5% = " + disList.get(index));
		index = (int) (size * 0.02);
		System.out.println("2% = " + disList.get(index));
		index = (int) (size * 0.03);
		System.out.println("3% = " + disList.get(index));
		index = (int) (size * 0.04);
		System.out.println("4% = " + disList.get(index));
		index = (int) (size * 0.1);
		System.out.println("10% = " + disList.get(index));

		index = (int) (size * 0.2);
		System.out.println("20% = " + disList.get(index));

		index = (int) (size * 0.3);
		System.out.println("30% = " + disList.get(index));

		index = (int) (size * 0.4);
		System.out.println("40% = " + disList.get(index));

		index = (int) (size * 0.5);
		System.out.println("50% = " + disList.get(index));

		System.out.println("100%=" + disList.get(size - 1));
	}
}
