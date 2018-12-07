package com.geocommon.util;

/**
 * Created by jzh on 2018/1/22.
 */
public class GpsCoordUtil {
    private static double PI=3.14159265358979324;
    private static double x_PI  = 3.14159265358979324 * 3000.0 / 180.0;
    private static double a = 6378245.0;
    private static double ee = 0.00669342162296594323;
    private static double transformLat(double x,double y){
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }
    private static double transformLon(double x,double y){
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }
    private static boolean outOfChina(double lon,double lat){
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }
    /**
     * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换
     * 即 百度 转 谷歌、高德
     * @param bd_lon
     * @param bd_lat
     * @return
     */
    public static double[] bd09togcj02(double bd_lon, double bd_lat){
        double x = bd_lon - 0.0065;
        double y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_PI );
        double gg_lng = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new double[]{gg_lng, gg_lat};
    }
    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
     * 即谷歌、高德 转 百度
     * @param lng
     * @param lat
     * @return
     */
    public static  double[] gcj02tobd09(double lng, double lat) {
        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * x_PI);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * x_PI);
        double bd_lng = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new double[]{bd_lng, bd_lat};
    }
    /**
     * WGS84转GCj02
     * @param lng
     * @param lat
     * @return
     */
    public static double[] wgs84togcj02(double lng, double lat){
        double dlat = transformLat(lng - 105.0, lat - 35.0);
        double dlng = transformLon(lng - 105.0, lat - 35.0);
        double radlat = lat / 180.0 * PI;
        double magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
        double mglat = lat + dlat;
        double mglng = lng + dlng;
        return new double[]{mglng, mglat};
    }
    /**
     *  GCJ02 转换为 WGS84
     * @param lng
     * @param lat
     * @return
     */
    public static double[] gcj02towgs84(double lng,double lat){
        double dlat = transformLat(lng - 105.0, lat - 35.0);
        double dlng = transformLon(lng - 105.0, lat - 35.0);
        double radlat = lat / 180.0 * PI;
        double magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
        double mglat = lat + dlat;
        double mglng = lng + dlng;
        return new double[]{lng * 2 - mglng, lat * 2 - mglat};
    }
    /**
     * WGS-84 to BD-09
     * @param wgsLon
     * @param wgsLat
     * @return
     */
    public static double[] wgstobd_encrypt(double wgsLon,double wgsLat){
        double[] gcj = wgs84togcj02(wgsLon,wgsLat);
        double[] bdLonlat =  gcj02tobd09(gcj[0],gcj[1]);
        double[] mectorbd = lonLat2Mercator(bdLonlat[0],bdLonlat[1]);
        return mectorbd;
    }
    public static double[] bdTowgs_encrypt(double Lon,double Lat){
        double[] gcj = bd09togcj02(Lon,Lat);
        double[] wgslonlat =  gcj02towgs84(gcj[0],gcj[1]);
        return wgslonlat;
    }
    public static double[] gcj02mectortowgs84(double gcj02mectorX,double gcj02mectorY){
        double[] gcj = Mercator2lonLat(gcj02mectorX,gcj02mectorY);
        double[] wgslonlat = gcj02towgs84(gcj[0],gcj[1]);
        return wgslonlat;
    }
    /**
     * 经纬度转墨卡托
     * @param lontitude
     * @param latitude
     * @return
     */
    public static double[] lonLat2Mercator(double lontitude,double latitude){
        double x = lontitude * 20037508.34 / 180;
        double y = Math.log(Math.tan((90 + latitude) * Math.PI / 360)) / (Math.PI / 180);
        y = y * 20037508.34 / 180;
        return new double[]{x,y};
    }
    /**
     * 墨卡托转经纬度
     * @param x
     * @param y
     * @return
     */
    public static double[] Mercator2lonLat(double x,double y){
        double longtitude = x / 20037508.34 * 180;
        double latitude = y / 20037508.34 * 180;
        latitude = 180 / Math.PI * (2 * Math.atan(Math.exp(latitude * Math.PI / 180)) - Math.PI / 2);
        return new double[]{longtitude,latitude};
    }

    public static void main(String[] args) {
        double[] input = {12671265.48,4110547.93};
        double[] output = new double[2];
        BD09.toGcj02(input,output,0);
        System.out.println(output[0]+"|"+output[1]);
        double[] wgs84 = new double[2];
        double[] ss= GCJ02.toWGS84(output,wgs84,0);
        System.out.println(wgs84[0]+"|"+wgs84[1]);
        double[] pt = Mercator2lonLat(output[0],output[1]);
        //double[] pt =   {113.736962,34.77871};
        double[] wgs = bdTowgs_encrypt(pt[0],pt[1]);
        System.out.println(wgs[0]+"|"+wgs[1]);
    }
}
