package com.gather.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.ActAlbumAddPhotoAdapter;
import com.gather.android.constant.Constant;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.http.AsyncHttpTask;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manage.IntentManage;
import com.gather.android.model.ActAlbumDetailModel;
import com.gather.android.params.ActAlbumCreateParam;
import com.gather.android.params.ActAlbumUploadParam;
import com.gather.android.params.UploadPicParam;
import com.gather.android.preference.AppPreference;
import com.gather.android.utils.BitmapUtils;
import com.gather.android.utils.ClickUtil;
import com.gather.android.widget.ChoosePicAlert;
import com.gather.android.widget.MMAlert;
import com.gather.android.widget.swipeback.SwipeBackActivity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 活动相册添加图片
 * Created by Christain on 2015/4/2.
 */
public class ActAlbumAddPhoto extends SwipeBackActivity implements View.OnClickListener {

    private TextView tvLeft, tvTitle, tvRight;
    private ImageView ivLeft, ivRight;

    private GridView gridView;
    private ActAlbumAddPhotoAdapter adapter;
    private ArrayList<ActAlbumDetailModel> picList = new ArrayList<ActAlbumDetailModel>();
    private LoadingDialog mLoadingDialog;
    private ArrayList<Integer> imgIdList;
    private int startIndex = 0;
    private int albumId = -1;//相册Id
    private int actId;

    /**
     * 多图
     */
    private File mPicFile;
    private static final int REQEUST_CODE_MULTIPICS_ALBUM = 103;
    private boolean isShowDialog = false, isPicChanged = false;

