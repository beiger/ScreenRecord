package com.bing.example.videoedit

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

        }

        override fun createHolder(parent: ViewGroup, viewType: Int): VideoEditViewHolder {
                return VideoEditViewHolder(getBinding(parent, viewType, R.layout.item_video_edit) as ItemVideoEditBinding)
        }

}

class VideoEditViewHolder(binding: ItemVideoEditBinding): BaseViewHolder<ItemVideoEditBinding>(binding)

class VideoEditType(val name: String): IsSame {
        override fun contentSame(obj: IsSame): Boolean {
                return name == (obj as VideoEditType).name
        }

        override fun itemSame(obj: IsSame): Boolean {
                return name == (obj as VideoEditType).name
        }

}