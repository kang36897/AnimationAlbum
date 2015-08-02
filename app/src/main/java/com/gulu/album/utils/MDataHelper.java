package com.gulu.album.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.gulu.album.R;
import com.gulu.album.item.PAddressMeta;
import com.gulu.album.item.TrafficInfo;

public class MDataHelper {
	
	public final static int DEFAULT_BUFFER_SIZE = 1024;
	public final static Charset DEFAULT_CHARSET = Charset.forName("GBK");
	
	final static String DEBUG_TAG = "MDataHelper";
	final static int DEFAULT_POSITION_VALUE = -1;
	
	private final static BitmapFactory.Options DEFAULT_BITMAPFACTORY_OPTIONS = new Options();;
	static {
		DEFAULT_BITMAPFACTORY_OPTIONS.inScaled = false;
	}
	
	private Context context;
	private DisplayMetrics mDisplayMetrics;
	
	private ArrayList<PAddressMeta> plist;
	private ArrayList<TrafficInfo> tlist;
	
	private int xCenterPosition = DEFAULT_POSITION_VALUE;
	private int yCenterPosition = DEFAULT_POSITION_VALUE;
	private int zoomLevel;
	
	private float xLeftTopOffset;
	private float yLeftTopOffset;
	private float xRightTopOffset;
	
	public MDataHelper(Context context) {
		this();
		this.context = context;
		mDisplayMetrics = context.getResources().getDisplayMetrics();
	}
	
	public MDataHelper() {
		plist = new ArrayList<PAddressMeta>();
		tlist = new ArrayList<TrafficInfo>();
	}
	
