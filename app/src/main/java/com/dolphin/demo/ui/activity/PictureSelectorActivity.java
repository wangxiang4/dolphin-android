package com.dolphin.demo.ui.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dolphin.core.base.BaseActivity;
import com.dolphin.core.entity.OssFile;
import com.dolphin.core.util.ToastUtil;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.ActivityPictureSelectorBinding;
import com.dolphin.demo.engine.ExoPlayerEngine;
import com.dolphin.demo.engine.GlideEngine;
import com.dolphin.demo.ui.adapter.PictureSelectorRecyclerAdapter;
import com.dolphin.demo.ui.vm.PictureSelectorViewModel;
import com.dolphin.demo.util.ImageLoaderUtil;
import com.luck.lib.camerax.SimpleCameraX;
import com.luck.lib.camerax.listener.OnSimpleXPermissionDeniedListener;
import com.luck.lib.camerax.listener.OnSimpleXPermissionDescriptionListener;
import com.luck.lib.camerax.permissions.SimpleXPermissionUtil;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.basic.PictureSelectionModel;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectLimitType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.dialog.RemindDialog;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.engine.VideoPlayerEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.interfaces.OnCameraInterceptListener;
import com.luck.picture.lib.interfaces.OnExternalPreviewEventListener;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnMediaEditInterceptListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.interfaces.OnSelectLimitTipsListener;
import com.luck.picture.lib.language.LanguageConfig;
import com.luck.picture.lib.permissions.PermissionConfig;
import com.luck.picture.lib.style.BottomNavBarStyle;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.style.TitleBarStyle;
import com.luck.picture.lib.utils.DateUtils;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.MediaUtils;
import com.luck.picture.lib.utils.PictureFileUtils;
import com.luck.picture.lib.utils.StyleUtils;
import com.luck.picture.lib.widget.MediumBoldTextView;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropImageEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 *<p>
 * 相册选择器
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/9/29
 */
public class PictureSelectorActivity extends BaseActivity<ActivityPictureSelectorBinding, PictureSelectorViewModel> implements PictureSelectorRecyclerAdapter.EventListener {

    private RecyclerView recyclerView;
    private Button uploadButton;
    public PictureSelectorRecyclerAdapter mAdapter;
    private ImageEngine imageEngine;
    private VideoPlayerEngine videoPlayerEngine;
    private PictureSelectorStyle selectorStyle;
    private int chooseMode = SelectMimeType.ofAll();
    private int aspect_ratio_x = -1, aspect_ratio_y = -1;
    private final String TAG_EXPLAIN_VIEW = "TAG_EXPLAIN_VIEW";

    @Override
    public int setContentView(Bundle savedInstanceState) {
        return R.layout.activity_picture_selector;
    }

