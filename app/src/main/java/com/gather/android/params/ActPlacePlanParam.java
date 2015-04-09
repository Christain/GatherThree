package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 场地平面图
 * Created by Christain on 2015/3/30.
 */
public class ActPlacePlanParam extends BaseParams {

    public ActPlacePlanParam(int actId) {
        super("act/actMore/placeImgs");
        put("actId", actId);
        put("page", 1);
        put("size", 50);
    }
}
