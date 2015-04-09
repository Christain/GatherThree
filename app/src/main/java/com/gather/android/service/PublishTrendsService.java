package com.gather.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.TrendsModel;
import com.gather.android.model.TrendsPicModel;
import com.gather.android.params.PublishTrendsParam;
import com.gather.android.params.UploadPicParam;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class PublishTrendsService extends Service {
	
	public static final String SERVICE_NAME = "com.gather.android.service.PublishTrendsService";
	public static final String PUBLISH_OVER = "com.gather.android.broadcast.PublishOver";
	private com.gather.android.database.PublishTrendsService service;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		service = new com.gather.android.database.PublishTrendsService(PublishTrendsService.this);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null && intent.hasExtra("MODEL")) {
			TrendsModel model = (TrendsModel) intent.getSerializableExtra("MODEL");
			if (model != null) {
				PublishTrends(model);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void PublishTrends(TrendsModel model) {
		ArrayList<Integer> imgIds = new ArrayList<Integer>();
		if (model.getImgs().getTotal_num() != 0) {
			UploadImage(model.getId(), model.getContent(), model.getImgs().getImgs(), 0, imgIds);
		} else {
			UploadText(model.getId(), model.getContent(), imgIds);
		}
	}
	
	/**
	 * 上传图片
	 * @param content
	 * @param list
	 * @param index
	 * @param imgIds
	 */
	private void UploadImage(final int id, final String content, final ArrayList<TrendsPicModel> list, final int index, final ArrayList<Integer> imgIds) {
		UploadPicParam param = new UploadPicParam(new File(list.get(index).getImg_url()));
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    imgIds.add(object.getInt("img_id"));
                    if (index < list.size() - 1) {
                        UploadImage(id, content, list, index + 1, imgIds);
                    } else {
                        UploadText(id, content, imgIds);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNeedLogin(String msg) {

            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {

            }
        });
	}
	
	/**
	 * 发布动态
	 * @param content
	 * @param list
	 */
	private void UploadText(final int id, String content, ArrayList<Integer> list) {
		PublishTrendsParam param = new PublishTrendsParam(content, list);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (service == null) {
                    service = new com.gather.android.database.PublishTrendsService(PublishTrendsService.this);
                }
                service.delete(id);
                Intent intent = new Intent();
                intent.setAction(PUBLISH_OVER);
                sendBroadcast(intent);
            }

            @Override
            public void onNeedLogin(String msg) {

            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {

            }
        });
	}

}
