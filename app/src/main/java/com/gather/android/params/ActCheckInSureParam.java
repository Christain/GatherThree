package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 签到确认
 * Created by Christain on 2015/4/7.
 */
public class ActCheckInSureParam extends BaseParams {
    public ActCheckInSureParam(int checkinId) {
        super("act/actMore/sureCheckin");
        put("checkinId", checkinId);
    }
}
