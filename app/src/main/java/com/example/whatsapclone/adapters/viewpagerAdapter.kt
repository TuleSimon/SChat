package com.example.whatsapclone.adapters

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter

class viewpagerAdapter(var fg:AppCompatActivity, var fragmentList:MutableList<Fragment>, var title:MutableList<String>)
    : FragmentStateAdapter(fg) {



    fun addFragments(fragment:Fragment, Title:String){
        fragmentList.add(fragment)
        title.add(Title)
    }

    override fun getItemCount(): Int {
       return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList.get(position)
    }



}