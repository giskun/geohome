package com.geocommon.index;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.index.strtree.STRtree;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;


import java.util.*;

/**
 * @Description:空间索引STRtree
 * @Author <a href="mailto:meijinbo@qq.com">Kimbo</a>
 * @Date 2017年09月05日 16:21
 **/
public class STRtreeIndex {
    public static Geometry querygeometry;
    private static STRtree strTree = new STRtree();

    /**
     * 构造索引
     * @param iterator
     */
    public static void buildStrTree(FeatureIterator<SimpleFeature> iterator){
        try{
            long time1 = System.currentTimeMillis();
            int j=0;
            while(iterator.hasNext()){
                j++;
                SimpleFeature simpleFeature = iterator.next();
                Geometry geometry = (Geometry) simpleFeature.getDefaultGeometry();
                if(querygeometry==null)
                    querygeometry = geometry;
                strTree.insert(geometry.getEnvelopeInternal(),simpleFeature);
            }

            long time2 = System.currentTimeMillis();
            System.out.println(time2-time1);
            System.out.println("深度:"+strTree.depth()+";节点:"+strTree.size());
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    public static List<SimpleFeature> query(final Geometry spatialFilter){
        Envelope enve=spatialFilter.getEnvelopeInternal();
        long time1 = System.currentTimeMillis();
        List<SimpleFeature> results = strTree.query(enve);
        List<SimpleFeature> temp = new ArrayList<SimpleFeature>();
        for(SimpleFeature simpleFeature:results){
            Geometry geometry = (Geometry) simpleFeature.getDefaultGeometry();
            if(spatialFilter.intersects(geometry)||spatialFilter.contains(geometry)){
                temp.add(simpleFeature);
            }
        }
        Collections.sort(temp, new Comparator<SimpleFeature>() {
            @Override
            public int compare(SimpleFeature o1, SimpleFeature o2) {
                double d1=spatialFilter.distance((Geometry)o1.getDefaultGeometry());
                double d2=spatialFilter.distance((Geometry)o2.getDefaultGeometry());
                if(d1==d2){
                    return 0;
                }else if(d1>d2){
                    return 1;
                }else{
                    return -1;
                }
            }
        });
        long time2 = System.currentTimeMillis();
        System.out.println(time2-time1);
        return temp;
    }
}
