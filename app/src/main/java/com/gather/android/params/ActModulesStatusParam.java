package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动模块信息
 * Created by Christain on 2015/3/17.
 */
public class ActModulesStatusParam extends BaseParams {
    public ActModulesStatusParam( int actId) {
        super("act/actMore/modules");
        put("actId", actId);
    }
}