    @Override
    public int setVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.mViewModel.mActivity = this;
        mAdapter = new PictureSelectorRecyclerAdapter(CollectionUtils.newArrayList());
        List<OssFile> localMedia = getIntent().getParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION);
        if (CollectionUtils.isNotEmpty(localMedia)) {
            mAdapter.refresh(localMedia);
        }
        setResult(RESULT_OK, new Intent().putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION, mAdapter.getData()));

        recyclerView = findViewById(R.id.picture_selector_recycler_view);
        uploadButton = findViewById(R.id.btn_upload);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if (itemAnimator != null) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }

        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, DensityUtil.dip2px(getContext(), 8), false));
        recyclerView.setAdapter(mAdapter);

        imageEngine = GlideEngine.createGlideEngine();
        videoPlayerEngine = new ExoPlayerEngine();
        selectorStyle = new PictureSelectorStyle();

        // 微信图片选择主体风格
        SelectMainStyle numberSelectMainStyle = new SelectMainStyle();
        numberSelectMainStyle.setSelectNumberStyle(true);
        numberSelectMainStyle.setPreviewSelectNumberStyle(false);
        numberSelectMainStyle.setPreviewDisplaySelectGallery(true);
        numberSelectMainStyle.setSelectBackground(R.drawable.ps_default_num_selector);
        numberSelectMainStyle.setPreviewSelectBackground(R.drawable.ps_preview_checkbox_selector);
        numberSelectMainStyle.setSelectNormalBackgroundResources(R.drawable.ps_select_complete_normal_bg);
        numberSelectMainStyle.setSelectNormalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_53575e));
        numberSelectMainStyle.setSelectNormalText(getString(R.string.ps_send));
        numberSelectMainStyle.setAdapterPreviewGalleryBackgroundResource(R.drawable.ps_preview_gallery_bg);
        numberSelectMainStyle.setAdapterPreviewGalleryItemSize(DensityUtil.dip2px(getContext(), 52));
        numberSelectMainStyle.setPreviewSelectText(getString(R.string.ps_select));
        numberSelectMainStyle.setPreviewSelectTextSize(14);
        numberSelectMainStyle.setPreviewSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
        numberSelectMainStyle.setPreviewSelectMarginRight(DensityUtil.dip2px(getContext(), 6));
        numberSelectMainStyle.setSelectBackgroundResources(R.drawable.ps_select_complete_bg);
        numberSelectMainStyle.setSelectText(getString(R.string.ps_send_num));
        numberSelectMainStyle.setSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
        numberSelectMainStyle.setMainListBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_black));
        numberSelectMainStyle.setCompleteSelectRelativeTop(true);
        numberSelectMainStyle.setPreviewSelectRelativeBottom(true);
        numberSelectMainStyle.setAdapterItemIncludeEdge(false);

        // 头部TitleBar 风格
        TitleBarStyle numberTitleBarStyle = new TitleBarStyle();
        numberTitleBarStyle.setHideCancelButton(true);
        numberTitleBarStyle.setAlbumTitleRelativeLeft(true);
        numberTitleBarStyle.setTitleAlbumBackgroundResource(R.drawable.ps_album_bg);
        numberTitleBarStyle.setTitleDrawableRightResource(R.drawable.ps_ic_grey_arrow);
        numberTitleBarStyle.setPreviewTitleLeftBackResource(R.drawable.ps_ic_normal_back);

        // 底部NavBar 风格
        BottomNavBarStyle numberBottomNavBarStyle = new BottomNavBarStyle();
        numberBottomNavBarStyle.setBottomPreviewNarBarBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_half_grey));
        numberBottomNavBarStyle.setBottomPreviewNormalText(getString(R.string.ps_preview));
        numberBottomNavBarStyle.setBottomPreviewNormalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_9b));
        numberBottomNavBarStyle.setBottomPreviewNormalTextSize(16);
        numberBottomNavBarStyle.setCompleteCountTips(false);
        numberBottomNavBarStyle.setBottomPreviewSelectText(getString(R.string.ps_preview_num));
        numberBottomNavBarStyle.setBottomPreviewSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));

        // 相册上弹动画
        PictureWindowAnimationStyle animationStyle = new PictureWindowAnimationStyle();
        animationStyle.setActivityEnterAnimation(R.anim.ps_anim_up_in);
        animationStyle.setActivityExitAnimation(R.anim.ps_anim_down_out);

        selectorStyle.setWindowAnimationStyle(animationStyle);
        selectorStyle.setTitleBarStyle(numberTitleBarStyle);
        selectorStyle.setBottomBarStyle(numberBottomNavBarStyle);
        selectorStyle.setSelectMainStyle(numberSelectMainStyle);

        uploadButton.setOnClickListener(view -> {
            PictureSelectionModel selectionModel = PictureSelector.create(getContext())
                    .openGallery(chooseMode)
                    .setSelectorUIStyle(selectorStyle)
                    .setImageEngine(imageEngine)
                    .setLanguage(LanguageConfig.SYSTEM_LANGUAGE)
                    .setVideoPlayerEngine(videoPlayerEngine)
                    .setCompressEngine(new ImageFileCompressEngine())
                    // 引入微信相机
                    .setCameraInterceptListener(new MeOnCameraInterceptListener())
                    .setSelectLimitTipsListener(new MeOnSelectLimitTipsListener())
                    .setEditMediaInterceptListener(new MeOnMediaEditInterceptListener(getSandboxPath(), buildOptions()))
                    .isAutoVideoPlay(true)
                    .isLoopAutoVideoPlay(false)
                    .isPageSyncAlbumCount(true)
                    .setQuerySortOrder(MediaStore.MediaColumns.DATE_MODIFIED + " ASC")
                    .isDisplayTimeAxis(true)
                    .isPageStrategy(true)
                    .isOriginalControl(true)
                    .isOpenClickSound(true)
                    // 跳过裁剪的资源类型
                    .setSkipCropMimeType(new String[]{PictureMimeType.ofGIF(), PictureMimeType.ofWEBP()})
                    .isFastSlidingSelect(true)
                    .isWithSelectVideoImage(true)
                    .isPreviewFullScreenMode(true)
                    .isVideoPauseResumePlay(true)
                    .isPreviewZoomEffect(true)
                    .isPreviewImage(true)
                    .isPreviewVideo(true)
                    .isPreviewAudio(true)
                    // 设置选中动画
                    .setGridItemSelectAnimListener((view1, isSelected) -> {
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(
                                ObjectAnimator.ofFloat(view1, "scaleX", isSelected ? 1F : 1.12F, isSelected ? 1.12f : 1.0F),
                                ObjectAnimator.ofFloat(view1, "scaleY", isSelected ? 1F : 1.12F, isSelected ? 1.12f : 1.0F)
                        );
                        set.setDuration(350);
                        set.start();
                    })
                    .setSelectAnimListener(view12 -> {
                        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.ps_anim_modal_in);
                        view12.startAnimation(animation);
                        return animation.getDuration();
                    })
                    .isGif(true)
                    .setMaxSelectNum(10)
                    .setMaxVideoSelectNum(2)
                    .setRecyclerAnimationMode(AnimationType.SLIDE_IN_BOTTOM_ANIMATION);

            forSelectResult(selectionModel);
        });
    }

    @Override
    public void onItemViewClicked(View v, int position) {
        // 转换ArrayList<LocalMedia>
        ArrayList<LocalMedia> list = CollectionUtils.newArrayList();
        list.addAll(mAdapter.getData().stream().map(item -> LocalMedia.generateHttpAsLocalMedia(item.getAvailablePath())).collect(Collectors.toList()));
        // 预览图片、视频、音频
        PictureSelector.create(PictureSelectorActivity.this)
                .openPreview()
                .setLanguage(LanguageConfig.SYSTEM_LANGUAGE)
                .setImageEngine(imageEngine)
                .setVideoPlayerEngine(videoPlayerEngine)
                .setSelectorUIStyle(selectorStyle)
                .isAutoVideoPlay(true)
                .isLoopAutoVideoPlay(false)
                .isPreviewFullScreenMode(true)
                .isVideoPauseResumePlay(true)
                .isPreviewZoomEffect(chooseMode != SelectMimeType.ofAudio(), recyclerView)
                .setExternalPreviewEventListener(new ExternalPreviewEventListener())
                .startActivityPreview(position, true, list);
    }

    @Override
    public void onItemViewLongClicked(PictureSelectorRecyclerAdapter.ViewHolder viewHolder, int position, View v) {

    }

    /** 处理相册选择回调 */
    private void forSelectResult(PictureSelectionModel model) {
        model.forResult(new MeOnResultCallbackListener());
    }

    /** 选择结果回调拦截监听 */
    private class MeOnResultCallbackListener implements OnResultCallbackListener<LocalMedia> {
        @Override
        public void onResult(ArrayList<LocalMedia> result) {
            analyticalSelectResults(result);
        }

        @Override
        public void onCancel() {
            LogUtils.i("PictureSelector Cancel");
        }
    }

    /** 自定义编辑拦截监听 */
    private class MeOnMediaEditInterceptListener  implements OnMediaEditInterceptListener {
        private final String outputCropPath;
        private final UCrop.Options options;

        public MeOnMediaEditInterceptListener(String outputCropPath, UCrop.Options options) {
            this.outputCropPath = outputCropPath;
            this.options = options;
        }

        @Override
        public void onStartMediaEdit(Fragment fragment, LocalMedia currentLocalMedia, int requestCode) {
            String currentEditPath = currentLocalMedia.getAvailablePath();
            Uri inputUri = PictureMimeType.isContent(currentEditPath)
                    ? Uri.parse(currentEditPath) : Uri.fromFile(new File(currentEditPath));
            Uri destinationUri = Uri.fromFile(
                    new File(outputCropPath, DateUtils.getCreateFileName("CROP_") + ".jpeg"));
            UCrop uCrop = UCrop.of(inputUri, destinationUri);
            options.setHideBottomControls(false);
            uCrop.withOptions(options);
            uCrop.setImageEngine(new UCropImageEngine() {
                @Override
                public void loadImage(Context context, String url, ImageView imageView) {
                    if (!ImageLoaderUtil.assertValidRequest(context)) {
                        return;
                    }
                    Glide.with(context).load(url).override(180, 180).into(imageView);
                }

                @Override
                public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
                    Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if (call != null) {
                                call.onCall(resource);
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            if (call != null) {
                                call.onCall(null);
                            }
                        }
                    });
                }
            });
            uCrop.startEdit(fragment.requireActivity(), fragment, requestCode);
        }
    }

    /** 自定义微信相机拦截监听 */
    private class MeOnCameraInterceptListener implements OnCameraInterceptListener {

        @Override
        public void openCamera(Fragment fragment, int cameraMode, int requestCode) {
            SimpleCameraX camera = SimpleCameraX.of();
            camera.isAutoRotation(true);
            camera.setCameraMode(cameraMode);
            camera.setVideoFrameRate(25);
            camera.setVideoBitRate(3 * 1024 * 1024);
            camera.isDisplayRecordChangeTime(true);
            camera.isManualFocusCameraPreview(true);
            camera.isZoomCameraPreview(true);
            camera.setPermissionDeniedListener(new MeOnSimpleXPermissionDeniedListener());
            camera.setPermissionDescriptionListener(new MeOnSimpleXPermissionDescriptionListener());
            camera.setImageEngine((context, url, imageView) -> Glide.with(context).load(url).into(imageView));
            camera.start(fragment.requireActivity(), fragment, requestCode);
        }
    }

    /** 选择限制拦截监听 */
    private static class MeOnSelectLimitTipsListener implements OnSelectLimitTipsListener {
        @Override
        public boolean onSelectLimitTips(Context context, @Nullable LocalMedia media, PictureSelectionConfig config, int limitType) {
            if (limitType == SelectLimitType.SELECT_NOT_SUPPORT_SELECT_LIMIT) {
                ToastUtil.show("暂不支持的选择类型");
                return true;
            }
            return false;
        }
    }

    /** 微信相机权限描述拒绝拦截监听 */
    private class MeOnSimpleXPermissionDeniedListener implements OnSimpleXPermissionDeniedListener {

        @Override
        public void onDenied(Context context, String permission, int requestCode) {
            String tips;
            if (TextUtils.equals(permission, Manifest.permission.RECORD_AUDIO)) {
                tips = "缺少麦克风权限\n可能会导致录视频无法采集声音";
            } else {
                tips = "缺少相机权限\n可能会导致不能使用摄像头功能";
            }
            RemindDialog dialog = RemindDialog.buildDialog(context, tips);
            dialog.setButtonText("去设置");
            dialog.setButtonTextColor(0xFF7D7DFF);
            dialog.setContentTextColor(0xFF333333);
            dialog.setOnDialogClickListener(view -> {
                SimpleXPermissionUtil.goIntentSetting((Activity) context, requestCode);
                dialog.dismiss();
            });
            dialog.show();
        }
    }

    /** 微信相机权限描述添加移除拦截监听 */
    private class MeOnSimpleXPermissionDescriptionListener implements OnSimpleXPermissionDescriptionListener {

        @Override
        public void onPermissionDescription(Context context, ViewGroup viewGroup, String permission) {
            addPermissionDescription(viewGroup, new String[]{permission});
        }

        @Override
        public void onDismiss(ViewGroup viewGroup) {
            removePermissionDescription(viewGroup);
        }
    }

    /** 添加权限描述 */
    private void addPermissionDescription(ViewGroup viewGroup, String[] permissionArray) {
        int dp10 = DensityUtil.dip2px(viewGroup.getContext(), 10);
        int dp15 = DensityUtil.dip2px(viewGroup.getContext(), 15);
        MediumBoldTextView view = new MediumBoldTextView(viewGroup.getContext());
        view.setTag(TAG_EXPLAIN_VIEW);
        view.setTextSize(14);
        view.setTextColor(Color.parseColor("#333333"));
        view.setPadding(dp10, dp15, dp10, dp15);

        String title;
        String explain;

        if (TextUtils.equals(permissionArray[0], PermissionConfig.CAMERA[0])) {
            title = "相机权限使用说明";
            explain = "相机权限使用说明\n用户app用于拍照/录视频";
        } else if (TextUtils.equals(permissionArray[0], Manifest.permission.RECORD_AUDIO)) {
            title = "麦克风权限使用说明";
            explain = "麦克风权限使用说明\n用户app用于录视频时采集声音";
        } else {
            title = "存储权限使用说明";
            explain = "存储权限使用说明\n用户app写入/下载/保存/读取/修改/删除图片、视频、文件等信息";
        }
        int startIndex = 0;
        int endOf = startIndex + title.length();
        SpannableStringBuilder builder = new SpannableStringBuilder(explain);
        builder.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(viewGroup.getContext(), 16)), startIndex, endOf, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(0xFF333333), startIndex, endOf, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        view.setText(builder);
        view.setBackground(ContextCompat.getDrawable(viewGroup.getContext(), R.drawable.icon_privacy_white_bg));

        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = DensityUtil.getStatusBarHeight(viewGroup.getContext());
        layoutParams.leftMargin = dp10;
        layoutParams.rightMargin = dp10;
        viewGroup.addView(view, layoutParams);

    }

    /** 移除权限描述 */
    private void removePermissionDescription(ViewGroup viewGroup) {
        View tagExplainView = viewGroup.findViewWithTag(TAG_EXPLAIN_VIEW);
        viewGroup.removeView(tagExplainView);
    }

    /** 分析选择结果处理 */
    private void analyticalSelectResults(ArrayList<LocalMedia> result) {
        runOnUiThread(() -> {
            for (LocalMedia media : result) {
                if (media.getWidth() == 0 || media.getHeight() == 0) {
                    if (PictureMimeType.isHasImage(media.getMimeType())) {
                        MediaExtraInfo imageExtraInfo = MediaUtils.getImageSize(getContext(), media.getPath());
                        media.setWidth(imageExtraInfo.getWidth());
                        media.setHeight(imageExtraInfo.getHeight());
                    } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                        MediaExtraInfo videoExtraInfo = MediaUtils.getVideoSize(getContext(), media.getPath());
                        media.setWidth(videoExtraInfo.getWidth());
                        media.setHeight(videoExtraInfo.getHeight());
                    }
                }
                LogUtils.i("文件名: " + media.getFileName());
                LogUtils.i("是否压缩:" + media.isCompressed());
                LogUtils.i("压缩:" + media.getCompressPath());
                LogUtils.i("初始路径:" + media.getPath());
                LogUtils.i("绝对路径:" + media.getRealPath());
                LogUtils.i("是否裁剪:" + media.isCut());
                LogUtils.i("裁剪路径:" + media.getCutPath());
                LogUtils.i("是否开启原图:" + media.isOriginal());
                LogUtils.i("原图路径:" + media.getOriginalPath());
                LogUtils.i("沙盒路径:" + media.getSandboxPath());
                LogUtils.i("水印路径:" + media.getWatermarkPath());
                LogUtils.i("视频缩略图:" + media.getVideoThumbnailPath());
                LogUtils.i("原始宽高: " + media.getWidth() + "x" + media.getHeight());
                LogUtils.i("裁剪宽高: " + media.getCropImageWidth() + "x" + media.getCropImageHeight());
                LogUtils.i("文件大小: " + PictureFileUtils.formatAccurateUnitFileSize(media.getSize()));
                LogUtils.i("文件时长: " + media.getDuration());
                mViewModel.uploadFile(media);
            }
        });
    }

    /** 图片文件引擎 */
    private class ImageFileCompressEngine implements CompressFileEngine {
        @Override
        public void onStartCompress(Context context, ArrayList<Uri> source, OnKeyValueResultCallbackListener call) {
            Luban.with(context).load(source).ignoreBy(100).setRenameListener(new OnRenameListener() {
                @Override
                public String rename(String filePath) {
                    int indexOf = filePath.lastIndexOf(".");
                    String postfix = indexOf != -1 ? filePath.substring(indexOf) : ".jpg";
                    return DateUtils.getCreateFileName("CMP_") + postfix;
                }
            }).setCompressListener(new OnNewCompressListener() {
                @Override
                public void onStart() {}

                @Override
                public void onSuccess(String source, File compressFile) {
                    if (call != null) {
                        call.onCallback(source, compressFile.getAbsolutePath());
                    }
                }

                @Override
                public void onError(String source, Throwable e) {
                    if (call != null) {
                        call.onCallback(source, null);
                    }
                }
            }).launch();
        }
    }

    /** 图片裁剪初始化参数 */
    private UCrop.Options buildOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setShowCropFrame(true);
        options.setShowCropGrid(true);
        options.withAspectRatio(aspect_ratio_x, aspect_ratio_y);
        options.setCropOutputPathDir(getSandboxPath());
        options.isCropDragSmoothToCenter(false);
        options.isForbidSkipMultipleCrop(false);
        options.setMaxScaleMultiplier(100);
        // 设置沉浸式状态栏
        if (selectorStyle != null && selectorStyle.getSelectMainStyle().getStatusBarColor() != 0) {
            SelectMainStyle mainStyle = selectorStyle.getSelectMainStyle();
            boolean isDarkStatusBarBlack = mainStyle.isDarkStatusBarBlack();
            int statusBarColor = mainStyle.getStatusBarColor();
            options.isDarkStatusBarBlack(isDarkStatusBarBlack);
            if (StyleUtils.checkStyleValidity(statusBarColor)) {
                options.setStatusBarColor(statusBarColor);
                options.setToolbarColor(statusBarColor);
            } else {
                options.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
                options.setToolbarColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
            }
            TitleBarStyle titleBarStyle = selectorStyle.getTitleBarStyle();
            if (StyleUtils.checkStyleValidity(titleBarStyle.getTitleTextColor())) {
                options.setToolbarWidgetColor(titleBarStyle.getTitleTextColor());
            } else {
                options.setToolbarWidgetColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
            }
        } else {
            options.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
            options.setToolbarColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
            options.setToolbarWidgetColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
        }
        return options;
    }

    /** 自定义预览额外操作事件监听 */
    private class ExternalPreviewEventListener implements OnExternalPreviewEventListener {

        @Override
        public void onPreviewDelete(int position) {
            mAdapter.delete(position);
        }

        @Override
        public boolean onLongPressDownload(LocalMedia media) {
            return false;
        }
    }

    /** 获取沙盒路径 */
    private String getSandboxPath() {
        File externalFilesDir = getContext().getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Sandbox");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private PictureSelectorActivity getContext() {
        return this;
    }

}
