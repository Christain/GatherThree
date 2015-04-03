package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动分组成员
 * Created by Christain on 2015/4/1.
 */
public class ActGroupMemberListParam extends StringParams {

    public ActGroupMemberListParam(Context context, int cityId, int groupId, int page, int size) {
        super(context, "act/actMore/groupUsers");
        setParameter("cityId", cityId);
        setParameter("groupId", groupId);
        setParameter("page", page);
        setParameter("size", size);
    }
}
