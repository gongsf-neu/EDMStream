package DBSTREAM;

public class test {
	
	public static void main(String[] args) {
		double result = 1;
		for(int i = 0 ; i < 100; i++){
			result = result * Math.pow(1.002, -1);
//			System.out.println(result);
		}
//		System.out.println(Math.pow(1.02, -100));
//		System.out.println(Math.pow(1.002, -1000));
//		System.out.println(Math.pow(0.998, 1000));
		
		System.out.println(Math.pow(2,-0.0028));
		System.out.println(Math.pow(2,-0.0028*2));
		System.out.println(Math.pow(2,-0.0028)*Math.pow(2,-0.0028));
	}
}
