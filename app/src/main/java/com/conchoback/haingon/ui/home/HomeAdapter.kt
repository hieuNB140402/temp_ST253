package com.conchoback.haingon.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.conchoback.haingon.ui.home.fragment.HomeFragment
import com.conchoback.haingon.ui.home.fragment.MyCreationFragment
import com.conchoback.haingon.ui.home.fragment.SettingsFragment

class HomeAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> HomeFragment()
            1 -> MyCreationFragment()
            else -> SettingsFragment()
        }
    }

    override fun getItemCount(): Int = 3
}