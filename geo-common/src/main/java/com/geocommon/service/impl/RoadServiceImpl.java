package com.geocommon.service.impl;

import com.geocommon.index.STRtreeIndex;
import com.geocommon.service.RoadService;

import com.geocommon.util.GpsCoordUtil;
import com.geocommon.vo.Roadline;
import com.hdsx.measure.jts.MWKTReader;
import com.hdsx.measure.jts.mgeom.*;
import com.vividsolutions.jts.geom.*;

import org.geotools.data.*;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author zhangfk 【giskun0915@126.com】
 * @Date 2018/10/28 14:37
 * @desc 路线服务
 */
@Service
public class RoadServiceImpl implements RoadService {

    @PostConstruct
    public void initRoad(){
        Gcj02mectortowgs("D:\\zhangfk\\prj\\weather\\","luduan","luduanprj");
        //readRoad();
        //List<SimpleFeature> list = STRtreeIndex.query(STRtreeIndex.querygeometry);
        //System.out.println(list.size());
        //LinearReference();
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

    @Override
    public boolean LinearReference() {
        String filepath = "D:\\zhangfk\\shape\\jx\\TRAFFIC_NETWORK_WGS.shp";
        ShapefileDataStore shpDataStore = null;
        try {
            ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
            shpDataStore = new ShapefileDataStore(new File(filepath).toURI().toURL());
            shpDataStore.setCharset(Charset.forName("GBK"));
            String typeName = shpDataStore.getTypeNames()[0];
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = (FeatureSource<SimpleFeatureType, SimpleFeature>) shpDataStore.getFeatureSource(typeName);
            FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures();
            FeatureIterator<SimpleFeature> iterator = result.features();
            while(iterator.hasNext()){
                SimpleFeature simpleFeature = iterator.next();
                Geometry geometry = (Geometry) simpleFeature.getDefaultGeometry();
                Collection<Property> p = simpleFeature.getProperties();
                Iterator<Property> it = p.iterator();
                Roadline item = new Roadline();
                Point beforePoint = null;
                while(it.hasNext()){
                    Property pro = it.next();
                    switch(pro.getName().toString().toLowerCase()){
                        case "guid":
                            item.setGuid(pro.getValue().toString());
                            break;
                        case "qdzh":
                            item.setStartmile(pro.getValue()!=null?Double.parseDouble(pro.getValue().toString()):0);
                            break;
                        case "zdzh":
                            item.setEndmile(pro.getValue()!=null?Double.parseDouble(pro.getValue().toString()):0);
                            break;
                        case "sxxfx":
                            item.setSxxfx(pro.getValue().toString());
                            break;
                        case "lxbm":
                            item.setRoadcode(pro.getValue().toString());
                            break;
                        case "lxmc":
                            item.setRoadname(pro.getValue().toString());
                            break;
                        case "lxlx":
                            item.setLxlx(Integer.parseInt(pro.getValue().toString()));
                            break;
                        case "id":
                            item.setUuid(pro.getValue().toString());
                            break;
                    }
                }
                if(geometry!=null){
                    double length = geometry.getLength();
                    MWKTReader mwktReader = new MWKTReader();
                    double zhrange = Math.abs(item.getStartmile()-item.getEndmile());
                    double factor = zhrange/length;
                    if(item.getStartmile()>item.getEndmile())
                        factor = factor*(-1);
                    MultiMLineString lineString = null;
                    String wkts = geometry.toString();
                    int ll = wkts.indexOf("((");
                    int end = wkts.indexOf("))");
                    wkts = wkts.substring(ll+2,end);
                    MGeometryFactory mGeometryFactory = new MGeometryFactory();
                    if(wkts.indexOf("), (")<0){//单线
                        MLineString mLineString= createMLineString(wkts,item.getStartmile(),factor);
                        lineString=mGeometryFactory.createMultiMLineString(new MLineString[]{mLineString});
                    }else{//双线
                        String[] wktArr = wkts.split("\\), \\(");
                        MLineString[] multiMLineString = new MLineString[wktArr.length];
                        double startQdzh = item.getStartmile();
                        for(int j=0;j<wktArr.length;j++){
                            MLineString mLineString = createMLineString(wktArr[j],startQdzh,factor);
                            multiMLineString[j] = mLineString;
                            if(factor<0)
                                startQdzh = mLineString.getMinM();
                            else{
                                startQdzh = mLineString.getMaxM();
                            }
                        }
                        lineString=mGeometryFactory.createMultiMLineString(multiMLineString);
                    }
                    item.setShape(lineString);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (shpDataStore!=null)
                shpDataStore.dispose();
        }
        return false;
    }


    @Override
    public boolean Gcj02mectortowgs(String filePath, String fileName, String projectName) {
        String filepath = filePath+fileName+".shp";
        ShapefileDataStore shpDataStore = null;
        String shppath = filePath+projectName+".shp";
        try{

            File shapeFile = new File(filepath);
            ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
            shpDataStore = (ShapefileDataStore)dataStoreFactory.createDataStore(new File(filepath).toURI().toURL());
            shpDataStore.setCharset(Charset.forName("GBK"));
            List<AttributeDescriptor> attrList = shpDataStore.getFeatureSource().getSchema()
                    .getAttributeDescriptors();
            String typeName = shpDataStore.getTypeNames()[0];
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = (FeatureSource<SimpleFeatureType, SimpleFeature>) shpDataStore.getFeatureSource(typeName);
            FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures();
            FeatureIterator<SimpleFeature> iterator = result.features();
            //创建shape
            //创建shape文件对象
            File file = new File(shppath);
            Map<String, Serializable> paramss = new HashMap<String, Serializable>();
            paramss.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
            ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(paramss);
            //定义图形信息和属性信息
            SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
            tb.setName("shapefile");
            tb.addAll(attrList);
            ds.createSchema(tb.buildFeatureType());
            ds.setCharset(Charset.forName("GBK"));
            //设置Writer
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0], Transaction.AUTO_COMMIT);
            //没有空间信息
            List<String> geomnullist = new ArrayList<>();
            int i=0;
            GeometryFactory geometryFactory = new GeometryFactory();
            while(iterator.hasNext()) {
                MultiLineString multilineString = null;
                SimpleFeature simpleFeature = iterator.next();
                Geometry geometry = (Geometry) simpleFeature.getDefaultGeometry();
                if(geometry==null)
                    continue;
                String wkts = geometry.toString();
                if(StringUtils.isEmpty(wkts))
                    continue;
                int ll = wkts.indexOf("((");
                int end = wkts.indexOf("))");
                if(ll<0)
                    continue;
                SimpleFeature savefeature = writer.next();
                savefeature.setAttributes(simpleFeature.getAttributes());
                wkts = wkts.substring(ll+2,end);
                if(wkts.indexOf("), (")<0){//单线
                    LineString lineString = createLineString(wkts);
                    multilineString=geometryFactory.createMultiLineString(new LineString[]{lineString});
                }else{//双线
                    String[] wktArr = wkts.split("\\), \\(");
                    LineString[] multiLineString = new LineString[wktArr.length];
                    for(int j=0;j<wktArr.length;j++){
                        LineString lineString = createLineString(wktArr[j]);
                        multiLineString[j] = lineString;
                    }
                    multilineString=geometryFactory.createMultiLineString(multiLineString);
                }
                if(multilineString!=null)
                    savefeature.setDefaultGeometry(multilineString);
                i++;
            }
            System.out.println("匹配空间数据的有"+i+"条");
            writer.write();
            writer.close();
            ds.dispose();
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (shpDataStore!=null)
                shpDataStore.dispose();
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        return false;
    }

    @Override
    public boolean wgs84togcj02(String filePath, String fileName, String projectName) {
        return false;
    }

    private LineString createLineString(String wkts){
        GeometryFactory geometryFactory = new GeometryFactory();
        String[] coordArr = wkts.split(", ");
        Coordinate[] coordinates = new Coordinate[coordArr.length];
        for(int i=0;i<coordArr.length;i++) {
            String coord = coordArr[i];
            String[] xy = coord.split(" ");
            double[] wgsLonlat = GpsCoordUtil.gcj02mectortowgs84(Double.parseDouble(xy[0]),Double.parseDouble(xy[1]));
            coordinates[i] = new Coordinate(wgsLonlat[0],wgsLonlat[1]);
        }
        return geometryFactory.createLineString(coordinates);
    }

    public MLineString createMLineString(String wkts,double qdzh,double factor){
        MGeometryFactory mGeometryFactory = new MGeometryFactory();
        Point beforePoint = null;
        GeometryFactory geometryFactory = new GeometryFactory();
        double m = qdzh;
        String[] coordArr = wkts.split(", ");
        MCoordinate[] mCoordinates = new MCoordinate[coordArr.length];
        for(int i=0;i<coordArr.length;i++){
            String coord = coordArr[i];
            String[] lonlat = coord.split(" ");
            MCoordinate mCoordinate = null;
            double   longtitude   =  Double.parseDouble(lonlat[0]);
            double   latitude   =   Double.parseDouble(lonlat[1]);
            if(beforePoint==null){
                mCoordinates[i] = new MCoordinate(longtitude,latitude,0,qdzh);
            }else{
                Point point = geometryFactory.createPoint(new Coordinate(longtitude,latitude));
                double distance = point.distance(beforePoint);
                m += distance*factor;
                mCoordinates[i] = new MCoordinate(longtitude,latitude,0,m);

            }
            beforePoint = geometryFactory.createPoint(new Coordinate(longtitude,latitude));
        }
        return mGeometryFactory.createMLineString(mCoordinates);
    }
}
