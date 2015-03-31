package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 场地平面图
 * Created by Christain on 2015/3/30.
 */
public class ActPlacePlanParam extends StringParams{

    public ActPlacePlanParam(Context context, int actId) {
        super(context, "act/actMore/placeImgs");
        setParameter("actId", actId);
        setParameter("page", 1);
        setParameter("size", 50);
    }
}
