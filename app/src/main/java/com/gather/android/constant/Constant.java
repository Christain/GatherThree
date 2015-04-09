package com.gather.android.constant;

import com.gather.android.BuildConfig;
import com.gather.android.manage.PhoneManage;

import java.io.File;

public final class Constant {
	
	public static final String DEFOULT_REQUEST_URL = "http://staging.app.jhla.com.cn/";// 外网数据访问地址
//	public static final String DEFOULT_REQUEST_URL = "http://app.jhla.com.cn/";// 外网数据访问地址
	public static final boolean SHOW_LOG = BuildConfig.DEBUG;// 显示log信息
	public static final String VERSION_SIZE = "5.0M";
	public static final String OFFICIAL_WEB = "http://www.jhla.com.cn";
	public static final String SHARE_QQZONE_IMAGE_URL = "http://jhla-app-icons.oss-cn-qingdao.aliyuncs.com/ic_launcher.jpg";
	public static final String BAIDU_SECRET_KEY = "L0SEOyZr8hIyeAyUdeKBpa2rtoMVDrrl";
	
	public static final String WE_CHAT_APPID = "wx325197f88ad7ba47"; //微信ID
	public static final String TENCENT_APPID = "1103292660"; //QQ登录ID
	public static final String SINA_APPID = "2247106580";	//新浪登录ID	
	public static final String SINA_CALLBACK_URL = "http://www.jhla.com.cn"; //新浪回调地址
	public static final String SINA_SCOPE = "all"; //新浪授权权限

    /**
     * 微信支付
     */
    //商户号
    public static final String MCH_ID = "1233653402";
    //  API密钥，在商户平台设置
    public static final  String WX_PAY_API_KEY="9bfe7c6249f79ab1012e726fd84d872b";
    //微信支付回调地址
    public static final String WXPAY_CALLBACK_URL_TEST = "http://test.app.jhla.com.cn/act/pay/wxpayNotifyUrl";
    public static final String WXPAY_CALLBACK_URL = "http://app.jhla.com.cn/act/pay/wxpayNotifyUrl";

	/**
	 * 支付宝
	 */
	// 商户PID
	public static final String PARTNER = "2088412900994954";
	// 商户收款账号
	public static final String SELLER = "212919377@qq.com";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJl2mq22CL3fXPuZvm3JaAwehti+ETjgvWptw3kYR9sXahxtLcXvglcTzEf2exXxd+Pf9kIomh18COMuoxqj2NVHzLkvSmflx8Q+bTNhp9Cm94fm0RhuCcGMUwjC/59cAxo0jsrVKkQe2x0epbomKBjmIiWdzsTgmbd06Wm7e1tvAgMBAAECgYAaYhSXlrsMAZGXw8fEUiLsKndeXdbSI3kNCg/YCr+Xv//DIVYEOHf9Bm9RU+O3Uwschw1sNFTCiudwPz1VQCPvysAzNW/FlNcvXoUIXhwBP+7YDnQL619zwrtvGYyb341oKGJD5Pyg61frNOjO081PHD9y3jINIZo/jhkFDn82+QJBAManRsE4yRyFWX2Rv1L7fgLXS80EonpV13qsrfntf4g0cPYUIGyg7oaF0K4Ee66MWqDO47owdZZb41lNp7yDyTUCQQDFw7lUE4F2PczBtiXNXSdnb3bCnXErxTO/6st12X5SPpepn3Hq6/JToL5G9XRwYULEsAxX4n0aliRxNXxvP8qTAkBBamIdTKLFNpBCjlUPcWuMafM3HviWCmB7sBNWQsQFSSeNkREgVFpXiAcw1p8X2nYZkCdjb4O5MKcMCEtdA5SlAkEArLyPNOOwsbG7FfveOulB0LPoPIa+YefmkpwnyLEHCBlJ6Uuuee5LBEajzGD6qmoZoqjOrzjR4xX/kK6SGK3UZwJAe8U0j9AaUHv+sYrOD3P5nMDPZjViqyAbaZGTvmp6ymXQ9XYcrFXZ8CrRQcA2q26hrmTJ6lMI4kPzS4TPJaUlsg==";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	// 支付宝回调地址
	public static final String ALIPAY_CALLBACK_URL_TEST = "http://test.app.jhla.com.cn/act/pay/alipayNotifyUrl";
	public static final String ALIPAY_CALLBACK_URL = "http://app.jhla.com.cn/act/pay/alipayNotifyUrl";
	
	public static final String IMAGE_CACHE_DIR_PATH = PhoneManage.getSdCardRootPath() + "/Gather/cache/";// 图片缓存地址
	public static final String EXCEPTION_LOG_DIR_PATH = PhoneManage.getSdCardRootPath() + "/Gather/log/";// 报告文件存放地址
	public static final String UPLOAD_FILES_DIR_PATH = PhoneManage.getSdCardRootPath() + "/Gather/upload/";
	public static final String DOWNLOAD_IMAGE_DIR_PATH = PhoneManage.getSdCardRootPath() + "/Gather/imagedown/";
	
	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
	
	public static void checkPath() {
		File dir = new File(IMAGE_CACHE_DIR_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		dir = new File(UPLOAD_FILES_DIR_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		dir = new File(DOWNLOAD_IMAGE_DIR_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		dir = new File(EXCEPTION_LOG_DIR_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

}