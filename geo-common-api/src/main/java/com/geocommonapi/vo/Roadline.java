package com.geocommonapi.vo;


import com.hdsx.measure.jts.MWKTReader;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 路径实体类
 * @author jingzh
 *
 */
@ApiModel(value = "路径表")
public class Roadline implements Serializable{

	/**
	 * 唯一标识
	 */
	private String uuid;

	@ApiModelProperty(value = "路线标识")
	private String guid;

	@ApiModelProperty(value = "路线代码")
	private String roadcode;

	@ApiModelProperty(value = "路线名称")
	private String roadname;

	@ApiModelProperty(value = "起点位置")
	private double startmile;

	@ApiModelProperty(value = "止点位置")
	private double endmile;

	@ApiModelProperty(value = "上下行方向")
	private String sxxfx=null;

	@ApiModelProperty(value = "空间数据")
	private Geometry shape;

	@ApiModelProperty(value = "wkt形式")
	private String geom;

	@ApiModelProperty(value = "路线类型（0，高速；1，普通国道；2，普通省道）")
	private int lxlx;

	@ApiModelProperty(value = "包含切片")
	private List<Tile> tiles;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getRoadcode() {
		return roadcode;
	}

	public void setRoadcode(String roadcode) {
		this.roadcode = roadcode;
	}

	public String getRoadname() {
		return roadname;
	}

	public void setRoadname(String roadname) {
		this.roadname = roadname;
	}

	public double getStartmile() {
		return startmile;
	}

	public void setStartmile(double startmile) {
		this.startmile = startmile;
	}

	public double getEndmile() {
		return endmile;
	}

	public void setEndmile(double endmile) {
		this.endmile = endmile;
	}

	public String getSxxfx() {
		return sxxfx;
	}

	public void setSxxfx(String sxxfx) {
		this.sxxfx = sxxfx;
	}

	public Geometry getShape() {
		return shape;
	}

	public void setShape(Geometry shape) {
		this.shape = shape;
	}

	public Roadline() {
	}

	public List<Tile> getTiles() {
		return tiles;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setTiles(List<Tile> tiles) {
		this.tiles = tiles;
	}

	public String getGeom() {
		return geom;
	}

	public void setGeom(String geom) {
		this.geom = geom;
		MWKTReader mwktReader = new MWKTReader();
		try {
			setShape(mwktReader.read(geom));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public Roadline(String guid, String roadcode, String roadname, double startmile, double endmile, String sxxfx, Geometry shape) {
		this.guid = guid;
		this.roadcode = roadcode;
		this.roadname = roadname;
		this.startmile = startmile;
		this.endmile = endmile;
		this.sxxfx = sxxfx;
		this.shape = shape;
	}

	@Override
	public String toString() {
		return "RoadLine [roadcode=" + roadcode + ", roadname=" + roadname + ", startmile=" + startmile + ", endmile="
				+ endmile + "]";
	}

	public int getLxlx() {
		return lxlx;
	}

	public void setLxlx(int lxlx) {
		this.lxlx = lxlx;
	}
}
