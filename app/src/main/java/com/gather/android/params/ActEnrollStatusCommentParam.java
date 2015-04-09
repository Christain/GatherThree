package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动报名情况留言列表
 * Created by Christain on 2015/3/18.
 */
public class ActEnrollStatusCommentParam extends BaseParams{
    public ActEnrollStatusCommentParam(int actId, int page, int size) {
        super("act/actMore/messages");
        put("actId", actId);
        put("page", page);
        put("size", size);
    }
}
