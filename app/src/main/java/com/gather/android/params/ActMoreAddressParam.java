package com.gather.android.params;

/**
 * Created by Christain on 2015/3/27.
 */

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动更多地址
 */
public class ActMoreAddressParam extends StringParams{
    public ActMoreAddressParam(Context context, int actId) {
        super(context, "act/actMore/addrs");
        setParameter("actId", actId);
        setParameter("page", 1);
        setParameter("size", 20);
    }
}
