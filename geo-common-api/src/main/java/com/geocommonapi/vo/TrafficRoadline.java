package com.geocommonapi.vo;

import com.vividsolutions.jts.geom.Geometry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jzh on 2017/10/7.
 */
@ApiModel(value = "路径表")
public class TrafficRoadline implements Serializable {
    /**
     * 编码
     */
    @ApiModelProperty(value = "唯一标识")
    protected String id;

    @ApiModelProperty(value = "路线唯一标识")
    protected String uuid;
    /**
     * 编码
     */
    @ApiModelProperty(value = "路线标识")
    protected String guid;
    /**
     * 路线编码
     */
    @ApiModelProperty(value = "路线编码")
    protected String lxbm;
    /**
     * 路线名称
     */
    @ApiModelProperty(value = "路线名称")
    protected String lxmc;

    /**
     * 起点桩号
     */
    @ApiModelProperty(value = "起点桩号")
    protected double qdzh;

    /**
     * 止点桩号
     */
    @ApiModelProperty(value = "止点桩号")
    protected double zdzh;
    /**
     * 上下行方向
     */
    @ApiModelProperty(value = "上下行方向")
    protected int sxxfx;
    /**
     * 状态
     * 1：畅通 2：缓行 3：拥堵
     */
    @ApiModelProperty(value = "拥堵状态")
    protected int status;
    /**
     * 数据时间
     */
    @ApiModelProperty(value = "数据时间")
    protected Date sjsj;
    
    /**
     * 中心坐标
     */
    @ApiModelProperty(value = "中心坐标")
    protected double ptx;

    /**
     * 中心坐标
     */
    @ApiModelProperty(value = "中心坐标")
    protected double pty;

    /**
     * 路段类型
     */
    @ApiModelProperty(value = "路段类型")
    protected int ldlx;

    /**
     * 拥堵里程
     */
    @ApiModelProperty(value = "拥堵里程")
    protected double len;
    
    @ApiModelProperty(value = "空间数据")
    protected Geometry shape;
    
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLxbm() {
        return lxbm;
    }

    public void setLxbm(String lxbm) {
        this.lxbm = lxbm;
    }

    public String getLxmc() {
        return lxmc;
    }

    public void setLxmc(String lxmc) {
        this.lxmc = lxmc;
    }

    public double getQdzh() {
        return qdzh;
    }

    public void setQdzh(double qdzh) {
        this.qdzh = qdzh;
    }

    public double getZdzh() {
        return zdzh;
    }

    public void setZdzh(double zdzh) {
        this.zdzh = zdzh;
    }

    public int getSxxfx() {
        return sxxfx;
    }

    public void setSxxfx(int sxxfx) {
        this.sxxfx = sxxfx;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getSjsj() {
        return sjsj;
    }

    public void setSjsj(Date sjsj) {
        this.sjsj = sjsj;
    }

    public Geometry getShape() {
        return shape;
    }

    public void setShape(Geometry shape) {
        this.shape = shape;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPtx() {
        return ptx;
    }

    public void setPtx(double ptx) {
        this.ptx = ptx;
    }

    public double getPty() {
        return pty;
    }

    public void setPty(double pty) {
        this.pty = pty;
    }

    public int getLdlx() {
        return ldlx;
    }

    public void setLdlx(int ldlx) {
        this.ldlx = ldlx;
    }

    public double getLen() {
        return len;
    }

    public void setLen(double len) {
        this.len = len;
    }

    @Override
    public String toString() {
        return "TrafficRoadline{" +
                "id='" + id + '\'' +
                ", guid='" + guid + '\'' +
                ", lxbm='" + lxbm + '\'' +
                ", lxmc='" + lxmc + '\'' +
                ", qdzh=" + qdzh +
                ", zdzh=" + zdzh +
                ", sxxfx=" + sxxfx +
                ", status=" + status +
                ", sjsj=" + sjsj +
                ", ptx=" + ptx +
                ", pty=" + pty +
                ", ldlx=" + ldlx +
                ", shape=" + shape +
                '}';
    }
}
