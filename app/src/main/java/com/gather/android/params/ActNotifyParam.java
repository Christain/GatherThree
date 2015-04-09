package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动通知
 * Created by Christain on 2015/3/31.
 */
public class ActNotifyParam extends BaseParams {

    public ActNotifyParam(int actId) {
        super("act/actMore/notices");
        put("actId", actId);
        put("page", 1);
        put("size", 50);
    }
}
