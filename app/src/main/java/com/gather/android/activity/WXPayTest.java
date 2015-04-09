package com.gather.android.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.constant.Constant;
import com.gather.android.widget.swipeback.SwipeBackActivity;
import com.gather.android.wxpay.MD5;
import com.gather.android.wxpay.Util;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 微信支付测试
 * Created by Christain on 2015/4/7.
 */
public class WXPayTest extends SwipeBackActivity implements View.OnClickListener {

    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvTitle, tvRight;

    private static final String TAG = "PayActivity";

    PayReq req;
    final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
    TextView show;
    Map<String, String> resultunifiedorder;
    StringBuffer sb;

    @Override
    protected int layoutResId() {
        return R.layout.wxpay_test;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
        this.ivRight = (ImageView) findViewById(R.id.ivRight);
        this.tvLeft = (TextView) findViewById(R.id.tvLeft);
        this.tvTitle = (TextView) findViewById(R.id.tvTitle);
        this.tvRight = (TextView) findViewById(R.id.tvRight);
        this.tvLeft.setVisibility(View.GONE);
        this.ivRight.setVisibility(View.GONE);
        this.tvRight.setVisibility(View.GONE);
        this.ivLeft.setVisibility(View.VISIBLE);
        this.tvTitle.setText("微信支付测试");
        this.ivLeft.setImageResource(R.drawable.title_back_click_style);
        this.ivLeft.setOnClickListener(this);


        show = (TextView) findViewById(R.id.editText_prepay_id);
        req = new PayReq();
        sb = new StringBuffer();

        msgApi.registerApp(Constant.WE_CHAT_APPID);
        //生成prepay_id
        Button payBtn = (Button) findViewById(R.id.unifiedorder_btn);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
                getPrepayId.execute();
            }
        });
        Button appayBtn = (Button) findViewById(R.id.appay_btn);
        appayBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendPayReq();
            }
        });

        //生成签名参数
        Button appay_pre_btn = (Button) findViewById(R.id.appay_pre_btn);
        appay_pre_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                genPayReq();
            }
        });
    }


    /**
     * 生成签名
     */

    private String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constant.WX_PAY_API_KEY);
        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        Log.e("orion", packageSign);
        return packageSign;
    }

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constant.WX_PAY_API_KEY);
        this.sb.append("sign str\n" + sb.toString() + "\n\n");
        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
        Log.e("orion", appSign);
        return appSign;
    }

    private String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");
            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</xml>");

        Log.e("orion", sb.toString());
        return sb.toString();
    }

    private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {

        private ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(WXPayTest.this, "提示", "正在获取预支付订单...");
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            if (dialog != null) {
                dialog.dismiss();
            }
            sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");
            show.setText(sb.toString());
            resultunifiedorder = result;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {
            String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
            String entity = genProductArgs();
            Log.e("orion", entity);
            byte[] buf = Util.httpPost(url, entity);
            String content = new String(buf);
            Log.e("orion", content);
            Map<String, String> xml = decodeXml(content);
            return xml;
        }
    }


    public Map<String, String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:
                        if ("xml".equals(nodeName) == false) {
                            //实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:

                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion", e.toString());
        }
        return null;

    }


    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }


    private String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private String genProductArgs() {
        StringBuffer xml = new StringBuffer();

        try {
            String nonceStr = genNonceStr();


            xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("appid", Constant.WE_CHAT_APPID));
            packageParams.add(new BasicNameValuePair("body", "APP pay test"));
            packageParams.add(new BasicNameValuePair("mch_id", Constant.MCH_ID));
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            if (Constant.SHOW_LOG) {
                packageParams.add(new BasicNameValuePair("notify_url", Constant.ALIPAY_CALLBACK_URL_TEST));
            } else {
                packageParams.add(new BasicNameValuePair("notify_url", Constant.ALIPAY_CALLBACK_URL));
            }
            packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
            packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
            packageParams.add(new BasicNameValuePair("total_fee", "1"));
            packageParams.add(new BasicNameValuePair("trade_type", "APP"));


            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));


            String xmlstring = toXml(packageParams);

            return xmlstring;

        } catch (Exception e) {
            Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }
    }

    private void genPayReq() {

        req.appId = Constant.WE_CHAT_APPID;
        req.partnerId = Constant.MCH_ID;
        req.prepayId = resultunifiedorder.get("prepay_id");
        req.packageValue = "prepay_id=" + resultunifiedorder.get("prepay_id");
        req.nonceStr = genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        req.sign = genAppSign(signParams);

        sb.append("sign\n" + req.sign + "\n\n");

        show.setText(sb.toString());

        Log.e("orion", signParams.toString());

    }

    private void sendPayReq() {
        msgApi.registerApp(Constant.WE_CHAT_APPID);
        msgApi.sendReq(req);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
        }
    }
}
