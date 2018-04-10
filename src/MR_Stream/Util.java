package MR_Stream;

import java.math.BigInteger;

public class Util {
	
	public static double DL; // sparse threshold
	public static double DH; // dense threshold 
	public static double lambda;
	public static double a;
	public static int H;
	public static int dim;
//	public static int num;
	public static long tp;
	public static BigInteger partNum;
	public static double eplislon;
	public static double mu;
	public static double beta;
	public static int CH;
	public static int mcid = 0;
	public static double CL;
	
	public static String hashFun(int[] vec){
//		long sum = 0;
		int dim = vec.length;
		StringBuilder biString = new StringBuilder();
		for(int i = 0; i < dim; i++){
			if(vec[i] == 0 || vec[i] == 1){
				biString.append(vec[i]);
			}else{
				System.out.println("there is not 0 or 1 in hashFunction vector");
			}
		}
		BigInteger bi = new BigInteger(biString.toString(), 2);
		return bi.toString();
	}
	
	public static double p(long time, long la, int h) {
		double val = DL*(1-Math.pow(lambda, -a*(time-la)-20));
		return val;
	}
	
//	public static double decayFun(long lt, long startTime){
//		return Math.pow(lambda, -(a* (startTime - lt)));
//	}
}
