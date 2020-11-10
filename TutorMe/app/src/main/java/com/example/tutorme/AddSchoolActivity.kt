package com.example.tutorme

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.tutorme.databinding.ActivityAddSchoolBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint


class AddSchoolActivity : AppCompatActivity() {

    // Using view-binding from arch-components (requires Android Studio 3.6 Canary 11+)
    private lateinit var binding: ActivityAddSchoolBinding
    private lateinit var coordinates: Location
    private lateinit var geo: GeoPoint
    private lateinit var located: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var REQUEST_LOCATION: Int = 2

    private fun showExplanation(
        title: String,
        message: String,
        permission: String,
        permissionRequestCode: Int
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok
            ) { _, _ ->
                requestPermission(
                    permission,
                    permissionRequestCode
                )
            }
        builder.create().show()
    }

    private fun requestPermission(permissionName: String, permissionRequestCode: Int) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permissionName), permissionRequestCode
        )
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationMode: Int
        try {
            locationMode =
                Settings.Secure.getInt(context.contentResolver, "location_mode")

        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            return false
        }

        return locationMode != Settings.Secure.LOCATION_MODE_OFF
    }

    @SuppressLint("SetTextI18n")
    private fun adjustLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    coordinates = location
                    binding.addSchoolLocation.text = coordinates.longitude.toString() +
                            " " + coordinates.latitude
                    located = "true"
                    geo = GeoPoint(coordinates.latitude, coordinates.longitude)
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()
                adjustLocation()
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSchoolBinding.inflate(layoutInflater)
        setContentView(binding.root)

        located = "false"

        if (isLocationEnabled(this)) {
            if (checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Check Permissions Now
                val REQUEST_LOCATION = 2

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) {
                    showExplanation(
                        "Permission needed",
                        "Location access needed to approximate university's location",
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        REQUEST_LOCATION
                    )
                } else {
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_LOCATION
                    )
                }
            } else {
                // permission has been granted, continue as usual
                adjustLocation()
            }
        } else {
            Toast.makeText(this, "Please ensure location services " +
                    "are enabled", Toast.LENGTH_LONG).show()
        }

        binding.addSchoolAddButton.setOnClickListener {

            //            println("Thing1: ${binding.editSettingsSchool.text}\n" +
//                    "Thing2: ${theSchool}")

            // If the school hasn't been selected or info is missing, refuse the save
            if (located == "false" || binding.addSchoolName.length() == 0 || !isLocationEnabled(this)) {
                Toast.makeText(
                    this, "Please make sure to enter your " +
                            "school's name and enable location" +
                            " permissions!", Toast.LENGTH_SHORT
                ).show()
            } else {
                // Prepares the settings based on the fields
                val settings = hashMapOf(
                    "name" to binding.addSchoolName.text.toString(),
                    "short_name" to binding.addSchoolShortName.text.toString(),
                    "location" to geo
                )

                val db = FirebaseFirestore.getInstance()

                // Adds or updates the document to the students collection based on the login email used
                db.collection("universities").document(binding.addSchoolName.text.toString())
                    .set(settings)

                // Redirects back to the school list page after saving
                finish()
//                val intent = Intent(this, SchoolListActivity::class.java)
//                startActivity(intent)

            }
        }
    }
}
