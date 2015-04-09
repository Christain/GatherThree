package com.gather.android.params;

/**
 * Created by Christain on 2015/3/27.
 */

import com.gather.android.baseclass.BaseParams;

/**
 * 活动更多地址
 */
public class ActMoreAddressParam extends BaseParams {
    public ActMoreAddressParam(int actId) {
        super("act/actMore/addrs");
        put("actId", actId);
        put("page", 1);
        put("size", 20);
    }
}
