package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动注意事项
 * Created by Christain on 2015/3/18.
 */
public class ActAttentionParam extends BaseParams {
    public ActAttentionParam(int actId) {
        super("act/actMore/attentions");
        put("actId", actId);
        put("page", 1);
        put("size", 30);
    }
}
