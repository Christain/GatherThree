package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动注意事项
 * Created by Christain on 2015/3/18.
 */
public class ActAttentionParam extends StringParams{
    public ActAttentionParam(Context context, int actId) {
        super(context, "");
        setParameter("actId", actId);
    }
}
