package com.gather.android.params;

import android.content.Context;

import com.gather.android.baseclass.StringParams;

/**
 * 创建相册
 * Created by Christain on 2015/4/2.
 */
public class ActAlbumCreateParam extends StringParams {

    public ActAlbumCreateParam(Context context, int actId, String subject) {
        super(context, "act/actMore/createAlbum");
        setParameter("actId", actId);
        setParameter("subject", subject);
    }

}
