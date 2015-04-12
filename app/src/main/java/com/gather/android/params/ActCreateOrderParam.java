package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 支付下订单
 * Created by Christain on 15/4/11.
 */
public class ActCreateOrderParam extends BaseParams{
    /**
     * @param productId 商品id
     * @param number 份数
     * @param payPlatform 支付平台：1支付宝，2微信，3银联
     */
    public ActCreateOrderParam(int productId, int number, int payPlatform) {
        super("act/pay/placeOrder");
        put("productId", productId);
        put("number", number);
        put("payPlatform", payPlatform);
    }
}
