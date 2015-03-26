package com.gather.android.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class PublishTrendsService extends DbService {
	private Cursor cursor;

	public PublishTrendsService(Context context) {
		super(context);
	}

	public boolean insertTrends(PublishTrendsInfo publishTrendsInfo) {
		this.writeBegin();
		String sql = "insert into publish_trends_info(content,imgPaths,time,userId) values(?,?,?,?)";
		sdb.execSQL(sql, new Object[] { publishTrendsInfo.content, publishTrendsInfo.imgPaths, publishTrendsInfo.time, publishTrendsInfo.userId});
		this.writeSuccess();
		this.writeOver();
		return true;
	}	

	public boolean delete(int... idList) {
		this.writeBegin();
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("delete from publish_trends_info where id=?");
		for (int i = 0; i < idList.length - 1; i++) {
			sbSql.append(" or id=?");
		}
		try {
			Object[] values = new Object[idList.length];
			for (int i = 0; i < idList.length; i++) {
				values[i] = idList[i];
			}
			sdb.execSQL(sbSql.toString(), values);
			this.writeSuccess();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			this.writeOver();
		}
	}

	public long count() {
		this.readBegin();
		String sql = "select count(id) from publish_trends_info";
		try {
			cursor = sdb.rawQuery(sql, null);
			cursor.moveToFirst();
			long count = cursor.getLong(0);
			cursor.close();
			return count;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			this.readOver();
		}
	}

	public ArrayList<PublishTrendsInfo> getTrendsList(int userId) {
		ArrayList<PublishTrendsInfo> list = null;
		this.readBegin();
		String sql = "select * from publish_trends_info where userId=? order by publish_trends_info.time desc";
		try {
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(userId) });
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String content = cursor.getString(cursor.getColumnIndex("content"));
				String imgPaths = cursor.getString(cursor.getColumnIndex("imgPaths"));
				long time = cursor.getLong(cursor.getColumnIndex("time"));
				int uid = cursor.getInt(cursor.getColumnIndex("userId"));
				if (null == list) {
					list = new ArrayList<PublishTrendsInfo>();
				}
				list.add(new PublishTrendsInfo(id, content, imgPaths, time, uid));
			}
			cursor.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			this.readOver();
		}
	}

}
