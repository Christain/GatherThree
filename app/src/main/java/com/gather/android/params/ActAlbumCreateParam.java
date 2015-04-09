package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 创建相册
 * Created by Christain on 2015/4/2.
 */
public class ActAlbumCreateParam extends BaseParams {

    public ActAlbumCreateParam(int actId, String subject) {
        super("act/actMore/createAlbum");
        put("actId", actId);
        put("subject", subject);
    }

}
