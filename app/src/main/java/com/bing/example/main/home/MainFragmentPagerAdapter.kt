package com.bing.example.main.home

import android.view.ViewGroup

import java.util.ArrayList
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MainFragmentPagerAdapter(fm: FragmentManager, private val mFragments: ArrayList<Fragment>) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
                return mFragments[position]
        }

        override fun getCount(): Int {
                return mFragments.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
                return super.instantiateItem(container, position)
        }
}
