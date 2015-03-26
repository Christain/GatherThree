package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动更多的信息
 * Created by Christain on 2015/3/18.
 */
public class ActMoreInfoParam extends StringParams {
    public ActMoreInfoParam(Context context, int actId) {
        super(context, "act/actMore/moreInfo");
        setParameter("actId", actId);
    }
}
