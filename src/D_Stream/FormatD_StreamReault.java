package D_Stream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class FormatD_StreamReault {

	public void Format(String cellToCluster, String pointToCell,
			String pointToTruth, String point, String output, int time)
			throws IOException {
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

		BufferedReader pointToCellReader = new BufferedReader(new FileReader(
				pointToCell));
		BufferedReader pointReader = new BufferedReader(new FileReader(point));
		BufferedReader pointToTruthReader = new BufferedReader(new FileReader(
				pointToTruth));
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));

		String pointToCellLine = null;
		String pointLine = null;
		String pointToTruthLine = null;
		for (int i = 0; i <= time; i++) {
			pointToCellLine = pointToCellReader.readLine();
			pointLine = pointReader.readLine();
			pointToTruthLine = pointToTruthReader.readLine();

			int cellId = Integer.parseInt(pointToCellLine.split("\\s+")[1]);
			if (cellToClusterMap.containsKey(cellId)) {
				bw.write(pointLine + " " + pointToTruthLine.split("\\s+")[1]
						+ " " + cellToClusterMap.get(cellId) + "\n");
			}
		}

		pointToCellReader.close();
		pointReader.close();
		pointToTruthReader.close();
		bw.close();
	}

	public static void main(String[] args) throws IOException {

		String dataPath = "G:/Mypaper/dataset/dpcluster/EDMStream/PPA";
//		String dataPath = null;
		int num = 426;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-in")) {
				dataPath = args[++i];
			}
			if (args[i].equals("-num")) {
				num = Integer.parseInt(args[++i]);
			}
		}
		for (int i = 25; i < num; i += 25) {
			String cellToCluster = dataPath + "/D_StreamResult/gridToCluster"
					+ i + ".txt";
			String pointToCell = dataPath + "/D_StreamResult/pointToGrid.txt";
			String pointToTruth = dataPath + "/label.txt";
			String point = dataPath + "/data.txt";
			String output = dataPath + "/D_StreamResult/result" + i + ".txt";
			FormatD_StreamReault fr = new FormatD_StreamReault();
			fr.Format(cellToCluster, pointToCell, pointToTruth, point, output,
					i * 1000);
			System.out.println(i);
		}
	}
}
