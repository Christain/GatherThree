package com.gather.android.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.gather.android.activity.ActDetail;
import com.gather.android.activity.Chat;
import com.gather.android.activity.TrendsComment;
import com.gather.android.activity.UserCenter;
import com.gather.android.activity.WebStrategy;
import com.gather.android.application.GatherApplication;
import com.gather.android.constant.Constant;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manage.AppManage;
import com.gather.android.params.BindPushParam;
import com.gather.android.preference.AppPreference;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Push消息处理receiver。请编写您需要的回调函数， 一般来说： onBind是必须的，用来处理startWork返回值；
 * onMessage用来接收透传消息； onSetTags、onDelTags、onListTags是tag相关操作的回调；
 * onNotificationClicked在通知被点击时回调； onUnbind是stopWork接口的返回值回调
 * 
 * 返回值中的errorCode，解释如下： 0 - Success 10001 - Network Problem 30600 - Internal
 * Server Error 30601 - Method Not Allowed 30602 - Request Params Not Valid
 * 30603 - Authentication Failed 30604 - Quota Use Up Payment Required 30605 -
 * Data Required Not Found 30606 - Request Time Expires Timeout 30607 - Channel
 * Token Timeout 30608 - Bind Relation Not Found 30609 - Bind Number Too Many
 * 
 * 当您遇到以上返回错误时，如果解释不了您的问题，请用同一请求的返回值requestId和errorCode联系我们追查问题。
 * 
 */
public class PushMessageReceiver extends FrontiaPushMessageReceiver {
	/** TAG to Log */
	public static final String TAG = PushMessageReceiver.class.getSimpleName();

	/**
	 * 调用PushManager.startWork后，sdk将对push
	 * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
	 * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
	 * 
	 * @param context
	 *            BroadcastReceiver的执行Context
	 * @param errorCode
	 *            绑定接口返回值，0 - 成功
	 * @param appid
	 *            应用id。errorCode非0时为null
	 * @param userId
	 *            应用user id。errorCode非0时为null
	 * @param channelId
	 *            应用channel id。errorCode非0时为null
	 * @param requestId
	 *            向服务端发起的请求id。在追查问题时有用；
	 * @return none
	 */

	public static String baiduUserId = "", baiduChannelId = "";

	@Override
	public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid=" + appid + " userId=" + userId + " channelId=" + channelId + " requestId=" + requestId;
		if (Constant.SHOW_LOG) {
			Log.d(TAG, responseString);
			// 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
			if (errorCode == 0) {
				baiduUserId = userId;
				baiduChannelId = channelId;
				Toast.makeText(context, "推送绑定成功", Toast.LENGTH_SHORT).show();
				if (AppPreference.hasLogin(context) && GatherApplication.cityId != 0) {
					BindService(context, userId, channelId);
				}
			}
		}
	}

	/**
	 * 接收透传消息的函数。
	 * 
	 * @param context
	 *            上下文
	 * @param message
	 *            推送的消息
	 * @param customContentString
	 *            自定义内容,为空或者json字符串
	 */
	@Override
	public void onMessage(Context context, String message, String customContentString) {
		String messageString = "透传消息 message=\"" + message + "\" customContentString=" + customContentString;
		if (Constant.SHOW_LOG) {
			Log.d(TAG, messageString);
		}

		// 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
		if (!TextUtils.isEmpty(customContentString)) {
			JSONObject customJson = null;
			try {
				customJson = new JSONObject(customContentString);
				String myvalue = null;
				if (!customJson.isNull("mykey")) {
					myvalue = customJson.getString("mykey");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            推送的通知的标题
	 * @param description
	 *            推送的通知的描述
	 * @param customContentString
	 *            自定义内容，为空或者json字符串
	 */
	@Override
	public void onNotificationClicked(Context context, String title, String description, String customContentString) {
		String notifyString = "通知点击 title=\"" + title + "\" description=\"" + description + "\" customContent=" + customContentString;
		if (Constant.SHOW_LOG) {
			Log.d(TAG, notifyString);
		}

		// 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
		if (!TextUtils.isEmpty(customContentString)) {
			JSONObject customJson = null;
			try {
				customJson = new JSONObject(customContentString);
				if (!customJson.isNull("filter_k_id") && !customJson.isNull("filter_v_id")) {
					// 跳转key：11活动，21资讯，31用户，41动态，51私信
					int key = customJson.getInt("filter_k_id");
					int value = customJson.getInt("filter_v_id");
					Intent intent = null;
					switch (key) {
					case 11:
						intent = new Intent(context, ActDetail.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("ID", value);
						context.startActivity(intent);
						break;
					case 21:
						intent = new Intent(context, WebStrategy.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("ID", value);
						context.startActivity(intent);
						break;
					case 31:
						intent = new Intent(context, UserCenter.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("UID", value);
						context.startActivity(intent);
						break;
					case 41:
						intent = new Intent(context, TrendsComment.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("ID", value);
						context.startActivity(intent);
						break;
					case 51:
						if (getTopActivity(context).contains("Chat")) {
							removeActivity(context);
						}
						intent = new Intent(context, Chat.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("UID", value);
						context.startActivity(intent);
						break;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * setTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
	 * @param sucessTags
	 *            设置成功的tag
	 * @param failTags
	 *            设置失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onSetTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onSetTags errorCode=" + errorCode + " sucessTags=" + sucessTags + " failTags=" + failTags + " requestId=" + requestId;
		if (Constant.SHOW_LOG) {
			Log.d(TAG, responseString);
			if (errorCode == 0) {
				Toast.makeText(context, "Tag设置成功", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * delTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
	 * @param sucessTags
	 *            成功删除的tag
	 * @param failTags
	 *            删除失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onDelTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onDelTags errorCode=" + errorCode + " sucessTags=" + sucessTags + " failTags=" + failTags + " requestId=" + requestId;
		if (Constant.SHOW_LOG) {
			Log.i(TAG, responseString);
			if (errorCode == 0) {
				Toast.makeText(context, "Tag删除成功", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * listTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示列举tag成功；非0表示失败。
	 * @param tags
	 *            当前应用设置的所有tag。
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onListTags(Context context, int errorCode, List<String> tags, String requestId) {
		String responseString = "onListTags errorCode=" + errorCode + " tags=" + tags;
		if (Constant.SHOW_LOG) {
			Log.d(TAG, responseString);
			if (errorCode == 0) {
				Toast.makeText(context, "Tag组设置成功", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * PushManager.stopWork() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示从云推送解绑定成功；非0表示失败。
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		String responseString = "onUnbind errorCode=" + errorCode + " requestId = " + requestId;
		if (Constant.SHOW_LOG) {
			Log.d(TAG, responseString);
			if (errorCode == 0) {
				Toast.makeText(context, "推送解绑定成功", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 绑定推送到服务端
	 * 
	 * @param context
	 * @param baiduUserId
	 * @param baiduChannelId
	 */
	private void BindService(final Context context, String baiduUserId, String baiduChannelId) {
		BindPushParam param = new BindPushParam(GatherApplication.cityId, 3, baiduUserId, baiduChannelId);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (Constant.SHOW_LOG) {
                    Toast.makeText(context, "绑定服务成功", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNeedLogin(String msg) {

            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (Constant.SHOW_LOG) {
                    Toast.makeText(context, "绑定服务失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
	}

	private String getTopActivity(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		if (runningTaskInfos != null)
			return (runningTaskInfos.get(0).topActivity).getClassName();
		else
			return null;
	}

	private void removeActivity(Context context) {
		AppManage appManage = AppManage.getInstance();
		appManage.removeTopActivity();
	}

}
