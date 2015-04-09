package com.gather.android.model;

import android.graphics.Bitmap;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Christain on 2015/4/1.
 */
public class ActAlbumDetailModel implements Serializable{

    private int id;//图片关联id
    private String img_url;//
    private String create_time;//
    private int status;//相册图片的状态：1已提交审核，2审核中，3已通过，4已拒绝
    private File file;
    private String path;
    private Bitmap bmp;

    public ActAlbumDetailModel(File file) {
        this.file = file;
    }

    public ActAlbumDetailModel(File file, String path) {
        this.file = file;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void destroy() {
        if (bmp != null && bmp.isRecycled()) {
            bmp.recycle();
        }
        bmp = null;
    }

    public void destroyAll() {
        if (bmp != null && bmp.isRecycled()) {
            bmp.recycle();
        }
        bmp = null;
        file.delete();
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg_url() {
        if (img_url != null) {
            return img_url;
        }
        return "";
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getCreate_time() {
        if (create_time != null) {
            return create_time;
        }
        return "";
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
