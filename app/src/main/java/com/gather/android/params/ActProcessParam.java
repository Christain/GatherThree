package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动流程信息
 * Created by Christain on 2015/3/18.
 */
public class ActProcessParam extends StringParams {
    public ActProcessParam(Context context, int actId, int page, int size) {
        super(context, "act/actMore/process");
        setParameter("actId", actId);
        setParameter("page", page);
        setParameter("size", size);
    }
}
