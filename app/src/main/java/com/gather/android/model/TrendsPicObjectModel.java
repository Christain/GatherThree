package com.gather.android.model;

import java.io.Serializable;
import java.util.ArrayList;

public class TrendsPicObjectModel  implements Serializable {

	private int total_num;
	private ArrayList<TrendsPicModel> imgs;

	public int getTotal_num() {
		return total_num;
	}

	public void setTotal_num(int total_num) {
		this.total_num = total_num;
	}

	public ArrayList<TrendsPicModel> getImgs() {
		return imgs;
	}

	public void setImgs(ArrayList<TrendsPicModel> imgs) {
		this.imgs = imgs;
	}

}
