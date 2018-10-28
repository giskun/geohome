package com.geocommon.service.impl;

import com.geocommon.index.STRtreeIndex;
import com.geocommon.service.RoadService;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author zhangfk 【giskun0915@126.com】
 * @Date 2018/10/28 14:37
 * @desc 路线服务
 */
@Service
public class RoadServiceImpl implements RoadService {

    @PostConstruct
    public void initRoad(){
        readRoad();
        List<SimpleFeature> list = STRtreeIndex.query(STRtreeIndex.querygeometry);
        System.out.println(list.size());
    }

    @Override
    public boolean readRoad() {
        String filepath = "D:\\zhangfk\\shape\\lwzx\\highway.shp";
        ShapefileDataStore shpDataStore = null;
        try {
            ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
            shpDataStore = new ShapefileDataStore(new File(filepath).toURI().toURL());
            shpDataStore.setCharset(Charset.forName("GBK"));
            String typeName = shpDataStore.getTypeNames()[0];
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = (FeatureSource<SimpleFeatureType, SimpleFeature>) shpDataStore.getFeatureSource(typeName);
            FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures();
            STRtreeIndex.buildStrTree(result.features());
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (shpDataStore!=null)
                shpDataStore.dispose();
        }
        return false;
    }
}
