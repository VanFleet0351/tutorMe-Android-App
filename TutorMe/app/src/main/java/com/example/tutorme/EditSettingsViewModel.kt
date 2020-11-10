package com.example.tutorme

import android.net.Uri
import androidx.lifecycle.ViewModel

class EditSettingsViewModel: ViewModel() {
    var selectedPhotoUri: Uri? = null
    var id:String? = null
    var firstName:String? = null
    var lastName:String? = null
    var email:String? = null
    var school = "default"

}