	public static Bitmap fetchBitmapFrom(String spec,
			BitmapFactory.Options options) throws Exception {
		if (TextUtils.isEmpty(spec)) {
			throw new IllegalArgumentException("the request url is null");
		}
		
		URL requestUrl = new URL(spec);
		HttpURLConnection connection = (HttpURLConnection) requestUrl
				.openConnection();
		try {
			connection.setConnectTimeout(15 * 1000);
			connection.connect();
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				
				InputStream is = connection.getInputStream();
				Bitmap result = BitmapFactory.decodeStream(is, null,
						(options == null ? DEFAULT_BITMAPFACTORY_OPTIONS
								: options));
				
				return result;
			} else {
				throw new Exception("failed ! getResponseCode = "
						+ connection.getResponseCode());
			}
		} finally {
			if (connection != null)
				connection.disconnect();
		}
		
	}
	
	public String buildRequestUrl(int zoom, double latitude, double longitude,
			int width, int height) {
		this.zoomLevel = zoom;
		StringBuilder builder = new StringBuilder(context.getResources()
				.getString(R.string.map_basic_bitmap_url));
		builder.append("zoom=").append(zoomLevel);
		builder.append("&").append("x=").append(String.valueOf(latitude))
				.append("&").append("y=").append(String.valueOf(longitude));
		builder.append("&").append("w=").append(String.valueOf(width))
				.append("&").append("h=").append(String.valueOf(height));
		
		return builder.toString();
	}
	
	public Bitmap extractBitmap(String requestUrl) throws Exception {
		byte[] data = MDataHelper.fecthResourceFrom(requestUrl);
		data = MDataHelper.decompressData(data);
		
		ByteBuffer buffer = fecthMetaDataFrom(data);
		
		Bitmap target = MDataHelper.decodeBitmap(buffer, null);
		int xPivot = target.getWidth() / 2;
		int yPivot = target.getHeight() / 2;
		
		Canvas canvas = new Canvas(target);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(20);
		for (int i = 0; i < plist.size(); i++) {
			PAddressMeta pam = plist.get(i);
			float x = xPivot + pam.xOffset;
			float y = yPivot + pam.yOffset;
			
			paint.setStyle(Style.STROKE);
			paint.setColor(Color.WHITE);
			paint.setStrokeWidth(1.0f);
			canvas.drawText(pam.name, x, y, paint);
			
			paint.setStyle(Style.FILL);
			paint.setColor(Color.BLACK);
			canvas.drawText(pam.name, x, y, paint);
			
		}
		
		return target;
	}
	
	public void clear() {
		
		xCenterPosition = DEFAULT_POSITION_VALUE;
		yCenterPosition = DEFAULT_POSITION_VALUE;
		
		if (plist != null)
			plist.clear();
		
		if (tlist != null)
			tlist.clear();
	}
	
	public ArrayList<PAddressMeta> getPlist() {
		return plist;
	}
	
	public void setPlist(ArrayList<PAddressMeta> plist) {
		this.plist = plist;
	}
	
	public ArrayList<TrafficInfo> getTlist() {
		return tlist;
	}
	
	public void setTlist(ArrayList<TrafficInfo> tlist) {
		this.tlist = tlist;
	}
	
	public static Bitmap fetchBitmapFrom(InputStream is,
			BitmapFactory.Options options, boolean isClosable) throws Exception {
		if (is == null) {
			throw new IllegalArgumentException("the InputStream is null");
		}
		
		try {
			Bitmap result = BitmapFactory
					.decodeStream(is, null,
							(options == null ? DEFAULT_BITMAPFACTORY_OPTIONS
									: options));
			return result;
			
		} finally {
			if (is != null && isClosable) {
				is.close();
			}
		}
	}
	
	public static byte[] fecthResourceFrom(String spec) throws Exception {
		if (TextUtils.isEmpty(spec)) {
			throw new IllegalArgumentException("the  request url is null");
		}
		
		URL requestUrl = new URL(spec);
		HttpURLConnection connection = (HttpURLConnection) requestUrl
				.openConnection();
		
		try {
			connection.setConnectTimeout(15 * 1000);
			connection.connect();
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				
				ByteArrayOutputStream bo = new ByteArrayOutputStream(
						DEFAULT_BUFFER_SIZE);
				
				InputStream is = connection.getInputStream();
				byte[] buff = new byte[DEFAULT_BUFFER_SIZE];
				int count = 0;
				while ((count = is.read(buff, 0, buff.length)) != -1) {
					bo.write(buff, 0, count);
				}
				
				return bo.toByteArray();
			} else {
				throw new Exception("failed ! getResponseCode = "
						+ connection.getResponseCode());
			}
		} finally {
			if (connection != null)
				connection.disconnect();
		}
		
	}
	
	public static byte[] decompressData(byte[] input)
			throws DataFormatException {
		ByteArrayOutputStream bo = new ByteArrayOutputStream(input.length * 2);
		
		Inflater decompresser = new Inflater();
		decompresser.setInput(input);
		byte[] buff = new byte[DEFAULT_BUFFER_SIZE];
		
		while (!decompresser.finished()) {
			int length = decompresser.inflate(buff);
			
			if (length != 0)
				bo.write(buff, 0, length);
		}
		
		decompresser.end();
		
		return bo.toByteArray();
		
	}
	
	public ByteBuffer fecthMetaDataFrom(byte[] data) throws Exception {
		if (data == null || data.length == 0) {
			throw new IllegalArgumentException("the input param is null");
		}
		
		byte[] temp = new byte[4];
		ByteBuffer meta = ByteBuffer.wrap(data);
		meta.order(ByteOrder.BIG_ENDIAN);
		meta.get(temp);
		xCenterPosition = getUnsignedInteger(temp);
		Log.d(DEBUG_TAG, "xCenterPosition =" + xCenterPosition);
		meta.get(temp);
		yCenterPosition = getUnsignedInteger(temp);
		Log.d(DEBUG_TAG, "yCenterPosition =" + yCenterPosition);
		
		// read the 5 offset because the first 20 bytes is useless,so just skip
		// them.
		meta.position(meta.position() + 20);
		// read meta data about line
		
		int size = meta.getShort() & 0xFF;
		
		if (plist == null) {
			plist = new ArrayList<PAddressMeta>();
		}
		
		for (int i = 0; i < size; i++) {
			PAddressMeta pam = new PAddressMeta();
			pam.zoomLevel = this.zoomLevel;
			meta.get(temp);
			pam.longitude = getUnsignedInteger(temp);
			meta.get(temp);
			pam.latitude = getUnsignedInteger(temp);
			pam.build(xCenterPosition, yCenterPosition);
			
			pam.name = getString(meta);
			pam.src = getString(meta);
			
			plist.add(pam);
		}
		
		size = meta.getShort() & 0xFF; // number of traffic information event
		
		if (tlist == null) {
			tlist = new ArrayList<TrafficInfo>();
		}
		
		for (int j = 0; j < size; j++) {
			TrafficInfo ti = new TrafficInfo();
			ti.sType = meta.get();
			ti.iLength = byteToInt(meta.get());
			ti.iCoordinates = new int[ti.iLength * 2];
			for (int m = 0; m <= ti.iCoordinates.length - 2; m += 2) {
				meta.get(temp);
				ti.iCoordinates[m] = getUnsignedInteger(temp);
				meta.get(temp);
				ti.iCoordinates[m + 1] = getUnsignedInteger(temp);
			}
			
			ti.length = meta.getShort() & 0xFF;
			ti.coordinates = new int[ti.length * 2];
			for (int n = 0; n <= ti.coordinates.length - 2; n += 2) {
				meta.get(temp);
				ti.coordinates[n] = getUnsignedInteger(temp);
				meta.get(temp);
				ti.coordinates[n + 1] = getUnsignedInteger(temp);
			}
			
			ti.eventType = getString(meta);
			ti.timestamp = getString(meta);
			ti.detail = getString(meta);
			ti.iPath = getString(meta);
			
			// System.out.println(ti);
		}
		
		return meta;
		
	}
	
	public int getxCenterPosition() {
		return xCenterPosition;
	}
	
	public int getyCenterPosition() {
		return yCenterPosition;
	}
	
	public static Bitmap decodeBitmap(ByteBuffer buffer,
			BitmapFactory.Options options) {
		
		int offset = buffer.position();
		int length = buffer.limit() - buffer.position();
		
		BitmapFactory.Options temp = (options == null ? DEFAULT_BITMAPFACTORY_OPTIONS
				: options);
		temp.inMutable = true;
		
		return BitmapFactory.decodeByteArray(buffer.array(), offset, length,
				temp);
	}
	
	public static void main(String[] args) throws Exception {
		String url = "http://tsp.navi168.com:3480/mapImage/mapImage3.jsp?zoom=6&x=121.508883&y=31.089231&w=800&h=800";
		
		byte[] data = decompressData(fecthResourceFrom(url));
		new MDataHelper().fecthMetaDataFrom(data);
	}
	
	public static String getString(ByteBuffer input) {
		String result = null;
		int start = input.position();
		int i = start;
		do {
			if (input.get(i) == 0x00) {
				byte[] dst = new byte[i - start];
				input.position(start);
				input.get(dst);
				input.position(i + 1);
				
				result = new String(dst, DEFAULT_CHARSET);
				// System.out.println(result);
				return result;
			}
			i++;
		} while (i < input.limit());
		return result;
	}
	
	public static double getDegreeDouble(byte[] degree) {
		
		int num = 1;
		byte duB = degree[0];
		byte fenB = degree[1];
		byte[] miaoB = new byte[2];
		miaoB[0] = degree[2];
		miaoB[1] = degree[3];
		int du = byteToInt(duB);
		int fen = byteToInt(fenB);
		int miao = seqToInt(miaoB);
		if (fen >= 128) {
			fen = fen - 128;
			num = -1;
		}
		return (du + (fen * 10000d + miao) / 60d / 10000d) * num;
	}
	
	public static int getUnsignedInteger(byte[] input) {
		int result = 0;
		result = (input[0] << 24) | (input[1] & 0xFF) << 16
				| (input[2] & 0xFF) << 8 | (input[3] & 0xFF);
		
		return result;
		
	}
	
	public static byte[] getDegreeBytes(double degree) {
		int num = 1;
		if (degree < 0) {
			num = -1;
			degree = degree * -1;
		}
		byte[] d = new byte[4];
		int du = (int) degree;
		int fen = (int) ((degree - du) * 60);
		int miao = (int) Math.round((((degree - du) * 60 - fen) * 10000));
		if (num == -1) {
			fen = fen + 128;
		}
		byte duB = intToByte(du);
		byte fenB = intToByte(fen);
		byte[] miaoB = intToSeq(miao);
		d[0] = duB;
		d[1] = fenB;
		d[2] = miaoB[0];
		d[3] = miaoB[1];
		return d;
	}
	
	public static byte[] intToSeq(int data) {
		data = data & 0xffff;
		byte[] temp = new byte[2];
		for (int i = 0; i < 2; i++) {
			temp[i] = intToByte(data >>> (8 - i * 8));
		}
		return temp;
	}
	
	public static int seqToInt(byte[] data) {
		return ((byteToInt(data[0])) << 8) + byteToInt(data[1]);
	}
	
	public static int byteToInt(byte data) {
		int a = (int) data;
		a = a & 0xff;
		return a;
		
	}
	
	public static byte intToByte(int data) {
		data = data & 0xff;
		int temp = data;
		if (temp > 127) {
			data = temp - 256;
		}
		byte b = (byte) data;
		return b;
	}
}
