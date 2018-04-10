package EDMStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class GetPurity {
	
	public static void getPurity(int k){
//		System.out.println(k);
		String idToLabelinput = "G:/ResearchWork/paper/mypaper/dpcluster/StreamDP/dataset/wwwpaper/"
				+ "kddcup99/result/kddcup99label.txt";
		String pointToCell = "G:/ResearchWork/paper/mypaper/dpcluster/StreamDP/dataset/wwwpaper/"
				+ "kddcup99/pointToCell.txt";
		String C2cPath = "G:/ResearchWork/paper/mypaper/dpcluster/StreamDP/dataset/wwwpaper/"
				+ "kddcup99/result"+k+".txt";
//		String C2cPath = "G:/ResearchWork/paper/mypaper/dpcluster/StreamDP/dataset/wwwpaper/"
//				+ "kddcup99/resultfinal.txt";
		Map<Integer, HashMap<String, Integer>> map2 = new HashMap<Integer, HashMap<String, Integer>>();
		try {
			BufferedReader brIdToLabel = new BufferedReader(new FileReader(
					idToLabelinput));
			String[] label = new String[494021];
			String line = null;
			int id = 0;
			while ((line = brIdToLabel.readLine()) != null) {
				StringTokenizer stk1 = new StringTokenizer(line);
				id = Integer.parseInt(stk1.nextToken());
//				stk1.nextToken();
//				stk1.nextToken();
				label[id] = stk1.nextToken();
			}
			brIdToLabel.close();

			BufferedReader brC2c = new BufferedReader(new FileReader(C2cPath));
			Map<Integer, Integer> map1 = new HashMap<Integer, Integer>();
			line = brC2c.readLine();
			while ((line = brC2c.readLine()) != null) {
				StringTokenizer stk2 = new StringTokenizer(line);
				int cid = Integer.parseInt(stk2.nextToken());
				int Cid = Integer.parseInt(stk2.nextToken());
				map1.put(cid, Cid);
			}
			brC2c.close();

			BufferedReader brP2C = new BufferedReader(new FileReader(
					pointToCell));
			line = null;
			while ((line = brP2C.readLine()) != null) {
				StringTokenizer stk3 = new StringTokenizer(line);
				int pid = Integer.parseInt(stk3.nextToken());
				int cid = Integer.parseInt(stk3.nextToken());
				if (map1.containsKey(cid)) {
					int Cid = map1.get(cid);
					if (map2.containsKey(Cid)) {
						HashMap<String, Integer> curMap = map2.get(Cid);
						if (curMap.containsKey(label[pid])) {
							int num = curMap.get(label[pid]);
							curMap.put(label[pid], num + 1);
						} else {
							curMap.put(label[pid], 1);
						}
					} else {
						HashMap<String, Integer> curMap = new HashMap<String, Integer>();
						curMap.put(label[pid], 1);
						map2.put(Cid, curMap);
					}
				}
			}
			brP2C.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			int Cnum = map2.size();
			double[] purs = new double[Cnum];
			double purity = 0;
			String output = "";
			// BufferedWriter bw = new BufferedWriter(new FileWriter(output));
			
//			System.out.println(Cnum);
			for (int i = 0; i < Cnum; i++) {
				double max = 0;
				double sum = 0;
				Map<String, Integer> curMap = map2.get(i);
				for (Map.Entry<String, Integer> entry : curMap.entrySet()) {
//					System.out.print(entry.getKey() + " ");
					double num = entry.getValue();
					if (num > max) {
						max = num;
					}
					sum += num;
				}
				purs[i] = max / sum;
//				System.out.println(purs[i]);
				purity += purs[i];
			}
//			bw.write(purity/Cnum + "\n");
			System.out.println( purity / Cnum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
//		getPurity(375);
		for(int i = 1; i < 20; i++){
			getPurity(i*25);
		}
		
//		String idToLabelinput = "G:/ResearchWork/paper/mypaper/dpcluster/StreamDP/dataset/wwwpaper/kddcup99/result/kddcup99label.txt";
//		String pointToCell = "G:/ResearchWork/paper/mypaper/dpcluster/StreamDP/dataset/wwwpaper/kddcup99/result/pointToCell.txt";
//		String C2cPath = "G:/ResearchWork/paper/mypaper/dpcluster/StreamDP/dataset/wwwpaper"
//				+ "/kddcup99/result/static/result225.txt";
//		Map<Integer, HashMap<String, Integer>> map2 = new HashMap<Integer, HashMap<String, Integer>>();
//		try {
//			BufferedReader brIdToLabel = new BufferedReader(new FileReader(
//					idToLabelinput));
//			String[] label = new String[494021];
//			String line = null;
//			int id = 0;
//			while ((line = brIdToLabel.readLine()) != null) {
//				StringTokenizer stk1 = new StringTokenizer(line);
//				id = Integer.parseInt(stk1.nextToken());
//				label[id] = stk1.nextToken();
//			}
//			brIdToLabel.close();
//
//			BufferedReader brC2c = new BufferedReader(new FileReader(C2cPath));
//			Map<Integer, Integer> map1 = new HashMap<Integer, Integer>();
//			line = brC2c.readLine();
//			while ((line = brC2c.readLine()) != null) {
//				StringTokenizer stk2 = new StringTokenizer(line);
//				int cid = Integer.parseInt(stk2.nextToken());
//				int Cid = Integer.parseInt(stk2.nextToken());
//				map1.put(cid, Cid);
//			}
//			brC2c.close();
//
//			BufferedReader brP2C = new BufferedReader(new FileReader(
//					pointToCell));
//			line = null;
//			while ((line = brP2C.readLine()) != null) {
//				StringTokenizer stk3 = new StringTokenizer(line);
//				int pid = Integer.parseInt(stk3.nextToken());
//				int cid = Integer.parseInt(stk3.nextToken());
//				if (map1.containsKey(cid)) {
//					int Cid = map1.get(cid);
//					if (map2.containsKey(Cid)) {
//						HashMap<String, Integer> curMap = map2.get(Cid);
//						if (curMap.containsKey(label[pid])) {
//							int num = curMap.get(label[pid]);
//							curMap.put(label[pid], num + 1);
//						} else {
//							curMap.put(label[pid], 1);
//						}
//					} else {
//						HashMap<String, Integer> curMap = new HashMap<String, Integer>();
//						curMap.put(label[pid], 1);
//						map2.put(Cid, curMap);
//					}
//				}
//			}
//			brP2C.close();
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			int Cnum = map2.size();
//			double[] purs = new double[Cnum];
//			double purity = 0;
//			String output = "";
//			// BufferedWriter bw = new BufferedWriter(new FileWriter(output));
//			
//			
//			for (int i = 0; i < Cnum; i++) {
//				double max = 0;
//				double sum = 0;
//				Map<String, Integer> curMap = map2.get(i);
//				for (Map.Entry<String, Integer> entry : curMap.entrySet()) {
//					System.out.print(entry.getKey() + " ");
//					double num = entry.getValue();
//					if (num > max) {
//						max = num;
//					}
//					sum += num;
//				}
//				purs[i] = max / sum;
//				System.out.println(purs[i]);
//				purity += purs[i];
//			}
//			System.out.println(purity / Cnum);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}
