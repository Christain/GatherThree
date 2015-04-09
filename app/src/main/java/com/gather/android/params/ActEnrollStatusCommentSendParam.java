package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动报名情况留言发送
 * Created by Christain on 2015/3/18.
 */
public class ActEnrollStatusCommentSendParam extends BaseParams{
    public ActEnrollStatusCommentSendParam(int actId, String content) {
        super("act/actMore/sendMsg");
        put("actId", actId);
        put("content", content);
    }
}
