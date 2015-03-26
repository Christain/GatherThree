package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动模块信息
 * Created by Christain on 2015/3/17.
 */
public class ActModulesStatusParam extends StringParams{
    public ActModulesStatusParam(Context context, int actId) {
        super(context, "act/actMore/modules");
        setParameter("actId", actId);
    }
}
