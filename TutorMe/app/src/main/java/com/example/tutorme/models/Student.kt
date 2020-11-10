package com.example.tutorme.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Student(
    @JvmField @PropertyName(ID) var id: String? = null,
    @JvmField @PropertyName(FIRST_NAME) var first_name: String? = null,
    @JvmField @PropertyName(LAST_NAME) var last_name: String? = null,
    @JvmField @PropertyName(EMAIL) var email: String? = null,
    @JvmField @PropertyName(PROFILE_PICTURE_URL) var profile_picture_url: String? = null,
    @JvmField @PropertyName(SCHOOL) var school: String? = null
) :Parcelable{
    companion object {
        const val ID = "id"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val PROFILE_PICTURE_URL = "profile_picture_url"
        const val SCHOOL = "school"
        const val EMAIL = "email"
    }
}