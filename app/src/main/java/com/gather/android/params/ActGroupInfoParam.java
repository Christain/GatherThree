package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动分组信息
 * Created by Christain on 2015/4/1.
 */
public class ActGroupInfoParam extends StringParams {

    public ActGroupInfoParam(Context context, int groupId) {
        super(context, "act/actMore/group");
        setParameter("groupId", groupId);
    }
}
