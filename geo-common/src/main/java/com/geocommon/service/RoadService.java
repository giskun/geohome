package com.geocommon.service;

/**
 * @author zhangfk 【giskun0915@126.com】
 * @Date 2018/10/28 12:10
 * @desc 路线相关操作服务
 */
public interface RoadService {
    boolean readRoad();

    /**
     * 线性参照服务
     * @return
     */
    boolean LinearReference();

    /**
     * 国测局02墨卡托投影成84坐标
     * @param filePath
     * @param fileName
     * @param projectName
     * @return
     */
    boolean Gcj02mectortowgs(String filePath,String fileName,String projectName);

    /**
     * wgs84转换成gcj02
     * @param filePath
     * @param fileName
     * @param projectName
     * @return
     */
    boolean wgs84togcj02(String filePath,String fileName,String projectName);
}
