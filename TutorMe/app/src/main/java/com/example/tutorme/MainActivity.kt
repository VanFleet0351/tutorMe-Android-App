package com.example.tutorme

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.tutorme.models.Student
import com.example.tutorme.swipe_view.SwipeActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private var curUser: Student? = null

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSignInIntent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
//            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val db = FirebaseFirestore.getInstance()
                val settings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build()
                db.firestoreSettings = settings

                var intentChoice = "swipe"
                val docRef = db.collection("students").document(FirebaseAuth.getInstance().currentUser!!.uid)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document.data.toString() == "null") {
                            Log.d("DEBUG", "DOCUMENT NOT NULL")
                            intentChoice = "edit"
                        } else {
                            Log.d("USER_DOC", document.data.toString())
                            curUser= document.toObject(Student::class.java)!!
                        }
                    }
                    .addOnFailureListener{exception ->
                        Log.d(TAG, "get failed with ", exception)
                    }

                db.collection("users")
                    .whereEqualTo("email", FirebaseAuth.getInstance().currentUser?.email)
                    .get()
                    .addOnSuccessListener {
                        val intent = if (it.isEmpty) {
                            intent.putExtra(
                                "user_email",
                                FirebaseAuth.getInstance().currentUser?.email
                            )
                            if (intentChoice == "swipe") {
                                Intent(this, SwipeActivity::class.java)
                            } else{
                                Intent(this, EditSettingsActivity::class.java)
                            }

                        } else {
                            Intent(this, EditSettingsActivity::class.java)
                        }
                        Log.d(TAG, it.toString())
                        if (curUser != null) {
                            intent.putExtra("cur_user", curUser)
                            Log.d("STUDENT_OBJ", curUser.toString())
                        }
                        startActivity(intent as Intent?)
                    }
            } else {
                // Sign in failed. If response is null the user canceled the sign-in flow using
                // the back button. Otherwise check response.getError().getErrorCode() and handle
                // the error
                Log.d(TAG, "Failed to sign in")
            }
        }
    }


    companion object {
        private const val RC_SIGN_IN = 9001
    }

}
