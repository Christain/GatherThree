package com.gather.android.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	// // MD5変換
	// public static String md5Encode(String str) {
	// if (str != null && !str.equals("")) {
	// try {
	// MessageDigest md5 = MessageDigest.getInstance("MD5");
	// char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
	// 'b', 'c', 'd', 'e', 'f' };
	// byte[] md5Byte = md5.digest(str.getBytes("UTF8"));
	// StringBuffer sb = new StringBuffer();
	// for (int i = 0; i < md5Byte.length; i++) {
	// sb.append(HEX[(int) (md5Byte[i] & 0xff) / 16]);
	// sb.append(HEX[(int) (md5Byte[i] & 0xff) % 16]);
	// }
	// str = sb.toString();
	// } catch (NoSuchAlgorithmException e) {
	// } catch (Exception e) {
	// }
	// }
	// return str;
	// }

	public static String md5Encode16(String plainTet) {
		String ret = md5Encode(plainTet);
		ret = ret.substring(8, 24);
		return ret;
	}

	public static String md5Encode(String plainText) {
		String result = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result;
	}

}
