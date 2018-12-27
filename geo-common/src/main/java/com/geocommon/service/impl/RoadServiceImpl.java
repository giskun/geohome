package com.geocommon.service.impl;

import com.geocommon.index.STRtreeIndex;

import com.geocommon.util.GpsCoordUtil;
import com.geocommon.util.RoadUtil;
import com.geocommonapi.service.RoadService;
import com.geocommonapi.vo.Roadline;
import com.hdsx.measure.jts.mgeom.*;
import com.vividsolutions.jts.geom.*;

import org.geotools.data.*;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author zhangfk 【giskun0915@126.com】
 * @Date 2018/10/28 14:37
 * @desc 路线服务
 */
@Service
@RestController
public class RoadServiceImpl implements RoadService {

    @PostConstruct
    public void initRoad(){
        //Gcj02mectortowgs("D:\\zhangfk\\prj\\weather\\","luduan","luduanprj");
        //wgs84togcj02("D:\\zhangfk\\prj\\road\\","ROAD_WGS84_M","ROAD02");
        //readRoad();
        //List<SimpleFeature> list = STRtreeIndex.query(STRtreeIndex.querygeometry);
        //System.out.println(list.size());
        LinearReference();
    }

    ///@Override
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

    //@Override
    public boolean LinearReference() {
        String filepath = "D:\\zhangfk\\shape\\NM\\wgs\\GPSLW_M.shp";
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
                        /*case "id":
                            item.setUuid(pro.getValue().toString());
                            break;*/
                    }
                }
                if(geometry!=null){
                    item.setShape(geometry);
                }
                RoadUtil.addRoadLine(item);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (shpDataStore!=null)
                shpDataStore.dispose();
        }
        return false;
    }


    //@Override
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
            MGeometry mGeometry = null;
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
        return false;
    }

    //@Override
    public boolean wgs84togcj02(String filePath, String fileName, String projectName) {
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
            MGeometryFactory mgeometryFactory = new MGeometryFactory();
            while(iterator.hasNext()) {
                SimpleFeature simpleFeature = iterator.next();
                MGeometry geometry = (MGeometry) simpleFeature.getDefaultGeometry();
                if(geometry==null)
                    continue;
                String wkts = geometry.toString();
                if(StringUtils.isEmpty(wkts))
                    continue;
                int ll = wkts.indexOf("((");
                int end = wkts.indexOf("))");
                if(ll<0)
                    continue;
                wkts = wkts.substring(ll+2,end);
                if(wkts.indexOf("), (")<0){//单线
                    createFeature(wkts,writer,simpleFeature);
                }else{//双线
                    String[] wktArr = wkts.split("\\), \\(");
                    for(int j=0;j<wktArr.length;j++){
                        createFeature(wktArr[j],writer,simpleFeature);
                    }
                }
            }
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
        return false;
    }

    @Override
    public Coordinate getCoordinateByPos(@RequestParam String lxbm, @RequestParam int sxxfx, @RequestParam double m) {
        return RoadUtil.getCoordinateByPos(lxbm,sxxfx,m);
    }

    @Override
    public List<CoordinateSequence[]> getCoordinatesBetweenMRange(@RequestParam String lxbm, @RequestParam int sxxfx, @RequestParam double startM, @RequestParam double endM) {
        return RoadUtil.getCoordinatesBetweenMRange(lxbm,sxxfx,startM,endM);
    }

    private boolean createFeature(String wkts,FeatureWriter<SimpleFeatureType, SimpleFeature> writer,SimpleFeature simpleFeature){
        try {
            GeometryFactory geometryFactory = new GeometryFactory();
            String[] coordArr = wkts.split(", ");
            for (int i = 0; i < coordArr.length - 1; i++) {
                SimpleFeature savefeature = writer.next();
                savefeature.setAttributes(simpleFeature.getAttributes());
                Coordinate[] coordinates = new Coordinate[2];
                String coord = coordArr[i];
                String[] xy = coord.split(" ");
                double[] gcj02lonlat = GpsCoordUtil.wgs84togcj02(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]));
                savefeature.setAttribute("QDZH",Double.parseDouble(xy[2]));
                coordinates[0] = new Coordinate(gcj02lonlat[0], gcj02lonlat[1]);
                String coord1 = coordArr[i + 1];
                String[] xy1 = coord1.split(" ");
                double[] gcj02lonlat1 = GpsCoordUtil.wgs84togcj02(Double.parseDouble(xy1[0]), Double.parseDouble(xy1[1]));
                coordinates[1] = new Coordinate(gcj02lonlat1[0], gcj02lonlat1[1]);
                savefeature.setAttribute("ZDZH",Double.parseDouble(xy1[2]));
                savefeature.setDefaultGeometry(geometryFactory.createLineString(coordinates));

            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
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

    private MLineString createMLineString(String wkts){
        MGeometryFactory mgeometryFactory = new MGeometryFactory();
        String[] coordArr = wkts.split(", ");
        MCoordinate[] coordinates = new MCoordinate[coordArr.length];
        for(int i=0;i<coordArr.length;i++) {
            String coord = coordArr[i];
            String[] xy = coord.split(" ");
            double[] gcj02lonlat = GpsCoordUtil.wgs84togcj02(Double.parseDouble(xy[0]),Double.parseDouble(xy[1]));
            MCoordinate mCoordinate = new MCoordinate(gcj02lonlat[0],gcj02lonlat[1],Double.parseDouble(xy[2]),Double.parseDouble(xy[2]));
            coordinates[i] = mCoordinate;
        }
        return mgeometryFactory.createMLineString(coordinates);
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
