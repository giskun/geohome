package com.geocommon.vo;

import com.vividsolutions.jts.geom.Envelope;

import java.io.Serializable;

public class Tile implements Serializable{

	private int x;
	private int y;
	private int z;

	private String id;

	/**
	 * wgs84
	 */
	private Envelope boundary;

	/**
	 * 其他坐标系
	 */
	private Envelope extBoundary;

	private double resulotion;

	private String url;

	private String filepath;

	public String getId() {
		return x + "_" + y + "_" + z;
	}

	public double getResulotion() {
		return resulotion;
	}

	public void setResulotion(double resulotion) {
		this.resulotion = resulotion;
	}

	public Envelope getBoundary() {
		return boundary;
	}

	public void setBoundary(Envelope boundary) {
		this.boundary = boundary;
	}

	public Envelope getExtBoundary() {
		return extBoundary;
	}

	public void setExtBoundary(Envelope extBoundary) {
		this.extBoundary = extBoundary;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	@Override
	public String toString() {
		return "Tile [坐标范围=" + boundary + ", resulotion=" + resulotion + ", x="
				+ x + ", y=" + y + ", z=" + z + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tile) {
			Tile t = (Tile) obj;
			if (t.getX() == this.getX() && t.getY() == this.getY() && t.getZ() == this.getZ()
					&& t.getResulotion() == this.getResulotion())
				return true;
		}
		return false;
	}

}
