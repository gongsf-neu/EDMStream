package CMM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class getTruth {
	
	public static void main(String[] args) throws IOException {
		Set<String> set = new HashSet<String>();
		String dataPaht = "G:/Mypaper/dataset/dpcluster/EDMStream/kddcup99";
		String path = dataPaht + "/label.txt";
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = null;
		while((line = br.readLine()) != null){
			String[] ss = line.split("\\s+");
			String truth = ss[1];
			if(!set.contains(truth)){
				set.add(truth);
			}
		}
		br.close();
		System.out.println(set.size());
		Iterator<String> itr = set.iterator();
		while(itr.hasNext()){
			System.out.println(itr.next());
		}
	}

}
