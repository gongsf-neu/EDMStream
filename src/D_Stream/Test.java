package D_Stream;

import java.util.HashMap;
import java.util.Map;

public class Test {

	public static void main(String[] args) {
		int[] v1 = new int[10];
		int[] v2 = new int[10];
		int[] v3 = new int[10];
		int[] v4 = new int[10];

		for (int i = 0; i < 10; i++) {
			v1[i] = i * 2;
			v2[i] = i * 2;
			v3[i] = i * 3;
			v4[i] = i * 5;
		}

		Key k1 = new Key(v1);
		Key k2 = new Key(v2);
		Key k3 = new Key(v3);
		Key k4 = new Key(v4);
		Map<Key, String> map = new HashMap<Key, String>();
		map.put(k1, "k1");
		System.out.println(map.containsKey(k2));
		map.put(k2, "k2");
		System.out.println(map.get(k1));
	}
}
