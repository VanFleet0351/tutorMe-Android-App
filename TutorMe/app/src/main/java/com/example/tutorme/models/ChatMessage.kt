package com.example.tutorme.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String, val timeStamp: Long){
    constructor() : this("", "", "", "", -1)
}