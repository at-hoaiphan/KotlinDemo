package com.example.gio.kotlindemo.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gio.kotlindemo.R
import com.example.gio.kotlindemo.models.bus_stops.PlaceStop
import kotlinx.android.synthetic.main.fragment_detail_marker.*

/**
 * Copyright by Gio.
 * Created on 6/15/2017.
 */
class ViewPagerMarker : Fragment() {
    private val OBJECT = "object"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater!!.inflate(R.layout.fragment_detail_marker, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val placeStop = arguments.getParcelable<PlaceStop>(OBJECT)
        if (placeStop != null) {
            tvMarkerTitle.text = placeStop.name
            tvMarkerAddress.text = "Address: " + placeStop.address
        }
//        if (mSettingsInterface.mode().get().toLowerCase() == MapActivity.WALKING) {
        imgLocation.setImageResource(R.drawable.ic_walking)
//        } else {
//            mImageLocaton.setImageResource(R.drawable.ic_car)
//        }
    }

    fun newInstance(placeStop: PlaceStop): Fragment {
        val fragment: ViewPagerMarker = ViewPagerMarker()
        val bundle = Bundle()
        bundle.putParcelable(OBJECT, placeStop)
        fragment.arguments = bundle
        return fragment
    }
}