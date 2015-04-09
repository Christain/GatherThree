package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动分组成员
 * Created by Christain on 2015/4/1.
 */
public class ActGroupMemberListParam extends BaseParams {

    public ActGroupMemberListParam(int cityId, int groupId, int page, int size) {
        super("act/actMore/groupUsers");
        put("cityId", cityId);
        put("groupId", groupId);
        put("page", page);
        put("size", size);
    }
}
