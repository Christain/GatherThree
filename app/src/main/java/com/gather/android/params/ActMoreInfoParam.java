package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动更多的信息
 * Created by Christain on 2015/3/18.
 */
public class ActMoreInfoParam extends BaseParams {
    public ActMoreInfoParam(int actId) {
        super("act/actMore/moreInfo");
        put("actId", actId);
    }
}
