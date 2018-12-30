package com.bing.example.search

import android.view.ViewGroup

import com.bing.example.R
import com.bing.example.model.entity.VideoInfo
import com.bing.mvvmbase.base.recycleview.BaseRecycleViewAdapter
import com.bing.example.databinding.ItemVideoSearchBinding

class VideosAdapter(private val mListener: OnClickListener?) : BaseRecycleViewAdapter<VideoInfo, VideoInfoHolder>() {
        override fun getItemCount(): Int {
                return super.getItemCount()
        }

        override fun createHolder(parent: ViewGroup, viewType: Int): VideoInfoHolder {
                return VideoInfoHolder(getBinding(parent, viewType, R.layout.item_video_search) as ItemVideoSearchBinding)
        }

        override fun bindData(holder: VideoInfoHolder, position: Int) {
                val binding = holder.binding
                val videoInfo = data!![position]
                binding.videoInfo = videoInfo
                binding.thumb.setOnClickListener {
                        mListener?.onClickImage(position, videoInfo)
                }
                binding.rename.setOnClickListener {
                        mListener?.onClickRename(position, videoInfo)
                }
                binding.delete.setOnClickListener {
                        mListener?.onClickDelete(position, videoInfo)
                }
                binding.share.setOnClickListener {
                        mListener?.onClickShare(position, videoInfo)
                }
        }

        interface OnClickListener {
                fun onClickImage(position: Int, videoInfo: VideoInfo)
                fun onClickRename(position: Int, videoInfo: VideoInfo)
                fun onClickDelete(position: Int, videoInfo: VideoInfo)
                fun onClickShare(position: Int, videoInfo: VideoInfo)
        }
}
