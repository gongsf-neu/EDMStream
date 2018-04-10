package DenStream;

public class test {

	public static void main(String[] args) {
		 System.out.println(Math.pow(0.998, 1664));
		 System.out.println(Math.pow(1.002, -1000));
//		 System.out.println(Math.log(0.998)/Math.log(0.25));
		 System.out.println(60874L-59210L);
		 System.out.println(Math.pow(2, -0.00288*(60874L-59210L)));
		 System.out.println( (Math.pow(2, -(0.00288 * (1000000000 - 4433 + 2609))) - 1)
					/ (Math.pow(2, -(0.00288 * (2609))) - 1));

		int dim1 = 3;
		int dim2 = 4;
		double[][] ps = new double[dim1][dim2];
		double[] a = { 2, 6, 7, 4 };
		ps[0] = a;
		double[] b = { 3, 8, 5, 2 };
		ps[1] = b;
		double[] c = { 6, 6, 8, 7 };
		ps[2] = c;
		double[] x0 = new double[dim2];
		double result = 0;
		for (int j = 0; j < dim2; j++) {
			for (int i = 0; i < dim1; i++) {
				x0[j] += ps[i][j];
			}
			x0[j] = x0[j] / dim1;
			for (int i = 0; i < dim1; i++) {
				result = result + (ps[i][j] - x0[j]) * (ps[i][j] - x0[j]);
			}
		}
//		System.out.println(Math.sqrt(result / dim1));
		double[] cf1 = new double[dim2];
		double[] cf2 = new double[dim2];
		for (int j = 0; j < dim2; j++) {
			for (int i = 0; i < dim1; i++) {
				cf1[j] += ps[i][j];
				cf2[j] = cf2[j] + ps[i][j] * ps[i][j];
			}
		}
		result = 0;
		double result2 = 0;
		for (int j = 0; j < dim2; j++) {
			result = result + (cf2[j] - (cf1[j] * cf1[j])/dim1);
//			result += cf2[j];
//			result2 = result2 + cf1[j] * cf1[j];
		}
//		result2 = result2 / dim1;
//		result = result - result2;
//		System.out.println(Math.sqrt(result / dim1));

	}
}
