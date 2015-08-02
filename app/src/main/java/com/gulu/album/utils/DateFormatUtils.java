package com.gulu.album.utils;

import android.text.format.DateFormat;

import com.gulu.album.constants.ConstantsCave;

public class DateFormatUtils {
	
	public static CharSequence formatTimeToString(String inFormat,
			long inTimeInMillis) {
		return DateFormat.format(inFormat, inTimeInMillis);
	}
	
	public static CharSequence formatTimeToString(long inTimeInMillis) {
		return formatTimeToString(ConstantsCave.DEFAULT_DATE_FORMAT_PATTERN,
				inTimeInMillis);
	}
}
