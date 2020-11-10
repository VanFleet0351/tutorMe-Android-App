package com.example.tutorme.models

import android.Manifest.permission_group.LOCATION
import android.location.Location
import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize
import java.util.*


@Parcelize
data class Class(
    @JvmField @PropertyName(STUDENT_ID) var student_id: String? = null,
    @JvmField @PropertyName(IS_TUTOR) var is_tutor: Boolean? = null,
    @JvmField @PropertyName(TUTOR_PRICE) var tutor_price: String? = null,
    @JvmField @PropertyName(SCHOOL) var school: String? = null,
    @JvmField @PropertyName(DPT_CODE) var dpt_code: String? = null,
    @JvmField @PropertyName(CLASS_CODE) var class_code: String? = null
) :Parcelable{
    companion object {
        const val STUDENT_ID = "student_id"
        const val IS_TUTOR = "is_tutor"
        const val TUTOR_PRICE = "tutor_price"
        const val SCHOOL = "school"
        const val DPT_CODE = "dpt_code"
        const val CLASS_CODE = "class_code"
    }
}