package com.bing.example.main.videolist

import android.view.ViewGroup

import com.bing.example.R
import com.bing.example.databinding.ItemVideoBinding
import com.bing.example.model.entity.VideoInfo
import com.bing.mvvmbase.base.recycleview.BaseRecycleViewAdapter

import java.util.ArrayList
import java.util.TreeSet

class VideosAdapter(private val mListener: OnClickListener?, private val mModeChangeListener: OnModeChangeListener?) : BaseRecycleViewAdapter<VideoInfo, VideoInfoHolder>() {

        var currentMode = MODE_NORMAL

        val selectedPostions = TreeSet<Int>()

        val selectedData: List<VideoInfo>
                get() {
                        val infos = ArrayList<VideoInfo>()
                        if (data != null) {
                                for (position in selectedPostions) {
                                        infos.add(data!![position])
                                }
                        }
                        return infos
                }

        override fun createHolder(parent: ViewGroup, viewType: Int): VideoInfoHolder {
                return VideoInfoHolder(getBinding(parent, viewType, R.layout.item_video) as ItemVideoBinding)
        }

        override fun bindData(holder: VideoInfoHolder, position: Int) {
                val binding = holder.binding
                val videoInfo = data!![position]
                binding.videoInfo = videoInfo
                binding.mode = currentMode
                binding.selected = selectedPostions.contains(position)
                binding.backContent.setOnLongClickListener {
                        intoMutiSelectMode(position)
                        true
                }
                binding.thumb.setOnLongClickListener {
                        intoMutiSelectMode(position)
                        true
                }
                binding.rename.setOnLongClickListener {
                        intoMutiSelectMode(position)
                        true
                }
                binding.delete.setOnLongClickListener {
                        intoMutiSelectMode(position)
                        true
                }
                binding.share.setOnLongClickListener {
                        intoMutiSelectMode(position)
                        true
                }
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
                binding.foreground.setOnClickListener { onClickForeground(position) }
                binding.checkbox.setOnClickListener { onClickForeground(position) }
        }

        private fun onClickForeground(position: Int) {
                if (selectedPostions.contains(position)) {
                        selectedPostions.remove(position)
                        notifyItemChanged(position)
                } else {
                        selectedPostions.add(position)
                        notifyItemChanged(position)
                }
                mListener?.onClickFore(position, selectedPostions.size)
        }

        private fun intoMutiSelectMode(position: Int) {
                currentMode = MODE_SELECT
                selectedPostions.add(position)
                mModeChangeListener?.onModeTo(MODE_SELECT)
                notifyDataSetChanged()
        }

        fun intoNormalMode() {
                currentMode = MODE_NORMAL
                mModeChangeListener?.onModeTo(MODE_NORMAL)
                selectedPostions.clear()
                notifyDataSetChanged()
        }

        interface OnClickListener {
                fun onClickImage(position: Int, videoInfo: VideoInfo)
                fun onClickRename(position: Int, videoInfo: VideoInfo)
                fun onClickDelete(position: Int, videoInfo: VideoInfo)
                fun onClickShare(position: Int, videoInfo: VideoInfo)
                fun onClickFore(position: Int, selectedNumber: Int)
        }

        interface OnModeChangeListener {
                fun onModeTo(mode: Int)
        }

        companion object {
                const val MODE_NORMAL = 0
                const val MODE_SELECT = 1
        }
}
