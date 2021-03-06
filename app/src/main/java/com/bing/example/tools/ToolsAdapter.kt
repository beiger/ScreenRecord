package com.bing.example.tools

import android.view.ViewGroup
import com.bing.example.R
import com.bing.example.databinding.ItemVideoEditBinding
import com.bing.mvvmbase.base.IsSame
import com.bing.mvvmbase.base.recycleview.BaseRecycleViewAdapter
import com.bing.mvvmbase.base.recycleview.BaseViewHolder

class VideoEditAdapter(listenerTemp: OnClickListener): BaseRecycleViewAdapter<VideoEditType, VideoEditViewHolder>() {
        init {
                listener = listenerTemp
        }
        override fun bindData(holder: VideoEditViewHolder, position: Int) {
                val binding = holder.binding
                when (data!![position].type) {
                        ToolsType.VIDEO_EDIT -> {
                                binding.tvEdit.text = binding.root.context.getText(R.string.jianqie)
                                binding.ivEdit.setImageResource(R.drawable.ic_jianqie)
                        }
                }
        }

        override fun createHolder(parent: ViewGroup, viewType: Int): VideoEditViewHolder {
                return VideoEditViewHolder(getBinding(parent, viewType, R.layout.item_video_edit) as ItemVideoEditBinding)
        }

}

class VideoEditViewHolder(binding: ItemVideoEditBinding): BaseViewHolder<ItemVideoEditBinding>(binding)

class VideoEditType(val type: ToolsType): IsSame {
        override fun contentSame(obj: IsSame): Boolean {
                return type == (obj as VideoEditType).type
        }

        override fun itemSame(obj: IsSame): Boolean {
                return type == (obj as VideoEditType).type
        }

}

enum class ToolsType {
        VIDEO_EDIT,
}