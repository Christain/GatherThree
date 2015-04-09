package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动报名自定义字段
 * Created by Christain on 2015/3/31.
 */
public class EnrollCustomKeyParam extends BaseParams {

    public EnrollCustomKeyParam(int actId) {
        super("act/actMore/enrollCusKeys");
        put("actId", actId);
        put("page", 1);
        put("size", 30);
    }
}
