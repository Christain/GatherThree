package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动相册列表
 * Created by Christain on 2015/4/1.
 */
public class ActAlbumListParam extends StringParams{
    public ActAlbumListParam(Context context, int actId, int cityId, int page, int size) {
        super(context, "act/actMore/albums");
        setParameter("actId", actId);
        setParameter("cityId", cityId);
        setParameter("page", page);
        setParameter("size", size);
    }
}
