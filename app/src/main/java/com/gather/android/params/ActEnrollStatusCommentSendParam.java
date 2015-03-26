package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动报名情况留言发送
 * Created by Christain on 2015/3/18.
 */
public class ActEnrollStatusCommentSendParam extends StringParams{
    public ActEnrollStatusCommentSendParam(Context context, int actId, String content) {
        super(context, "act/actMore/sendMsg");
        setParameter("actId", actId);
        setParameter("content", content);
    }
}
