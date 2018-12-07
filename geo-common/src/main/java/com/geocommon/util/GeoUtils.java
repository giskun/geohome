package com.geocommon.util;

public class GeoUtils {
    public enum GaussSphere{
        Beijing54,
        Xian80,
        WGS84,
    }
    private static double Rad(double d){
        return d * Math.PI / 180.0;
    }
    public double DistanceOfTwoPoints(double lng1,double lat1,double lng2,double lat2,
                                      GaussSphere gs){
        double radLat1 = Rad(lat1);
        double radLat2 = Rad(lat2);
        double a = radLat1 - radLat2;
        double b = Rad(lng1) - Rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b/2),2)));
        s = s * (gs == GaussSphere.WGS84 ? 6378137.0 : (gs == GaussSphere.Xian80 ? 6378140.0 : 6378245.0));
        s = Math.round(s * 10000) / 10000;
        return s;
    }
}
