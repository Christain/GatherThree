package com.gather.android.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.gather.android.R;
import com.gather.android.application.GatherApplication;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.model.ActCheckInListModel;
import com.gather.android.model.ActMoreInfoModel;
import com.gather.android.params.SignInParam;
import com.gather.android.utils.ClickUtil;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.view.ViewfinderView;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Vector;

public class QRCodeScan extends BaseActivity implements Callback, OnClickListener {

	private ImageView ivBack;

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	
	private LocationClient mLocationClient;
	private LoadingDialog mLoadingDialog;

    private ActMoreInfoModel actMoreInfoModel;

	@Override
	protected int layoutResId() {
		return R.layout.qr_code_scan;
	}

	@Override
	protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("MORE_INFO")) {
            this.actMoreInfoModel = (ActMoreInfoModel) intent.getSerializableExtra("MORE_INFO");
            this.ivBack = (ImageView) findViewById(R.id.ivBack);

            CameraManager.init(getApplication());
            viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
            hasSurface = false;
            inactivityTimer = new InactivityTimer(this);

            this.mLocationClient = ((GatherApplication)getApplication()).mLocationClient;
            this.mLoadingDialog = LoadingDialog.createDialog(QRCodeScan.this, true);
            this.ivBack.setOnClickListener(this);

            this.initLocation();
        } else {
            finish();
        }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBack:
			if (!ClickUtil.isFastClick()) {
				finish();
			}
			break;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		if (resultString.equals("")) {
			Toast.makeText(QRCodeScan.this, "Scan failed!", Toast.LENGTH_SHORT).show();
			finish();
		} else {
            try {
//                resultString = "647db2e9999c992f81b2c0bd6a3d80e6a9fc00d7a6cc74339a83013cf3c7ff858ad9ab48d5680cd92292be5c796b57ef003df7710ca6f6de";
//                DES des = new DES();
//                String json = des.decrypt(resultString);
                String json = "{\"filter\":\"checkin_id\",\"value\":1}";
                if (Constant.SHOW_LOG) {
                    Log.e("QR_CODE", json);
                }
                try {
                    JSONObject object = new JSONObject(json);
                    if (object.has("filter") && object.has("value")) {
                        if (object.getString("filter").equals("checkin_id")) {
                            signInAct(object.getInt("value"));
                        } else {
                            Toast.makeText(QRCodeScan.this, "不是集合啦指定活动", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(QRCodeScan.this, "不是集合啦指定活动", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(QRCodeScan.this, "不是集合啦指定活动", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}
	
	@Override
	public void onStop() {
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		super.onStop();
	}
	
	/**
	 * 初始化定位
	 */
	private void initLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
//		option.setScanSpan(60000);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);//是否需要地址信息
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}
	
	/**
	 * 签到
	 */
	private void signInAct(int checkinId){
		mLoadingDialog.setMessage("签到中...");
		mLoadingDialog.show();
		SignInParam param = new SignInParam(checkinId);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                Gson gson = new Gson();
                ActCheckInListModel list = gson.fromJson(result, ActCheckInListModel.class);
                if (list != null) {
                    if (list.getCheckins().size() > 0) {
                        Intent intent = new Intent(QRCodeScan.this, ActPassPort.class);
                        intent.putExtra("MODEL", list.getCheckins().get(list.getCheckins().size() - 1));
                        if (actMoreInfoModel != null) {
                            intent.putExtra("MORE_INFO", actMoreInfoModel);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        toast("签到失败");
                        finish();
                    }
                } else {
                    toast("签到失败");
                    finish();
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast(errorMsg);
                finish();
            }
        });
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
}