package D_Stream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

class P {
	int id;
	String truth;
	int gridId;

	P(int id, String truth) {
		this.id = id;
		this.truth = truth;
	}
}

public class AddGroundTruth {

	public static void addTruth(String id) throws IOException {
		String dataPaht = "G:/Mypaper/dataset/dpcluster/EDMStream/PPA";
		String pointToGrid = dataPaht + "/D_StreamResult"
				+ "/pointToGrid.txt";
		String gridToCluster = dataPaht + "/D_StreamResult/gridToCluster" + id + ".txt";
		String pointGroundTruth = dataPaht + "/label.txt";
		String clusterToTruth = dataPaht + "/D_StreamResult/clusterToTruth" + id + ".txt";

		ArrayList<P> list = new ArrayList<P>();
		BufferedReader br2 = new BufferedReader(
				new FileReader(pointGroundTruth));
		String line2 = null;
		while ((line2 = br2.readLine()) != null) {
			String[] ss = line2.split("\\s+");
			int pointId = Integer.parseInt(ss[0]);
			String truth = ss[1];
			P p = new P(pointId, truth);
			list.add(p);
		}
		br2.close();
		int time = Integer.parseInt(id) * 1000;
		HashMap<Integer, HashSet<P>> map = new HashMap<Integer, HashSet<P>>();
		BufferedReader br = new BufferedReader(new FileReader(pointToGrid));
		String line = null;
		for (int i = 0; (line = br.readLine()) != null && i <= time; i++) {
			String[] ss = line.split("\\s+");
			int pointId = Integer.parseInt(ss[0]);
			int gridId = Integer.parseInt(ss[1]);
			if (map.containsKey(gridId)) {
				map.get(gridId).add(list.get(pointId));
			} else {
				HashSet<P> set = new HashSet<P>();
				set.add(list.get(pointId));
				map.put(gridId, set);
			}
		}
		br.close();

		BufferedReader br3 = new BufferedReader(new FileReader(gridToCluster));
		BufferedWriter bw = new BufferedWriter(new FileWriter(clusterToTruth));
		String line3 = null;
		while ((line3 = br3.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line3);
			while (st.hasMoreTokens()) {
				int gridId = Integer.parseInt(st.nextToken());
				Iterator<P> itr = map.get(gridId).iterator();
				while (itr.hasNext()) {
					P p = itr.next();
					bw.write(p.truth + " ");
				}
			}
			bw.write("\n");
		}
		br3.close();
		bw.close();
	}

	public static void main(String[] args) throws IOException {
		for (int i = 1; i < 450; i++) {
			if (i % 25 == 0) {
				AddGroundTruth.addTruth(i+"");
			}
		}
//		AddGroundTruth.addTruth("final");
		System.out.println("finished");
	}
}
