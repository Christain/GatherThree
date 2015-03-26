package com.gather.android.utils;

public class ThumbnailUtil {
	
	//源地址+@宽w_高h_1e_0c_50Q_1x.jpg
	
	public static String ThumbnailMethod(String url, int width, int high, int quality) {
		if (url.startsWith("http")) {
			StringBuffer sb = new StringBuffer();
			sb.append(url);
			sb.append("@");
			sb.append(width);
			sb.append("w_");
			sb.append(high);
			sb.append("h_");
			sb.append("1e_");
			sb.append("0c_");
			sb.append(quality);
			sb.append("Q_");
			sb.append("1x.jpg");
			return sb.toString();
		} else {
			return url;
		}
	}

}
