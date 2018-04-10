package MR_Stream;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Test {

	public static void test1() {
		Set<Integer> set = new HashSet<Integer>();
		set.add(1);
		set.add(2);
		set.add(3);
		set.add(8);
		set.add(11);
		set.add(12);
		while(!set.isEmpty()){
			System.out.println("size:" +set.size());;
			Iterator<Integer> itr = set.iterator();
			int a = itr.next();
			itr.remove();
			Queue<Integer> queue = new LinkedList<Integer>();
			queue.add(a);
			while(!queue.isEmpty()){
				int c = queue.poll();
				System.out.println(c);
				Iterator<Integer> itr2 = set.iterator();
				while(itr2.hasNext()){
					int b = itr2.next();
					if(Math.abs(c-b) == 1){
						queue.add(b);
						itr2.remove();
					}
				}
			}
		}
	}
	
	public static void test2(String[] args) {
		HashMap<String, MRCell> map = new HashMap<String, MRCell>();
		map.put("1", new MRCell());
		map.put("2", new MRCell());
		map.put("3", new MRCell());
		map.put("4", new MRCell());
		MRCell mrcell1 = new MRCell();
		MRCell mrcell2 = new MRCell();
		map.put("5", mrcell1);
		map.put("6", mrcell2);
		System.out.println(map.containsKey(new String("5")));
		map.remove("5");
		System.out.println(map.containsKey("5"));
		
	}
	
	public static void main(String[] args) {
		int[] ii = {1,1,1, 1};
		StringBuilder s = new StringBuilder();
		for(int i = 0 ; i < ii.length; i++){
			s.append(ii[i]);
		}
		BigInteger bi = new BigInteger(s.toString(), 2);
		System.out.println(bi.toString());
	}

}
