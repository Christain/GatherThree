package com.gather.android.model;

import java.util.ArrayList;

/**
 * Created by Christain on 2015/4/1.
 */
public class ActAlbumModel {

    private ActAlbumContentModel busi_photo;
    private ActAlbumContentModel busi_video;
    private int my_album_id;//自己的相册id：-1无
    private ArrayList<ActAlbumContentModel> albums;

    public int getMy_album_id() {
        return my_album_id;
    }

    public void setMy_album_id(int my_album_id) {
        this.my_album_id = my_album_id;
    }

    public ArrayList<ActAlbumContentModel> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<ActAlbumContentModel> albums) {
        this.albums = albums;
    }

    public ActAlbumContentModel getBusi_photo() {
        if (busi_photo != null) {
            return busi_photo;
        }
        return null;
    }

    public void setBusi_photo(ActAlbumContentModel busi_photo) {
        this.busi_photo = busi_photo;
    }

    public ActAlbumContentModel getBusi_video() {
        if (busi_video != null) {
            return busi_video;
        }
        return null;
    }

    public void setBusi_video(ActAlbumContentModel busi_video) {
        this.busi_video = busi_video;
    }
}
