package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

import java.util.ArrayList;

/**
 * 活动相册批量上传
 * Created by Christain on 2015/4/7.
 */
public class ActAlbumUploadParam extends BaseParams {
    public ActAlbumUploadParam(int albumId, ArrayList<Integer> imgList) {
        super("act/actMore/addPhoto");
        put("albumId", albumId);
        for (int i = 0; i < imgList.size(); i++) {
            put("imgIds[" + i + "]", imgList.get(i));
        }
    }
}
