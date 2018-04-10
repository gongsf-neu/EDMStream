package MR_Stream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class FormatResult {

	public static void Format(String cellToCluster, String pointToCell,
			String pointToTruth, String point, String output, int time)
			throws IOException {
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
		int size = 5 + 1;
//		System.out.println("size="+size);
		for (int i = 0; i <= time; i++) {
			pointToCellLine = pointToCellReader.readLine();
			pointLine = pointReader.readLine();
			pointToTruthLine = pointToTruthReader.readLine();
			
			int[] cellIds = new int[size];
			StringTokenizer cellIdT = new StringTokenizer(pointToCellLine);
			cellIdT.nextToken();//skip id
			boolean f = false;
			for (int j = 0; j < size; j++) {
				cellIds[j] = Integer.parseInt(cellIdT.nextToken());
				if (cellToClusterMap.containsKey(cellIds[j])) {
//					System.out.println("ok");
					if (!f) {
						f = true;
					} else {
						System.err
								.println("big error in format result caused by offline cluster");
					}
					bw.write(pointLine + " "
							+ pointToTruthLine.split("\\s+")[1] + " "
							+ cellToClusterMap.get(cellIds[j]) + "\n");
				}
			}
		}

		pointToCellReader.close();
		pointReader.close();
		pointToTruthReader.close();
		bw.close();
	}

	public static void main(String[] args) throws IOException {

		String streamResultPath = null; // a fold path
		String dataPath = null;
		String labelPath = null;
		int num = 0;

		String pointToCellPath = null;// get by streamResultPath
		String cellToClusterPath = null; // get by streamResultPath
		String output = null; // get by streamResultPath

		streamResultPath = "G:/Mypaper/dataset/dpcluster/EDMStream/kddcup99/MR-StreamResult";
		dataPath = "G:/Mypaper/dataset/dpcluster/EDMStream/kddcup99/data.txt";
		labelPath = "G:/Mypaper/dataset/dpcluster/EDMStream/kddcup99/label.txt";
		num = 490;

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

		pointToCellPath = streamResultPath + "/pointToCell.txt";
		cellToClusterPath = streamResultPath + "/cellToCluster";
		output = streamResultPath + "/result";

		for (int i = 25; i < num; i += 25) {
			System.out.println(i);
			Format(cellToClusterPath + i + ".txt", pointToCellPath, labelPath,
					dataPath, output + i + ".txt", i*1000);
		}
	}
}
