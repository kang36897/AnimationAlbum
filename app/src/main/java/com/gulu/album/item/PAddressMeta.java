package com.gulu.album.item;

public class PAddressMeta {
	
	public int zoomLevel;
	public int latitude;
	public int longitude;
	public String name;
	public String src;
	
	public float xOffset;
	public float yOffset;
	
	@Override
	public String toString() {
		return "PAddressMeta [latitude=" + latitude + ", longitude="
				+ longitude + ", name=" + name + ", src=" + src + "]";
	}
	
	public void build(int xcp, int ycp) {
		
		xOffset = (longitude - xcp) >> zoomLevel;
		yOffset = (latitude - ycp) >> zoomLevel;
	}
}
