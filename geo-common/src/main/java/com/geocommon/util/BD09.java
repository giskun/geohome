package com.geocommon.util;

public class BD09 {
	final static double PI = Math.PI;
	final static double X_PI = PI * 3000 / 180;

	static double[] toGcj02(double[] input, double[] output, int offset) {
		double x = input[offset] - 0.0065;
		double y = input[offset + 1] - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
		output[offset] = z * Math.cos(theta);
		output[offset + 1] = z * Math.sin(theta);
		return output;
	}

	public static double[] toGCJ02(double[] input, double[] opt_output, int opt_dimension) {
		int len = input.length;
		int dimension = opt_dimension > 0 ? opt_dimension : 2;
		double[] output = null;
		if (opt_output == null) {
			output = opt_output;
		} else {
			if (dimension != 2) {
				System.arraycopy(input, 0, output, 0, len);
			} else {
				output = new double[len];
			}
		}
		for (int offset = 0; offset < len; offset += dimension) {
			output = toGcj02(input, output, offset);
		}
		return output;
	}

	static double[] fromGcj02(double[] input, double[] output, int offset) {
		double x = input[offset];
		double y = input[offset + 1];
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * X_PI);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * X_PI);
		output[offset] = z * Math.cos(theta) + 0.0065;
		output[offset + 1] = z * Math.sin(theta) + 0.006;
		return output;
	}

	public static double[] fromGCJ02(double[] input, double[] opt_output, int opt_dimension) {
		int len = input.length;
		int dimension = opt_dimension > 0 ? opt_dimension : 2;
		double[] output = null;
		if (opt_output == null) {
			output = opt_output;
		} else {
			if (dimension != 2) {
				System.arraycopy(input, 0, output, 0, len);
			} else {
				output = new double[len];
			}
		}
		for (int offset = 0; offset < len; offset += dimension) {
			output = fromGcj02(input, output, offset);
		}
		return output;
	}

	public static double[] toWGS84(double[] bdll) {
		return toWGS84(bdll, new double[2], 2);
	}

	public static double[] toWGS84(double[] input, double[] opt_output, int opt_dimension) {
		double[] output = toGCJ02(input, opt_output, opt_dimension);
		return GCJ02.toWGS84(output, output, opt_dimension);
	}

	public static double[] fromWGS84(double[] input, double[] opt_output, int opt_dimension) {
		double[] output = GCJ02.fromWGS84(input, opt_output, opt_dimension);
		return fromGCJ02(output, output, opt_dimension);
	}

	public static double[] fromWGS84(double[] ds) {
		return fromWGS84(ds, new double[2], 2);
	}

}
