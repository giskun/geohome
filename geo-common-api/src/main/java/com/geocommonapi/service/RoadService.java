package com.geocommonapi.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author zhangfk 【giskun0915@126.com】
 * @Date 2018/10/28 12:10
 * @desc 路线相关操作服务
 */
@RequestMapping("roadService")
public interface RoadService {
    //boolean readRoad();


    /**
     * 国测局02墨卡托投影成84坐标
     * @param filePath
     * @param fileName
     * @param projectName
     * @return
     */
    //boolean Gcj02mectortowgs(String filePath, String fileName, String projectName);

    /**
     * wgs84转换成gcj02
     * @param filePath
     * @param fileName
     * @param projectName
     * @return
     */
    //boolean wgs84togcj02(String filePath, String fileName, String projectName);

    /**
     * 根据桩号获取经纬度
     * @param lxbm
     * @param sxxfx
     * @param m
     * @return
     */
    @GetMapping("/getCoordinateByPos")
    Coordinate getCoordinateByPos(@RequestParam("lxbm") String lxbm,@RequestParam("sxxfx") int sxxfx,@RequestParam("m") double m);

    /**
     * 根据桩号范围获取路段路段坐标信息
     * @param lxbm
     * @param sxxfx
     * @param startM
     * @param endM
     * @return
     */
    @GetMapping("/getCoordinatesBetweenMRange")
    List<CoordinateSequence[]> getCoordinatesBetweenMRange(@RequestParam("lxbm") String lxbm,@RequestParam("sxxfx") int sxxfx,@RequestParam("startM") double startM,@RequestParam("endM") double endM);
}
