package DenStream;

public class Util {
	
	public static double a;
	public static double lambda;
	public static double epsilon;
	
	public static double decayFun(long curt, long lastT){
//		System.out.println(curt);
//		System.out.println(lastT);
//		System.out.println(Math.pow(a, -lambda*(curt-lastT)));
		return Math.pow(a, -lambda*(curt-lastT));
	}

}
