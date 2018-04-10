package DenStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class FormatResult {

	public static void Format(String MCToCluster, String pointToMC,
			String pointToTruth, String point, String output, int time)
			throws IOException {

		Map<Integer, Integer> cellToClusterMap = new HashMap<Integer, Integer>();
		BufferedReader br = new BufferedReader(new FileReader(MCToCluster));
		String line = null;
		for (int i = 0; (line = br.readLine()) != null; i++) {
			StringTokenizer st = new StringTokenizer(line);
			while (st.hasMoreTokens()) {
				cellToClusterMap.put(Integer.parseInt(st.nextToken()), i);
			}
		}
		br.close();

		BufferedReader pointToMCReader = new BufferedReader(new FileReader(
				pointToMC));
		BufferedReader pointReader = new BufferedReader(new FileReader(point));
		BufferedReader pointToTruthReader = new BufferedReader(new FileReader(
				pointToTruth));
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));

		String pointToMCLine = null;
		String pointLine = null;
		String pointToTruthLine = null;
		for (int i = 0; i <= time; i++) {
			pointToMCLine = pointToMCReader.readLine();
			pointLine = pointReader.readLine();
			pointToTruthLine = pointToTruthReader.readLine();

			int cellId = Integer.parseInt(pointToMCLine.split("\\s+")[1]);
			if (cellToClusterMap.containsKey(cellId)) {
				bw.write(pointLine + " " + pointToTruthLine.split("\\s+")[1]
						+ " " + cellToClusterMap.get(cellId) + "\n");
			}
		}

		pointToMCReader.close();
		pointReader.close();
		pointToTruthReader.close();
		bw.close();
	}

	public static void main(String[] args) throws IOException {

		String streamResultPath = null; // a fold path
		String dataPath = null;
		String labelPath = null;
		int num = 0;

		String pointToMCPath = null;// get by streamResultPath
		String MCToClusterPath = null; // get by streamResultPath
		String output = null; // get by streamResultPath

		streamResultPath = "G:/Mypaper/dataset/dpcluster/EDMStream/PPA/DenStreamResult/";
		dataPath = "G:/Mypaper/dataset/dpcluster/EDMStream/PPA/data.txt";
		labelPath = "G:/Mypaper/dataset/dpcluster/EDMStream/PPA/label.txt";
		num = 426;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-rpath")) {
				streamResultPath = args[++i];
			}
			if (args[i].equals("-data")) {
				dataPath = args[++i];
			}
			if (args[i].equals("-lpath")) {
				labelPath = args[++i];
			}
			if (args[i].equals("-num")) {
				num = Integer.parseInt(args[++i]);
			}
		}

		if (streamResultPath == null || dataPath == null || labelPath == null
				|| num == 0) {
			System.err.println("invalid parameter while formatResult!!");
			System.exit(0);
		}

		pointToMCPath = streamResultPath + "/pointToMC.txt";
		MCToClusterPath = streamResultPath + "/mcToCluster";
		output = streamResultPath + "/result";

		for (int i = 25; i < num; i += 25) {
			System.out.println(i);
			Format(MCToClusterPath + i + ".txt", pointToMCPath, labelPath,
					dataPath, output + i + ".txt", i*1000);
		}
	}

}
