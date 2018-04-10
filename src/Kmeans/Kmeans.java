package Kmeans;

import java.util.List;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Kmeans {

	public static class Point {
		double x;
		double y;
		int id;

		public Point(int id, double x, double y) {
			this.id = id;
			this.x = x;
			this.y = y;
		}
	}

	public static double distance(Point p1, Point p2) {
		return Math.sqrt(((p1.x - p2.x) * (p1.x - p2.x))
				+ ((p1.y - p2.y) * (p1.y - p2.y)));
	}

	public final static int pnum = 312;

	public static Point[] p = new Point[pnum];

	public static void process() {
		String input = "C:/Users/NEU/Desktop/spiral.txt";
		String output1 = "C:/Users/NEU/Desktop/out1.txt";
		String output2 = "C:/Users/NEU/Desktop/out2.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(input));
			String s = null;
			int l = 0;
			while ((s = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(s);
				double x = Double.parseDouble(st.nextToken());
				double y = Double.parseDouble(st.nextToken());

				p[l] = new Point(0, x, y);
				l++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int k = 3;
		Point[] point = new Point[k];
		for (int i = 0; i < k; i++) {
			point[i] = p[i * 10];
		}

		List<Point>[] list = new ArrayList[k];

		for (int i = 0; i < k; i++) {
			list[i] = new ArrayList<Point>();
		}

		int cyc = 0;
		while (cyc < 100) {
			// 100, 200, 300
			for (int i = 0; i < k; i++) {
				list[i].clear();
			}
			for (int i = 0; i < pnum; i++) {
				double minValue = Double.MAX_VALUE;
				int minId = -1;
				for (int ks = 0; ks < k; ks++) {
					double dis = distance(p[i], point[ks]);
					if (minValue > dis) {
						minValue = dis;
						minId = ks;
					}
				}
				list[minId].add(p[i]);
			}

			for (int i = 0; i < k; i++) {
				double x = 0;
				double y = 0;
				for (int j = 0; j < list[i].size(); j++) {
					x += list[i].get(j).x;
					y += list[i].get(j).y;
				}
				x = x / list[i].size();
				y = y / list[i].size();
				System.out.println(x + " , " + point[i].x + " : " + y + ", "
						+ point[i].y);
				point[i] = new Point(i, x, y);
			}
			System.out.println();
			System.out.println();
			cyc++;
		}

		try {
			BufferedWriter bw1 = new BufferedWriter(new FileWriter(output1));
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(output2));

			for (int i = 0; i < k; i++) {
				if (i < 8) {
					for (int j = 0; j < list[i].size(); j++) {
						bw1.write(list[i].get(j).x + " " + list[i].get(j).y
								+ "\t" + i + "\n");
					}
				} else {
					for (int j = 0; j < list[i].size(); j++) {
						bw2.write(list[i].get(j).x + " " + list[i].get(j).y
								+ "\t" + i + "\n");
					}
				}
			}

			bw1.close();
			bw2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		process();
	}
}
