package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 活动某一相册的图片列表
 * Created by Christain on 2015/4/1.
 */
public class ActAlbumDetailListParam extends StringParams{

    public ActAlbumDetailListParam(Context context, int albumId, int page, int size) {
        super(context, "act/actMore/photoes");
        setParameter("albumId", albumId);
        setParameter("page", page);
        setParameter("size", size);
    }
}
