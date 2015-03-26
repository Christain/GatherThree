package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动报名情况留言列表
 * Created by Christain on 2015/3/18.
 */
public class ActEnrollStatusCommentParam extends StringParams{
    public ActEnrollStatusCommentParam(Context context, int actId, int page, int size) {
        super(context, "act/actMore/messages");
        setParameter("actId", actId);
        setParameter("page", page);
        setParameter("size", size);
    }
}
