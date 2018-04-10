package EDMStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class FormatReault {

	public void Format(String cellToCluster, String pointToCell,
			String pointToTruth, String point, String output, int time)
			throws IOException {
//		System.out.println(cellToCluster);
//		System.out.println(pointToCell);
//		System.out.println(pointToTruth);
//		System.out.println(point);
//		System.out.println(output);
//		System.out.println(time);
		// String cellToCluster = "";
		// String pointToCell = "";
		// String pointToTruth = "";
		// String point = "";

		Map<Integer, Integer> cellToClusterMap = new HashMap<Integer, Integer>();
		BufferedReader br = new BufferedReader(new FileReader(cellToCluster));
		String line = null;
		for (int i = 0; (line = br.readLine()) != null; i++) {
			StringTokenizer st = new StringTokenizer(line);
			while (st.hasMoreTokens()) {
				cellToClusterMap.put(Integer.parseInt(st.nextToken()), i);
			}
		}
		br.close();
//		System.out.println(cellToClusterMap.size());
		BufferedReader pointToCellReader = new BufferedReader(new FileReader(
				pointToCell));
		BufferedReader pointReader = new BufferedReader(new FileReader(point));
		BufferedReader pointToTruthReader = new BufferedReader(new FileReader(
				pointToTruth));
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));

		String pointToCellLine = null;
		String pointLine = null;
		String pointToTruthLine = null;
		for (int i = 0; i < time
				&& (pointToCellLine = pointToCellReader.readLine()) != null; i++) {

			pointLine = pointReader.readLine();
			pointToTruthLine = pointToTruthReader.readLine();
			if (pointToTruthLine == null) {
				System.out.println(i);
			}
			int cellId = Integer.parseInt(pointToCellLine.split("\\s+")[1]);
			if (cellToClusterMap.containsKey(cellId)) {
				
				bw.write(pointLine + " " + pointToTruthLine.split("\\s+")[1]
						+ " " + cellToClusterMap.get(cellId) + "\n");
//				String[] ss = pointLine.split("\\s+");
//				bw.write(ss[1] + " " + ss[2] + " "
//						+ cellToClusterMap.get(cellId) + "\n");
			}
		}

		pointToCellReader.close();
		pointReader.close();
		pointToTruthReader.close();
		bw.close();
	}

	public static void main(String[] args) throws IOException {
		String resultPath = "G:/Mypaper/experiment/dpcluster/EDMStream/tmpResult";
		String dataPath = "G:/Mypaper/experiment/dpcluster/EDMStream/covtype";
		int num = 576;
		String pointToCell = resultPath + "/pointToCell.txt";
		String pointToTruth = dataPath + "/label.txt";
		String point = dataPath + "/data.txt";
		// String dataPath = null;
		// int num = 0;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-in")) {
				dataPath = args[++i];
			}
			if (args[i].equals("-num")) {
				num = Integer.parseInt(args[++i]);
			}
		}
		for (int id = 25; id < num; id += 25) {
			String cellToCluster = resultPath + "/cellToCluster"
					+ id + ".txt";
			
			String output = resultPath + "/result" + id + ".txt";
			FormatReault fr = new FormatReault();
			fr.Format(cellToCluster, pointToCell, pointToTruth, point, output,
					id * 1000);
			System.out.println(id);
		}
	}
}
