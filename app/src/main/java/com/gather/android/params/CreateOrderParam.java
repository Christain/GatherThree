package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 生成订单
 */
public class CreateOrderParam extends StringParams {

	/**
	 * @param context
	 * @param totalFee
	 *            价格
	 * @param subject
	 *            名称
	 * @param body
	 *            描述
	 */
	public CreateOrderParam(Context context, double totalFee, String subject, String body) {
		super(context, "act/pay/createOrder");
		setParameter("totalFee", totalFee);
		setParameter("subject", subject);
		setParameter("body", body);
	}

}
