package com.example.tokopaerbe

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tokopaerbe.prelogin.onboarding.OnBoarding1Fragment
import com.example.tokopaerbe.prelogin.onboarding.OnBoarding2Fragment
import com.example.tokopaerbe.prelogin.onboarding.OnBoarding3Fragment

class SectionsPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = OnBoarding1Fragment()
            1 -> fragment = OnBoarding2Fragment()
            2 -> fragment = OnBoarding3Fragment()
        }
        return fragment as Fragment
    }
}
