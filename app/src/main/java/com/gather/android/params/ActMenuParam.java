package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动菜单列表
 * Created by Christain on 2015/3/30.
 */
public class ActMenuParam extends BaseParams {

    public ActMenuParam(int actId, int type) {
        super("act/actMore/menus");
        put("actId", actId);
        put("type", type);//类型：1午宴，2晚宴
        put("page", 1);
        put("size", 50);
    }
}
