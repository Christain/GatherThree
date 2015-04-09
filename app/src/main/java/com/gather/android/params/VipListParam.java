package com.gather.android.params;

import com.gather.android.baseclass.BaseParams;

/**
 * 获取达人列表
 */
public class VipListParam extends BaseParams {

	/**
	 * 分类获取达人列表
	 * @param cityId
	 * @param tagId
	 * @param sex
	 * @param userTagId
	 * @param page
	 * @param size
	 */
	public VipListParam(int cityId, int tagId, int sex, int userTagId, int page, int size) {
		super("act/vip/users");
        put("cityId", cityId);
		if (tagId != 0) {
            put("tagId", tagId);
		}
		if (sex != 0) {
            put("sex", sex);
		}
		if (userTagId != 0) {
            put("userTagId", userTagId);
		}
        put("page", page);
        put("size", size);
	}

	/**
	 * 搜索达人（根据关键字）
	 * @param cityId
	 * @param keyWords
	 */
	public VipListParam(int cityId, String keyWords, int page, int size) {
		super("act/vip/users");
        put("cityId", cityId);

		if (keyWords != null && !keyWords.equals("")) {
            put("keyWords", keyWords);
		}
        put("page", page);
        put("size", size);
	}

}
