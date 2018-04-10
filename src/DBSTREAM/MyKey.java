package DBSTREAM;

import java.util.HashSet;
import java.util.Set;

public class MyKey {

	public MicroCluster mc1;
	public MicroCluster mc2;

	public MyKey(MicroCluster mc1, MicroCluster mc2) {
		this.mc1 = mc1;
		this.mc2 = mc2;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MyKey) {
			MyKey mykey = (MyKey) obj;
			if (mc1 == mykey.mc1 && mc2 == mykey.mc2) {
				return true;
			}
			if (mc1 == mykey.mc2 && mc2 == mykey.mc1) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return mc1.hashCode() + mc2.hashCode();
	}

//	public static void main(String[] args) {
//		MicroCluster mc1 = new MicroCluster();
//		MicroCluster mc2 = new MicroCluster();
//		MicroCluster mc3 = new MicroCluster();
//		MyKey mykey1 = new MyKey(mc1, mc2);
//		MyKey mykey2 = new MyKey(mc1, mc3);
//		MyKey mykey3 = new MyKey(mc2, mc1);
//		MyKey mykey4 = new MyKey(mc1, mc2);
//		Set<MyKey> set = new HashSet<MyKey>();
//		set.add(mykey3);
//
//		System.out.println(set.contains(mykey2));
//		System.out.println(set.contains(mykey1));
//		System.out.println(set.contains(mykey4));
//	}

}
