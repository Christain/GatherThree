package com.gather.android.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.gather.android.activity.ActDetail;
import com.gather.android.activity.LoginIndex;
import com.gather.android.constant.Constant;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tendcloud.tenddata.TCAgent;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;
	private static String type = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		api = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID, false);
		api.handleIntent(getIntent(), this);
		super.onCreate(savedInstanceState);
		type = "";
		Intent intent = getIntent();
		if (intent.hasExtra("TYPE")) {
			type = intent.getExtras().getString("TYPE");
			if (type.equals("LOGIN")) {
				api.registerApp(Constant.WE_CHAT_APPID);
				SendAuth.Req req = new SendAuth.Req();
				req.scope = "snsapi_userinfo";
				req.state = "com.gather.android";
				api.sendReq(req);
			}
		}
	}

	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			Toast.makeText(this, "COMMAND_GETMESSAGE_FROM_WX", Toast.LENGTH_SHORT).show();
			finish();
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			Toast.makeText(this, "COMMAND_SHOWMESSAGE_FROM_WX", Toast.LENGTH_SHORT).show();
			finish();
			break;
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}
	
	@Override
	public void onResp(BaseResp resp) {
		String result = "";
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			if (type.equals("LOGIN")) {
				SendAuth.Resp sendResp = (SendAuth.Resp) resp;
				if (sendResp.state.equals("com.gather.android")) {
					Intent intent = new Intent(WXEntryActivity.this, LoginIndex.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("CODE", sendResp.code);
					startActivity(intent);
				}
			} else {
				TCAgent.onEvent(WXEntryActivity.this, "分享到微信");
				result = "微信分享成功";
			}
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			if (type.equals("LOGIN")) {
				result = "微信登录取消";
			} else {
				result = "微信分享取消";
			}
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			if (type.equals("LOGIN")) {
				result = "微信登录已拒绝";
			}else {
				result = "微信认证失败";
			}
			break;
		default:
			result = "失败，请重试";
			break;
		}
		if (result.length() > 1) {
			Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
		}
		finish();
	}
	
}
