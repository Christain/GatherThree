package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动分组信息
 * Created by Christain on 2015/4/1.
 */
public class ActGroupInfoParam extends BaseParams {

    public ActGroupInfoParam(int groupId) {
        super("act/actMore/group");
        put("groupId", groupId);
    }
}
