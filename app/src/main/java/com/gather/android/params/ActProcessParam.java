package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动流程信息
 * Created by Christain on 2015/3/18.
 */
public class ActProcessParam extends BaseParams {
    public ActProcessParam(int actId, int page, int size) {
        super("act/actMore/process");
        put("actId", actId);
        put("page", page);
        put("size", size);
    }
}
