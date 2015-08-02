package com.gulu.album.item;

import java.util.Arrays;

public class TrafficInfo {
	
	/**
	 * show type
	 */
	public byte sType;
	
	/**
	 * the length of coordinate position array of picture
	 */
	public int iLength;
	
	/**
	 * the coordinate position array of picture
	 */
	public int[] iCoordinates;
	
	/**
	 * the size of coordinate pairs array two byte
	 */
	public int length;
	
	/**
	 * coordinate pairs array
	 */
	public int[] coordinates;
	
	/**
	 * the traffic info event type
	 */
	public String eventType;
	
	/**
	 * the timestamp
	 */
	public String timestamp;
	
	/**
	 * the traffic info detail
	 */
	public String detail;
	
	/**
	 * the picture path
	 */
	public String iPath;
	
	@Override
	public String toString() {
		return "TrafficInfo [sType=" + sType + ", iLength=" + iLength
				+ ", iCoordinates=" + Arrays.toString(iCoordinates)
				+ ", length=" + length + ", coordinates="
				+ Arrays.toString(coordinates) + ", eventType=" + eventType
				+ ", timestamp=" + timestamp + ", detail=" + detail
				+ ", iPath=" + iPath + "]";
	}
	
}
