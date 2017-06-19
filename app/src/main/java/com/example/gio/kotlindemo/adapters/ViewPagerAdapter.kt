package com.example.gio.kotlindemo.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.gio.kotlindemo.fragments.ViewPagerMarker
import com.example.gio.kotlindemo.models.bus_stops.PlaceStop

/**
 * Copyright by Gio.
 * Created on 6/15/2017.
 */
class ViewPagerAdapter(private val mContext: Context, fm: FragmentManager, private val mPlaceStops: List<PlaceStop>) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        val fragment: Fragment = ViewPagerMarker().newInstance(mPlaceStops[position])
        return fragment
    }

    override fun getCount(): Int {
        return mPlaceStops.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return position.toString()
    }

    override fun getItemPosition(`object`: Any?): Int {
        return POSITION_NONE
    }
}