    @Override
    protected int layoutResId() {
        return R.layout.act_album_add_photo;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("ACT_ID")) {
            this.actId = intent.getExtras().getInt("ACT_ID");
            if (intent.hasExtra("ID")) {
                this.albumId = intent.getExtras().getInt("ID");
            }
            this.mLoadingDialog = LoadingDialog.createDialog(ActAlbumAddPhoto.this, false);
            this.tvLeft = (TextView) findViewById(R.id.tvLeft);
            this.tvRight = (TextView) findViewById(R.id.tvRight);
            this.tvTitle = (TextView) findViewById(R.id.tvTitle);
            this.ivLeft = (ImageView) findViewById(R.id.ivLeft);
            this.ivRight = (ImageView) findViewById(R.id.ivRight);
            this.tvLeft.setVisibility(View.GONE);
            this.tvRight.setVisibility(View.VISIBLE);
            this.ivLeft.setVisibility(View.VISIBLE);
            this.ivLeft.setImageResource(R.drawable.title_back_click_style);
            this.tvTitle.setText("我的相册");
            this.tvRight.setText("上传");
            this.ivRight.setVisibility(View.GONE);
            this.ivLeft.setOnClickListener(this);
            this.tvRight.setOnClickListener(this);

            this.gridView = (GridView) findViewById(R.id.gridView);
            this.adapter = new ActAlbumAddPhotoAdapter(ActAlbumAddPhoto.this);
            this.gridView.setAdapter(adapter);

            this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    if (!adapter.isFull() && position == adapter.getCount() - 1) {
                        if (!isShowDialog) {
                            isShowDialog = true;
                            showMenuDialog(1, position);
                        }
                    } else {
                        if (!isShowDialog) {
                            isShowDialog = true;
                            showMenuDialog(0, position);
                        }
                    }
                }
            });

            this.initView();
        } else {
            toast("相册数据有误...");
            finish();
        }
    }

    private void initView() {
        if (imgIdList == null) {
            imgIdList = new ArrayList<Integer>();
        }
        startIndex = 0;
        if (picList != null) {
            adapter.setUserPhotoList(picList);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivLeft:
                if (!ClickUtil.isFastClick()) {
                    finish();
                }
                break;
            case R.id.tvRight:
                if (!ClickUtil.isFastClick()) {
                    if (picList.size() == 0) {
                        toast("请先添加图片");
                        return;
                    }
                    if (!isPicChanged) {
                        toast("相册未发生任何修改");
                        return;
                    }
                    if (albumId == -1) {
                        createAlbum();
                    }
                    if (picList.size() != 0) {
                        if (startIndex != 100) {
                            uploadNewPic(startIndex);
                        } else {
                            UpdateActPhoto();
                        }
                    }
                }
                break;
        }
    }

    /**
     * 上传新增图片
     */
    private void uploadNewPic(final int index) {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setMessage("上传中...");
            mLoadingDialog.show();
        }
        UploadPicParam param = new UploadPicParam(new File(picList.get(index).getPath()));
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (imgIdList == null) {
                        imgIdList = new ArrayList<Integer>();
                    }
                    imgIdList.add(object.getInt("img_id"));
                    if (index + 1 < picList.size()) {
                        uploadNewPic(index + 1);
                    } else {
                        UpdateActPhoto();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                startIndex = index;
                toast("上传图片失败，请重试");
            }
        });
    }

    /**
     * 更新相册图片Id
     */
    private void UpdateActPhoto() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setMessage("更新中...");
            mLoadingDialog.show();
        }
        ActAlbumUploadParam param = new ActAlbumUploadParam(albumId, imgIdList);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                setResult(RESULT_OK);
                toast("上传相册成功");
                finish();
            }

            @Override
            public void onNeedLogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                startIndex = 100;
                toast("更新相册失败，请重试");
            }
        });
    }

    /**
     * 创建相册
     */
    private void createAlbum() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setMessage("上传中...");
            mLoadingDialog.show();
        }
        ActAlbumCreateParam param = new ActAlbumCreateParam(actId, AppPreference.getUserPersistent(ActAlbumAddPhoto.this, AppPreference.NICK_NAME));
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    albumId = object.getInt("id");
                    if (picList.size() != 0) {
                        if (startIndex != 100) {
                            uploadNewPic(startIndex);
                        } else {
                            UpdateActPhoto();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNeedLogin(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                needLogin(msg);
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                startIndex = 100;
                toast("上传相册失败，请重试");
            }
        });
    }

    private void showMenuDialog(int type, final int actionPosition) {
        switch (type) {
            case 0:
                MMAlert.showAlert(ActAlbumAddPhoto.this, "图片操作", new String[]{"查看图片", "删除图片"}, null, new MMAlert.OnAlertSelectId() {
                    public void onDismissed() {
                        isShowDialog = false;
                    }

                    public void onClick(int whichButton) {
                        switch (whichButton) {
                            case 0:
                                isShowDialog = false;
                                ArrayList<String> imgList = new ArrayList<String>();
                                for (int i = 0; i < adapter.getPicList().size(); i++) {
                                    if (adapter.getPicList().get(i).getId() == 0) {
                                        imgList.add(adapter.getPicList().get(i).getPath());
                                    } else {
                                        imgList.add(adapter.getPicList().get(i).getImg_url());
                                    }
                                }
                                Intent intent = new Intent(ActAlbumAddPhoto.this, PublishTrendsPicGallery.class);
                                intent.putExtra("LIST", imgList);
                                intent.putExtra("POSITION", actionPosition);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                break;
                            case 1:
                                isShowDialog = false;
                                if (adapter.getPicList().get(actionPosition).getId() == 0) {
                                    File file = new File(adapter.getPicList().get(actionPosition).getPath());
                                    if (file != null && file.exists()) {
                                        file.delete();
                                    }
                                }
                                isPicChanged = true;
                                adapter.del(actionPosition);
                                break;
                        }
                    }
                });
                break;
            case 1:
                ChoosePicAlert.showAlert(ActAlbumAddPhoto.this, "选择图片", new String[]{"相机拍照", "相册选取"}, null, new ChoosePicAlert.OnAlertSelectId() {
                    public void onDismissed() {
                        isShowDialog = false;
                    }

                    public void onClick(int whichButton) {
                        switch (whichButton) {
                            case 0:
                                isShowDialog = false;
                                mPicFile = getImageTempFile();
                                Intent intent = IntentManage.getSystemCameraIntent(mPicFile);
                                startActivityForResult(intent, IntentManage.REQUEST_CODE_TAKE_PHOTO);
                                break;
                            case 1:
                                isShowDialog = false;
                                Intent intent2 = new Intent(ActAlbumAddPhoto.this, SelectPicture.class);
                                intent2.putExtra(SelectPicture.MAX_PICS_NUM, (SelectPicture.MAX_NUM - adapter.getPicList().size()));
                                startActivityForResult(intent2, REQEUST_CODE_MULTIPICS_ALBUM);
                                break;
                        }
                    }
                });
                break;
        }
    }

    private File getImageTempFile() {
        File file = new File(Constant.UPLOAD_FILES_DIR_PATH + System.currentTimeMillis() + ".jpg");
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IntentManage.REQUEST_CODE_TAKE_PHOTO:// 拍照
                    if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
                        mLoadingDialog.setMessage("正在处理");
                        mLoadingDialog.show();
                    }
                    new CameraImageProgress().execute();
                    break;
                case REQEUST_CODE_MULTIPICS_ALBUM:
                    if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
                        mLoadingDialog.setMessage("正在处理");
                        mLoadingDialog.show();
                    }
                    String picsPath = data.getStringExtra("chosedPic");
                    new MultiImagesProgress().execute(picsPath.split(","));
                    break;
            }
        }
    }

    /**
     * 处理拍照返回图片
     */
    private class CameraImageProgress extends AsyncTask<Uri, Void, File> {

        @Override
        protected File doInBackground(Uri... params) {
            File file = null;
            try {
                Bitmap bmp = BitmapUtils.getBitmapFromFile(mPicFile, -1);
                if (bmp != null) {
                    file = getImageTempFile();
                    FileOutputStream outputStream = new FileOutputStream(file);
                    int size = (int) bmp.getRowBytes() * bmp.getHeight() / (1024 * 8);
                    boolean b = false;
                    if (size > 120) {
                        b = bmp.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                    } else {
                        b = bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    }
                    outputStream.flush();
                    outputStream.close();
                    if (!b && file != null && file.exists()) {
                        file.delete();
                        file = null;
                    }
                    bmp.recycle();
                    bmp = null;
                }
                if (mPicFile != null && mPicFile.exists()) {
                    mPicFile.delete();
                }
                mPicFile = null;
            } catch (Exception e) {
                e.printStackTrace();
                if (file != null && file.exists()) {
                    file.delete();
                }
                file = null;
            }
            return file;
        }

        @Override
        protected void onPostExecute(File result) {
            if (result != null) {
                ActAlbumDetailModel model = new ActAlbumDetailModel(result, result.getAbsolutePath());
                isPicChanged = true;
                adapter.add(model);
            } else {
                toast("图片处理出错啦！");
            }
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        }
    }

    /**
     * 相册图片返回处理
     */
    private class MultiImagesProgress extends AsyncTask<String, Void, List<ActAlbumDetailModel>> {

        @Override
        protected List<ActAlbumDetailModel> doInBackground(String... params) {
            ArrayList<ActAlbumDetailModel> list = new ArrayList<ActAlbumDetailModel>();
            File file = null;
            for (int i = 0; i < params.length; i++) {
                try {
                    file = new File(params[i]);
                    Bitmap bmp = BitmapUtils.getBitmapFromFile(file, -1);
                    if (bmp != null) {
                        file = getImageTempFile();
                        FileOutputStream outputStream = new FileOutputStream(file);
                        int size = (int) bmp.getRowBytes() * bmp.getHeight() / (1024 * 8);
                        boolean b = false;
                        if (size > 120) {
                            b = bmp.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                        } else {
                            b = bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        }
                        outputStream.flush();
                        outputStream.close();
                        if (!b && file != null && file.exists()) {
                            file.delete();
                            file = null;
                        } else {
                            ActAlbumDetailModel model = new ActAlbumDetailModel(file, file.getAbsolutePath());
                            list.add(model);
                        }
                        bmp.recycle();
                        bmp = null;
                    }
                    file = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (file != null && file.exists()) {
                        file.delete();
                    }
                }

            }
            return list;
        }

        @Override
        protected void onPostExecute(List<ActAlbumDetailModel> result) {
            isPicChanged = true;
            adapter.add(result);
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        }
    }
}
