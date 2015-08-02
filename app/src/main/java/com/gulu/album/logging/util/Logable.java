package com.gulu.album.logging.util;


public interface Logable {

	public boolean isDebuging();

	public String getTag();

	public void debug(String msg);

}
