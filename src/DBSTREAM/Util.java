package DBSTREAM;

public class Util {
	
	public static double a;
	public static double lambda;
	
	public static double decayFun(long curt, long lastT){
		return Math.pow(a, -lambda*(curt-lastT));
	}

}
