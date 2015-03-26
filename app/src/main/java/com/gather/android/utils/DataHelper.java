package com.gather.android.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;

public class DataHelper {
	private Context mContext;
	private String key;
	public DataHelper(Context context, String dataKey){
		this.mContext = context;
		MD5 md5 = new MD5();
		this.key = md5.md5Encode(dataKey);
	}
	
	public boolean saveData(String dataContent){
		try {
			String content = dataContent;
			FileOutputStream outputStream = mContext.openFileOutput(key, Context.MODE_PRIVATE);
			byte[] bytes = content.getBytes();
			outputStream.write(bytes);
			outputStream.close();
			outputStream.flush();
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getData(){
		try {
			FileInputStream inputStream = mContext.openFileInput(key);
			int length = inputStream.available();
			byte[] buffer = new byte[length];
			inputStream.read(buffer);
			String data = new String(buffer);
			inputStream.close();
			return data;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Context getContext(){
		return this.mContext;
	}
	
	public String getDataKey(){
		return this.key;
	}
}
