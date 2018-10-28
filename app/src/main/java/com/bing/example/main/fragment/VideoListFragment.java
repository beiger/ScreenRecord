package com.bing.example.main.fragment;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.jzvd.JzvdStd;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.bing.example.R;
import com.bing.example.databinding.FragmentVideoListBinding;
import com.bing.example.main.adapter.VideosAdapter;
import com.bing.example.main.viewmodel.MainViewModel;
import com.bing.example.main.viewmodel.VideoListViewModel;
import com.bing.example.model.RepositoryManager;
import com.bing.example.model.entity.VideoInfo;
import com.bing.mvvmbase.base.recycleview.BaseRecycleViewFragment;
import com.bing.mvvmbase.model.datawrapper.Status;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.io.File;
import java.util.List;

import static com.bing.example.main.adapter.VideosAdapter.MODE_NORMAL;
import static com.bing.example.main.adapter.VideosAdapter.MODE_SELECT;

public class VideoListFragment extends BaseRecycleViewFragment<FragmentVideoListBinding, VideoListViewModel, MainViewModel, VideosAdapter, VideoInfo>  {

        public VideoListFragment() {
                // Required empty public constructor
        }

        @Override
        protected void handleArguments() {

        }

        @Override
        protected int layoutId() {
                return R.layout.fragment_video_list;
        }

        @Override
        protected void initActivityViewModel() {
                mActivityViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        }

        @Override
        protected void initViewModel() {
                mViewModel = ViewModelProviders.of(this).get(VideoListViewModel.class);
        }

        @Override
        protected void reload(View view) {

        }

        @Override
        protected SmartRefreshLayout getRefreshLayout() {
                return mBinding.refreshLayout;
        }

        @Override
        protected void initRefreshLayout() {
                super.initRefreshLayout();
                getRefreshLayout().setEnableRefresh(false);
        }

        @Override
        protected void refresh(@NonNull RefreshLayout refreshLayout) {

        }

        @Override
        protected RecyclerView getRecyclerView() {
                return mBinding.recyclerView;
        }

        @Override
        protected RecyclerView.LayoutManager getLayoutManager() {
                return new GridLayoutManager(getContext(), 2);
        }

        @Override
        protected void initRecycleView() {
                super.initRecycleView();
                GridOffsetsItemDecoration offsetsItemDecoration = new GridOffsetsItemDecoration(
                                GridOffsetsItemDecoration.GRID_OFFSETS_VERTICAL);
                offsetsItemDecoration.setVerticalItemOffsets(SizeUtils.dp2px(8));
                offsetsItemDecoration.setHorizontalItemOffsets(SizeUtils.dp2px(8));
                mRecyclerView.addItemDecoration(offsetsItemDecoration);
        }

        @Override
        protected void initAdapter() {
                mAdapter = new VideosAdapter(new VideosAdapter.OnClickListener() {
                        @Override
                        public void onClickImage(int position, VideoInfo videoInfo) {
                                String fileName = videoInfo.getPath();
                                if (FileUtils.isFileExists(fileName)) {
                                        JzvdStd.startFullscreen(getContext(), JzvdStd.class, videoInfo.getPath(), videoInfo.getTitle());
                                } else {
                                        ToastUtils.showShort(R.string.file_not_exits);
                                }
                        }

                        @Override
                        public void onClickRename(int position, VideoInfo videoInfo) {
                                new LovelyTextInputDialog(getContext())
                                                .setTopColorRes(R.color.color18)
                                                .setTitle(R.string.rename)
                                                .setMessage(R.string.please_input_filename)
                                                .setIcon(R.drawable.ic_edit_black_24dp)
                                                .setConfirmButton(android.R.string.ok, text -> {
                                                        videoInfo.setTitle(text);
                                                        RepositoryManager.instance().updateVideo(videoInfo);
                                                })
                                                .show();
                        }

                        @Override
                        public void onClickDelete(int position, VideoInfo videoInfo) {
                                new LovelyStandardDialog(getContext(), LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                                                .setTopColorRes(R.color.color21)
                                                .setButtonsColorRes(R.color.color22)
                                                .setIcon(R.drawable.ic_delete_black_24dp)
                                                .setTitle(R.string.delete)
                                                .setMessage(R.string.sure_delete)
                                                .setPositiveButton(android.R.string.ok, v ->{
                                                        mViewModel.deleteVideo(videoInfo);
                                                })
                                                .setNegativeButton(android.R.string.no, null)
                                                .show();
                        }

                        @Override
                        public void onClickShare(int position, VideoInfo videoInfo) {
                                Uri uri = FileProvider.getUriForFile(getContext(), "com.bing.example.fileprovider", new File(videoInfo.getPath()));

                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);            //分享视频只能单个分享
                                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                shareIntent.setType("audio/*");
                                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)));
                        }

                        @Override
                        public void onClickFore(int position, int selectedNumber) {

                        }
                }, mode -> {
                        if (mode == MODE_NORMAL) {
                                mActivityViewModel.getIsNormalMode().set(true);
                        } else {
                                mActivityViewModel.getIsNormalMode().set(false);
                        }
                });
        }

        @Override
        protected MutableLiveData<Status> getNetworkState() {
                return mViewModel.getLoadStatus();
        }

        @Override
        protected MutableLiveData<Status> getRefreshState() {
                return mViewModel.getLoadStatus();
        }

        @Override
        protected LiveData<List<VideoInfo>> getData() {
                return mViewModel.getVideoInfos();
        }

        @Override
        protected void bindAndObserve() {
                super.bindAndObserve();
        }

        public void onClickBack() {
                mAdapter.intoNormalMode();
                mActivityViewModel.getIsNormalMode().set(true);
        }

        public void onClickDelete() {
                mViewModel.deleteVideos(mAdapter.getSelectedData());
                mAdapter.intoNormalMode();
                mActivityViewModel.getIsNormalMode().set(true);
        }

        public void onClickSelectAll() {
                if (mAdapter.getSelectedPostions().size() == mAdapter.getItemCount()) {
                        mAdapter.getSelectedPostions().clear();
                } else {
                        for (int i = 0; i < mAdapter.getData().size(); i++) {
                                mAdapter.getSelectedPostions().add(i);
                        }
                }
                mAdapter.notifyDataSetChanged();
        }

        public boolean onBackPressed() {
                if (mAdapter.getCurrentMode() == MODE_SELECT) {
                        mAdapter.intoNormalMode();
                        return true;
                } else {
                        return false;
                }
        }

        public void intoNormalMode() {
                if (mAdapter.getCurrentMode() == MODE_SELECT) {
                        mAdapter.intoNormalMode();
                }
        }
}
