package MR_Stream;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class MR_Stream {

	public int dim;
	public double[] max;
	public double[] min;
	public int H;
	public double lambda;
	public double a;
	public int h;
	long nodesNum;
	MRTree mrtree;
	List<Cluster> result;

	public MR_Stream(int dim, String input, int H, int h) throws IOException {
		this.dim = dim;
		min = new double[dim];
		max = new double[dim];
		for (int i = 0; i < dim; i++) {
			min[i] = Double.POSITIVE_INFINITY;
			max[i] = Double.NEGATIVE_INFINITY;
		}
		BufferedReader br = new BufferedReader(new FileReader(input));
		String line = null;
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);
			// st.nextToken();// skip pid
			double[] vec = new double[dim];
			for (int i = 0; i < dim; i++) {
				vec[i] = Double.parseDouble(st.nextToken());
				min[i] = min[i] < vec[i] ? min[i] : vec[i];
				max[i] = max[i] > vec[i] ? max[i] : vec[i];
			}
		}
		br.close();
		this.H = H;
		nodesNum = 0;
		mrtree = new MRTree(min, max);
		this.h = h;
	}

	public void process(Point p) throws IOException {
		MRCell g = mrtree.add(p);
		mrtree.prTreeUp(g);
		if (p.id % Util.tp == 0 && p.id != 0) {
			if (nodesNum != mrtree.NodeNum()) {
				nodesNum = mrtree.NodeNum();
				mrtree.generateSetForClustering(h);
			}
			mrtree.updateTreeWeight(p.startTime);
			mrtree.pruneTreeDown(p.startTime);
			mrtree.mergeDown(Type.DENSE);
			mrtree.mergeDown(Type.SPARSE);
		}
		result = new ArrayList<Cluster>();
		if (p.id % 25000 == 0 && p.id != 0)
			mrtree.cluster(h, result);
	}
}
