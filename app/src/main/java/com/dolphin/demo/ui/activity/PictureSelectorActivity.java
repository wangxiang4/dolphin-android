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
 * ???????????????
 *</p>
 *
 * @Author: entfrm????????????-??????
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
        mAdapter.setEventListener(this);
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

        // ??????????????????????????????
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

        // ??????TitleBar ??????
        TitleBarStyle numberTitleBarStyle = new TitleBarStyle();
        numberTitleBarStyle.setHideCancelButton(true);
        numberTitleBarStyle.setAlbumTitleRelativeLeft(true);
        numberTitleBarStyle.setTitleAlbumBackgroundResource(R.drawable.ps_album_bg);
        numberTitleBarStyle.setTitleDrawableRightResource(R.drawable.ps_ic_grey_arrow);
        numberTitleBarStyle.setPreviewTitleLeftBackResource(R.drawable.ps_ic_normal_back);

        // ??????NavBar ??????
        BottomNavBarStyle numberBottomNavBarStyle = new BottomNavBarStyle();
        numberBottomNavBarStyle.setBottomPreviewNarBarBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_half_grey));
        numberBottomNavBarStyle.setBottomPreviewNormalText(getString(R.string.ps_preview));
        numberBottomNavBarStyle.setBottomPreviewNormalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_9b));
        numberBottomNavBarStyle.setBottomPreviewNormalTextSize(16);
        numberBottomNavBarStyle.setCompleteCountTips(false);
        numberBottomNavBarStyle.setBottomPreviewSelectText(getString(R.string.ps_preview_num));
        numberBottomNavBarStyle.setBottomPreviewSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));

        // ??????????????????
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
                    // ??????????????????
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
                    // ???????????????????????????
                    .setSkipCropMimeType(new String[]{PictureMimeType.ofGIF(), PictureMimeType.ofWEBP()})
                    .isFastSlidingSelect(true)
                    .isWithSelectVideoImage(true)
                    .isPreviewFullScreenMode(true)
                    .isVideoPauseResumePlay(true)
                    .isPreviewZoomEffect(true)
                    .isPreviewImage(true)
                    .isPreviewVideo(true)
                    .isPreviewAudio(true)
                    // ??????????????????
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
        // ??????ArrayList<LocalMedia>
        ArrayList<LocalMedia> list = CollectionUtils.newArrayList();
        list.addAll(mAdapter.getData().stream().map(item -> LocalMedia.generateHttpAsLocalMedia(item.getAvailablePath())).collect(Collectors.toList()));
        // ??????????????????????????????
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

    /** ???????????????????????? */
    private void forSelectResult(PictureSelectionModel model) {
        model.forResult(new MeOnResultCallbackListener());
    }

    /** ?????????????????????????????? */
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

    /** ??????????????????????????? */
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

    /** ????????????????????????????????? */
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

    /** ???????????????????????? */
    private static class MeOnSelectLimitTipsListener implements OnSelectLimitTipsListener {
        @Override
        public boolean onSelectLimitTips(Context context, @Nullable LocalMedia media, PictureSelectionConfig config, int limitType) {
            if (limitType == SelectLimitType.SELECT_NOT_SUPPORT_SELECT_LIMIT) {
                ToastUtil.show("???????????????????????????");
                return true;
            }
            return false;
        }
    }

    /** ?????????????????????????????????????????? */
    private class MeOnSimpleXPermissionDeniedListener implements OnSimpleXPermissionDeniedListener {

        @Override
        public void onDenied(Context context, String permission, int requestCode) {
            String tips;
            if (TextUtils.equals(permission, Manifest.permission.RECORD_AUDIO)) {
                tips = "?????????????????????\n??????????????????????????????????????????";
            } else {
                tips = "??????????????????\n??????????????????????????????????????????";
            }
            RemindDialog dialog = RemindDialog.buildDialog(context, tips);
            dialog.setButtonText("?????????");
            dialog.setButtonTextColor(0xFF7D7DFF);
            dialog.setContentTextColor(0xFF333333);
            dialog.setOnDialogClickListener(view -> {
                SimpleXPermissionUtil.goIntentSetting((Activity) context, requestCode);
                dialog.dismiss();
            });
            dialog.show();
        }
    }

    /** ???????????????????????????????????????????????? */
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

    /** ?????????????????? */
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
            title = "????????????????????????";
            explain = "????????????????????????\n??????app????????????/?????????";
        } else if (TextUtils.equals(permissionArray[0], Manifest.permission.RECORD_AUDIO)) {
            title = "???????????????????????????";
            explain = "???????????????????????????\n??????app??????????????????????????????";
        } else {
            title = "????????????????????????";
            explain = "????????????????????????\n??????app??????/??????/??????/??????/??????/???????????????????????????????????????";
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

    /** ?????????????????? */
    private void removePermissionDescription(ViewGroup viewGroup) {
        View tagExplainView = viewGroup.findViewWithTag(TAG_EXPLAIN_VIEW);
        viewGroup.removeView(tagExplainView);
    }

    /** ???????????????????????? */
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
                LogUtils.i("?????????: " + media.getFileName());
                LogUtils.i("????????????:" + media.isCompressed());
                LogUtils.i("??????:" + media.getCompressPath());
                LogUtils.i("????????????:" + media.getPath());
                LogUtils.i("????????????:" + media.getRealPath());
                LogUtils.i("????????????:" + media.isCut());
                LogUtils.i("????????????:" + media.getCutPath());
                LogUtils.i("??????????????????:" + media.isOriginal());
                LogUtils.i("????????????:" + media.getOriginalPath());
                LogUtils.i("????????????:" + media.getSandboxPath());
                LogUtils.i("????????????:" + media.getWatermarkPath());
                LogUtils.i("???????????????:" + media.getVideoThumbnailPath());
                LogUtils.i("????????????: " + media.getWidth() + "x" + media.getHeight());
                LogUtils.i("????????????: " + media.getCropImageWidth() + "x" + media.getCropImageHeight());
                LogUtils.i("????????????: " + PictureFileUtils.formatAccurateUnitFileSize(media.getSize()));
                LogUtils.i("????????????: " + media.getDuration());
                mViewModel.uploadFile(media);
            }
        });
    }

    /** ?????????????????? */
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

    /** ??????????????????????????? */
    private UCrop.Options buildOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setShowCropFrame(true);
        options.setShowCropGrid(true);
        options.withAspectRatio(aspect_ratio_x, aspect_ratio_y);
        options.setCropOutputPathDir(getSandboxPath());
        options.isCropDragSmoothToCenter(false);
        options.isForbidSkipMultipleCrop(false);
        options.setMaxScaleMultiplier(100);
        // ????????????????????????
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

    /** ??????????????????????????????????????? */
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

    /** ?????????????????? */
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
