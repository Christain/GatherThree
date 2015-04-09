package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动签到的状态列表
 * Created by Christain on 2015/4/7.
 */
public class ActCheckInListParam extends BaseParams {
    public ActCheckInListParam(int actId) {
        super("act/actMore/checkins");
        put("actId", actId);
        put("page", 1);
        put("size", 10);
    }
}
