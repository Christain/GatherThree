package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 生成订单
 */
public class CreateOrderParam extends BaseParams {

	/**
	 * @param totalFee
	 *            价格
	 * @param subject
	 *            名称
	 * @param body
	 *            描述
	 */
	public CreateOrderParam(double totalFee, String subject, String body) {
		super("act/pay/createOrder");
        put("totalFee", totalFee);
        put("subject", subject);
        put("body", body);
	}

}
