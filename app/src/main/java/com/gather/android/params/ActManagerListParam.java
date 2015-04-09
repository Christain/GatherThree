package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动管理员列表
 * Created by Christain on 2015/4/1.
 */
public class ActManagerListParam extends BaseParams{

    public ActManagerListParam(int cityId, int actId, int page, int size) {
        super("act/actMore/managers");
        put("cityId", cityId);
        put("actId", actId);
        put("page", page);
        put("size", size);
    }
}
