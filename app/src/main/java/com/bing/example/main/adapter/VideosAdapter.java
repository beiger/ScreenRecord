package com.bing.example.main.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.bing.example.R;
import com.bing.example.databinding.ItemVideoBinding;
import com.bing.example.model.entity.VideoInfo;
import com.bing.mvvmbase.base.recycleview.BaseRecycleViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import androidx.annotation.NonNull;

public class VideosAdapter extends BaseRecycleViewAdapter<VideoInfo, VideoInfoHolder> {
        public static final int MODE_NORMAL = 0;
        public static final int MODE_SELECT = 1;

        private int mCurrentMode = MODE_NORMAL;

	private OnClickListener mListener;
	private OnModeChangeListener mModeChangeListener;

        private Set<Integer> mSelectedPostions = new TreeSet<>();

        public Set<Integer> getSelectedPostions() {
                return mSelectedPostions;
        }

        public List<VideoInfo> getSelectedData() {
                List<VideoInfo> infos = new ArrayList<>();
                for (Integer position : mSelectedPostions) {
                        infos.add(mData.get(position));
                }
                return infos;
        }

        public VideosAdapter(OnClickListener listener, OnModeChangeListener modeChangeListener) {
		mListener = listener;
		mModeChangeListener = modeChangeListener;
	}

        @Override
        protected VideoInfoHolder createHolder(@NonNull ViewGroup viewGroup, int i) {
                return new VideoInfoHolder((ItemVideoBinding)getBinding(viewGroup, i, R.layout.item_video));
        }

        @Override
        protected void bindData(VideoInfoHolder holder, int position) {
                ItemVideoBinding binding = holder.getBinding();
                binding.setVideoInfo(mData.get(position));
                binding.setMode(mCurrentMode);
                binding.setSelected(mSelectedPostions.contains(position));
                binding.backContent.setOnLongClickListener(v -> {
                        intoMutiSelectMode(position);
                        return true;
                });
                binding.thumb.setOnLongClickListener(v -> {
                        intoMutiSelectMode(position);
                        return true;
                });
                binding.rename.setOnLongClickListener(v -> {
                        intoMutiSelectMode(position);
                        return true;
                });
                binding.delete.setOnLongClickListener(v -> {
                        intoMutiSelectMode(position);
                        return true;
                });
                binding.share.setOnLongClickListener(v -> {
                        intoMutiSelectMode(position);
                        return true;
                });
                binding.thumb.setOnClickListener(v -> {
                        if (mListener != null) {
                                mListener.onClickImage(position, mData.get(position));
                        }
                });
	        binding.rename.setOnClickListener(v -> {
		        if (mListener != null) {
			        mListener.onClickRename(position, mData.get(position));
		        }
	        });
	        binding.delete.setOnClickListener(v -> {
		        if (mListener != null) {
			        mListener.onClickDelete(position, mData.get(position));
		        }
	        });
	        binding.share.setOnClickListener(v -> {
		        if (mListener != null) {
			        mListener.onClickShare(position, mData.get(position));
		        }
	        });
                binding.foreground.setOnClickListener(v -> {
			onClickForeground(position);
                });
                binding.checkbox.setOnClickListener(v -> onClickForeground(position));
        }

        private void onClickForeground(int position) {
	        if (mSelectedPostions.contains(position)) {
		        mSelectedPostions.remove(position);
		        notifyItemChanged(position);
	        } else {
		        mSelectedPostions.add(position);
		        notifyItemChanged(position);
	        }
	        if (mListener != null) {
		        mListener.onClickFore(position, mSelectedPostions.size());
	        }
        }

        private void intoMutiSelectMode(int position) {
                mCurrentMode = MODE_SELECT;
                mSelectedPostions.add(position);
                if (mModeChangeListener != null) {
                        mModeChangeListener.onModeTo(MODE_SELECT);
                }
                notifyDataSetChanged();
        }

        public void intoNormalMode() {
                mCurrentMode = MODE_NORMAL;
                if (mModeChangeListener != null) {
                        mModeChangeListener.onModeTo(MODE_NORMAL);
                }
                mSelectedPostions.clear();
                notifyDataSetChanged();
        }

	public interface OnClickListener {
                void onClickImage(int position, VideoInfo videoInfo);
                void onClickRename(int position, VideoInfo videoInfo);
                void onClickDelete(int position, VideoInfo videoInfo);
                void onClickShare(int position, VideoInfo videoInfo);
                void onClickFore(int position, int selectedNumber);
	}

	public interface OnModeChangeListener {
                void onModeTo(int mode);
        }
}
