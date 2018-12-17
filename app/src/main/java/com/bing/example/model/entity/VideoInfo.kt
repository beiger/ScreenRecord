package com.bing.example.model.entity

import com.bing.mvvmbase.base.IsSame

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoInfo(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        var title: String? = null,
        var path: String? = null,
        var imagePath: String? = null
) : IsSame {

        override fun itemSame(isSame: IsSame): Boolean {
                return id == (isSame as VideoInfo).id
        }

        override fun contentSame(isSame: IsSame): Boolean {
                val info = isSame as VideoInfo
                return (id == info.id
                        && title == info.title
                        && path == info.path
                        && imagePath == info.imagePath)
        }

}
