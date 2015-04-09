package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 活动某一相册的图片列表
 * Created by Christain on 2015/4/1.
 */
public class ActAlbumDetailListParam extends BaseParams{

    public ActAlbumDetailListParam(int albumId, int page, int size) {
        super("act/actMore/photoes");
        put("albumId", albumId);
        put("page", page);
        put("size", size);
    }
}
