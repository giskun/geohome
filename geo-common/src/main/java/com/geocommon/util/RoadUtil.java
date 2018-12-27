package com.geocommon.util;

import com.geocommonapi.vo.Roadline;
import com.hdsx.measure.jts.mgeom.MCoordinate;
import com.hdsx.measure.jts.mgeom.MGeometry;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoadUtil {

    public static Map<String,List<Roadline>> roadMap;

   public static void addRoadLine(Roadline item){
       if(item==null)
           return;
       if(roadMap==null)
           roadMap = new HashMap<>();
       if(roadMap.get(item.getGuid())==null)
           roadMap.put(item.getGuid(),new ArrayList<Roadline>());
       roadMap.get(item.getGuid()).add(item);
   }

    /**
     * 根据路线编码、桩号获取位置信息
     * @param lxbm
     * @param sxxfx
     * @param m
     * @return
     */
   public static Coordinate getCoordinateByPos(String lxbm, int sxxfx, double m){
       Coordinate coordinate = null;
       try{
           String guid = lxbm+"_"+sxxfx;
           if(roadMap==null)
               return null;
           List<Roadline> list = roadMap.get(guid);
           if(list!=null && list.size()>0){
               for(Roadline item:list){
                   coordinate = ((MGeometry)item.getShape()).getCoordinateAtM(m);
                   break;
               }
           }
       }catch(Exception e){
           e.printStackTrace();
       }
       return coordinate;
   }

    /**
     * 根据路线编码、上下行方向、桩号范围获取路段
     * @param lxbm
     * @param sxxfx
     * @param startM
     * @param endM
     * @return
     */
   public static List<CoordinateSequence[]> getCoordinatesBetweenMRange(String lxbm,int sxxfx,double startM,double endM){
       List<CoordinateSequence[]> list = null;
       try{
           String guid = lxbm+"_"+sxxfx;
           if(roadMap==null)
               return null;
           List<Roadline> roads = roadMap.get(guid);
           if(roads!=null && roads.size()>0){
               for(Roadline item:roads){
                   CoordinateSequence[] coordinates = ((MGeometry)item.getShape()).getCoordinatesBetween(startM,endM);
                   if(coordinates!=null)
                       if(list==null)
                           list = new ArrayList<>();
                   list.add(coordinates);
               }
           }
       }catch(Exception e){
           e.printStackTrace();
       }
       return list;
   }


}
