package com.geocommon.util;

public class GCJ02 {

	// var PI = Math.PI;
	// var AXIS = 6378245.0;
	// var OFFSET = 0.00669342162296594323; // (a^2 - b^2) / a^2

	final static double PI = Math.PI;
	final static double AXIS = 6378245.0;
	final static double OFFSET = 0.00669342162296594323; // (a^2 - b^2) / a^2

	static double[] delta(double wgLon, double wgLat) {
		double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
		double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
		double radLat = wgLat / 180.0 * PI;
		double magic = Math.sin(radLat);
		magic = 1 - OFFSET * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((AXIS * (1 - OFFSET)) / (magic * sqrtMagic) * PI);
		dLon = (dLon * 180.0) / (AXIS / sqrtMagic * Math.cos(radLat) * PI);
		return new double[] { dLon, dLat };
	}

	static boolean outOfChina(double lon, double lat) {
		if (lon < 72.004 || lon > 137.8347) {
			return true;
		}
		if (lat < 0.8293 || lat > 55.8271) {
			return true;
		}
		return false;
	}

	static double transformLat(double x, double y) {
		double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
		return ret;
	}

	static double transformLon(double x, double y) {
		double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
		return ret;
	}

	static void toWgs84(double[] input, double[] output, int offset) {
		double lng = input[offset];
		double lat = input[offset + 1];
		if (!outOfChina(lng, lat)) {
			double[] deltaD = delta(lng, lat);
			lng = lng - deltaD[0];
			lat = lat - deltaD[1];
		}
		output[offset] = lng;
		output[offset + 1] = lat;
	}

	public static double[] toWGS84(double[] input, double[] opt_output, int opt_dimension) {
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
			toWgs84(input, output, offset);
		}
		return output;
	}

	static void fromwsg84(double[] input, double[] output, int offset) {
		double lng = input[offset];
		double lat = input[offset + 1];
		if (!outOfChina(lng, lat)) {
			double[] deltaD = delta(lng, lat);
			lng = lng + deltaD[0];
			lat = lat + deltaD[1];
		}
		output[offset] = lng;
		output[offset + 1] = lat;
	}
	
	public static double[] fromWGS84(double[] input, double[] opt_output, int opt_dimension) {
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
			fromwsg84(input, output, offset);
		}
		return output;
	}
}
