package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动相册列表
 * Created by Christain on 2015/4/1.
 */
public class ActAlbumListParam extends BaseParams{
    public ActAlbumListParam(int actId, int cityId, int page, int size) {
        super("act/actMore/albums");
        put("actId", actId);
        put("cityId", cityId);
        put("page", page);
        put("size", size);
    }
}
