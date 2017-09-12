package com.go.picturechosedemo.picture;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.go.picturechosedemo.BaseFragment;
import com.go.picturechosedemo.R;
import com.go.picturechosedemo.SelectImageContract;
import com.go.picturechosedemo.SelectOptions;
import com.go.picturechosedemo.TDevice;
import com.go.picturechosedemo.picture.bean.Image;
import com.go.picturechosedemo.picture.bean.ImageFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by go on 2017/9/7.
 */

public class SelectFragment extends BaseFragment implements SelectImageContract.View, View.OnClickListener,
        ImageLoaderListener, BaseRecyclerAdapter.OnItemClickListener {


    RecyclerView mContentView;

    Button mSelectFolderView;

    ImageView mSelectFolderIcon;

    View mToolbar;

    Button mDoneView;

    Button mPreviewView;

    EmptyLayout mErrorLayout;
    private static SelectOptions mOption;
    private SelectImageContract.Operator mOperator;

    private List<Image> mSelectedImage;

    private String mCamImageName;

    private ImageAdapter mImageAdapter;
    //private ImageFolderAdapter mImageFolderAdapter;

    private LoaderListener mCursorLoader = new LoaderListener();


    private Button btn_preview;

    private Button btn_done;

    private Button btn_title_select;

    private ImageView icon_back;



    public static SelectFragment newInstance(SelectOptions options) {
        mOption = options;

        return new SelectFragment();
    }

    @Override
    public void onAttach(Context context) {
        this.mOperator = (SelectImageContract.Operator) context;
        this.mOperator.setDataView(this);
        super.onAttach(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_image;
    }


    @Override
    protected void initData() {
        if(mOption == null){
            getActivity().finish();
            return;
        }
        mSelectedImage = new ArrayList<>();

        if (mOption.getSelectCount() > 1 && mOption.getSelectedImages() != null) {
            List<String> images = mOption.getSelectedImages();
            for (String s : images) {
                // checkShare file exists
                if (s != null && new File(s).exists()) {
                    Image image = new Image();
                    image.setSelect(true);
                    image.setPath(s);
                    mSelectedImage.add(image);
                }
            }
        }
        getLoaderManager().initLoader(0, null, mCursorLoader);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
           case R.id.btn_done:
               onSelectComplete();
               break;
        }

    }

    @Override
    public void displayImage(ImageView iv, String path) {
        getImgLoader().load(path)
                .asBitmap()
                .centerCrop()
                .error(R.mipmap.ic_split_graph)
                .into(iv);

    }

    @Override
    public void onOpenCameraSuccess() {
          toOpenCamera();
    }

    @Override
    public void onCameraPermissionDenied() {

    }

    /**
     * 完成选择
     */
    public void onSelectComplete() {
        handleResult();
    }



    /**
     * 打开相机
     */
    private void toOpenCamera() {
        // 判断是否挂载了SD卡
        mCamImageName = null;
        String savePath = "";
        if (Util.hasSDCard()) {
            savePath = Util.getCameraPath();
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (TextUtils.isEmpty(savePath)) {
            Toast.makeText(getActivity(), "无法保存照片，请检查SD卡是否挂载", Toast.LENGTH_LONG).show();
            return;
        }

        mCamImageName = Util.getSaveImageFullName();
        File out = new File(savePath, mCamImageName);

        /**
         * android N 系统适配
         */
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(getContext(), "net.oschina.app.provider", out);
        } else {
            uri = Uri.fromFile(out);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent,
                0x03);
    }



    /**
     * 拍照完成通知系统添加照片
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            switch (requestCode) {
                case 0x03:
                    if (mCamImageName == null) return;
                    Uri localUri = Uri.fromFile(new File(Util.getCameraPath() + mCamImageName));
                    Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
                    getActivity().sendBroadcast(localIntent);
                    break;
                case 0x04:
                    if (data == null) return;
                    mOption.getCallback().doSelected(new String[]{data.getStringExtra("crop_path")});
                    getActivity().finish();
                    break;
            }
        }
    }



    @Override
    public void onItemClick(int position, long itemId) {
        if (mOption.isHasCam()) {
            if (position != 0) {
                handleSelectChange(position);
            } else {
                if (mSelectedImage.size() < mOption.getSelectCount()) {
                    mOperator.requestCamera();
                } else {
                    Toast.makeText(getActivity(), "最多只能选择 " + mOption.getSelectCount() + " 张图片", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            handleSelectChange(position);
        }
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mContentView = findView(R.id.rv_image);
        mSelectFolderView = findView(R.id.btn_title_select);
        mSelectFolderIcon = findView(R.id.iv_title_select);
        mToolbar = findView(R.id.toolbar);
        mDoneView = findView(R.id.btn_done);
        mPreviewView = findView(R.id.btn_preview);

        btn_preview = findView(R.id.btn_preview);
        btn_done = findView(R.id.btn_done);
        btn_title_select = findView(R.id.btn_title_select);
        icon_back = findView(R.id.icon_back);
        btn_preview.setOnClickListener(this);
        btn_done.setOnClickListener(this);
        btn_title_select.setOnClickListener(this);
        icon_back.setOnClickListener(this);
     //   mErrorLayout = findView(R.id.error_layout);

        if(mOption == null){
            getActivity().finish();
            return;
        }
        mContentView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        mContentView.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 1)));
        mImageAdapter = new ImageAdapter(getContext(), this);
        mImageAdapter.setSingleSelect(mOption.getSelectCount() <= 1);
        mRoot.findViewById(R.id.lay_button).setVisibility(mOption.getSelectCount() == 1 ? View.GONE : View.VISIBLE);
        mContentView.setAdapter(mImageAdapter);
        mContentView.setItemAnimator(null);
        mImageAdapter.setOnItemClickListener(this);
//        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
//                getLoaderManager().initLoader(0, null, mCursorLoader);
//            }
//        });

    }



    private class LoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.MINI_THUMB_MAGIC,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if(id == 0)
                return new CursorLoader(getContext(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI,IMAGE_PROJECTION,null,
                        null,IMAGE_PROJECTION[2] + " DESC");
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null){

                final ArrayList<Image> images = new ArrayList<>();
                final List<ImageFolder> imageFolders = new ArrayList<>();

                final ImageFolder defaultFolder = new ImageFolder();
                defaultFolder.setName("全部照片");
                defaultFolder.setPath("");
                imageFolders.add(defaultFolder);

                int count = data.getCount();


                if (count > 0){
                    data.moveToFirst();
                        do {
                            String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                            String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                            long date = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                            int id = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                            String thumb_magic = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                            String bucket = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));

                            Image image = new Image();
                            image.setName(name);
                            image.setId(id);
                            image.setPath(path);
                            image.setDate(date);
                            image.setThumbPath(thumb_magic);
                            image.setFolderName(bucket);

                            images.add(image);


                            if (mCamImageName != null && mCamImageName.equals(image.getName())){
                                image.setSelect(true);
                                mSelectedImage.add(image);
                            }


                            //如果是被选中的图片
                            if (mSelectedImage.size() > 0){
                                for (Image i : mSelectedImage){
                                    if (i.getPath().equals(image.getPath())) {
                                        image.setSelect(true);
                                    }
                                }
                            }


                            File imageFile = new File(path);
                            File folderFile = imageFile.getParentFile();
                            ImageFolder folder = new ImageFolder();
                            folder.setName(folderFile.getName());
                            folder.setPath(folderFile.getAbsolutePath());

                            if (!imageFolders.contains(folder)){
                                folder.getImages().add(image);
                                folder.setAlbumPath(image.getPath());//默认相册封面
                                imageFolders.add(folder);
                            }else {
                                //更新
                                ImageFolder f = imageFolders.get(imageFolders.indexOf(folder));
                                f.getImages().add(image);
                            }


                        }while (data.moveToNext());
                }

                addImagesToAdapter(images);
                defaultFolder.getImages().addAll(images);

                if (mOption.isHasCam()) {
                    defaultFolder.setAlbumPath(images.size() > 1 ? images.get(1).getPath() : null);
                } else {
                    defaultFolder.setAlbumPath(images.size() > 0 ? images.get(0).getPath() : null);
                }
              //  mImageFolderAdapter.resetItem(imageFolders);


                if (mSelectedImage.size() > 0){
                    List<Image> rs = new ArrayList<>();
                    for (Image i : mSelectedImage) {
                        File f = new File(i.getPath());
                        if (!f.exists()) {
                            rs.add(i);
                        }
                    }
                    mSelectedImage.removeAll(rs);
                }

                // If add new mCamera picture, and we only need one picture, we result it.
                if (mOption.getSelectCount() == 1 && mCamImageName != null) {
                    handleResult();
                }

                handleSelectSizeChange(mSelectedImage.size());
            //    mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);




            }

        }



        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    private void handleSelectSizeChange(int size) {
        if (size > 0) {
            mPreviewView.setEnabled(true);
            mDoneView.setEnabled(true);
            mDoneView.setText(String.format("%s(%s)", getText(R.string.image_select_opt_done), size));
        } else {
            mPreviewView.setEnabled(false);
            mDoneView.setEnabled(false);
            mDoneView.setText(getText(R.string.image_select_opt_done));
        }
    }


    private void handleSelectChange(int position) {
        Image image = mImageAdapter.getItem(position);
        if(image == null)
            return;
        //如果是多选模式
        final int selectCount = mOption.getSelectCount();
        if (selectCount > 1) {
            if (image.isSelect()) {
                image.setSelect(false);
                mSelectedImage.remove(image);
                mImageAdapter.updateItem(position);
            } else {
                if (mSelectedImage.size() == selectCount) {
                    Toast.makeText(getActivity(), "最多只能选择 " + selectCount + " 张照片", Toast.LENGTH_SHORT).show();
                } else {
                    image.setSelect(true);
                    mSelectedImage.add(image);
                    mImageAdapter.updateItem(position);
                }
            }
            handleSelectSizeChange(mSelectedImage.size());
        } else {
            mSelectedImage.add(image);
            handleResult();
        }
    }


    private void handleResult() {
        if (mSelectedImage.size() != 0) {
            if (mOption.isCrop()) {
                List<String> selectedImage = mOption.getSelectedImages();
                selectedImage.clear();
                selectedImage.add(mSelectedImage.get(0).getPath());
                mSelectedImage.clear();
             //   CropActivity.show(this, mOption);
            } else {
                mOption.getCallback().doSelected(Util.toArray(mSelectedImage));
                getActivity().finish();
            }
        }
    }


    private void addImagesToAdapter(ArrayList<Image> images) {
        mImageAdapter.clear();
        if (mOption.isHasCam()) {
            Image cam = new Image();
            mImageAdapter.addItem(cam);
        }
        mImageAdapter.addAll(images);
    }
}
