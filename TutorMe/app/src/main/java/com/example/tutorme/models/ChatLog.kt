package com.example.tutorme.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class ChatLog(
    @JvmField @PropertyName(MESSAGES) var messages: String? = null,
    @JvmField @PropertyName(USER_ONE) var user_one: String? = null,
    @JvmField @PropertyName(USER_TWO) var user_two: String? = null
) {
    companion object {
        const val MESSAGES = "messages"
        const val USER_ONE = "user_one"
        const val USER_TWO = "user_two"
    }
}