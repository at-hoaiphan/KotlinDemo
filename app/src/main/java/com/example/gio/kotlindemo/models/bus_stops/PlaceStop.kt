package com.example.gio.kotlindemo.models.bus_stops

import android.os.Parcel
import android.os.Parcelable
import android.provider.BaseColumns

/**
 * Copyright by Gio.
 * Created on 6/13/2017.
 */
class PlaceStop : BaseColumns, Parcelable {
    private var id: Int = 0
    internal var name: String? = null
    internal var latitude: Double = 0.toDouble()
    internal var longitude: Double = 0.toDouble()
    internal var carriage: String? = null
    internal var address: String? = null

    private constructor(`in`: Parcel) {
        id = `in`.readInt()
        name = `in`.readString()
        latitude = `in`.readDouble()
        longitude = `in`.readDouble()
        carriage = `in`.readString()
        address = `in`.readString()
    }

    constructor(id: Int, name: String, latitude: Double, longitude: Double, carriage: String, address: String) {
        this.id = id
        this.name = name
        this.latitude = latitude
        this.longitude = longitude
        this.carriage = carriage
        this.address = address
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(carriage)
        parcel.writeString(address)
    }

    companion object {

        val CREATOR: Parcelable.Creator<PlaceStop> = object : Parcelable.Creator<PlaceStop> {
            override fun createFromParcel(`in`: Parcel): PlaceStop {
                return PlaceStop(`in`)
            }

            override fun newArray(size: Int): Array<PlaceStop?> {
                return arrayOfNulls(size)
            }
        }
    }
}