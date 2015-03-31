package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动菜单列表
 * Created by Christain on 2015/3/30.
 */
public class ActMenuParam extends StringParams {

    public ActMenuParam(Context context, int actId, int type) {
        super(context, "act/actMore/menus");
        setParameter("actId", actId);
        setParameter("type", type);//类型：1午宴，2晚宴
        setParameter("page", 1);
        setParameter("size", 50);
    }
}
