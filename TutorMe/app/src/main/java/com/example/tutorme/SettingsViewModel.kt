package com.example.tutorme

import androidx.lifecycle.ViewModel
import com.example.tutorme.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsViewModel: ViewModel() {
    var currentUser: Student? = null
    var email: String? = null
}