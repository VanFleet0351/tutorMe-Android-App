package com.example.tutorme.models

import android.Manifest.permission_group.LOCATION
import android.location.Location
import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@Parcelize
data class University(
    @JvmField @PropertyName(LOCATION) var location: @RawValue GeoPoint? = null,
    @JvmField @PropertyName(NAME) var name: String? = null,
    @JvmField @PropertyName(SHORT_NAME) var short_name: String? = null
) :Parcelable{
    companion object {
        const val LOCATION = "location"
        const val NAME = "name"
        const val SHORT_NAME = "short_name"
    }
}