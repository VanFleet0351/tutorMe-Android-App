package com.example.tutorme.swipe_view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tutorme.models.Student
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.text.isDigitsOnly
import com.bumptech.glide.Glide
import com.example.tutorme.*
import com.example.tutorme.databinding.ActivityUserListBinding
import com.example.tutorme.models.Class
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlin.system.exitProcess


private const val TAG = "UserListActivity"

class UserListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    // Handles the item selection in the drawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_add_class -> {
                val intent = Intent(this, AddClassActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_chat -> {
                val intent = Intent(this, ChatListActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_sign_out -> {
                val intent = Intent(this, MainActivity::class.java)
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        // user is now signed out
                        startActivity(intent)
                        finish()
                    }
            }
            else -> Log.d("DEBUG", "Odd interaction with the navigation drawer...")
        }
        return true
    }

    private lateinit var binding: ActivityUserListBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var WHICH_CLASS: Class
    private lateinit var curUser: Student

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        binding.recViewUserList.layoutManager = LinearLayoutManager(this)

        if (savedInstanceState == null) {
            val extras = this.intent.extras
            curUser = extras!!.get("cur_user") as Student
            Log.d("DEBUG", curUser.toString())
        }
        WHICH_CLASS = intent.getParcelableExtra("WHICH_CLASS")!!

        var docToDelete = "none"

        //TODO: fix bug with deleting class

        binding.userListDeleteClassBtn.setOnClickListener {
            FirebaseFirestore.getInstance().collection("classes")
                .whereEqualTo("school", WHICH_CLASS.school)
                .whereEqualTo("dpt_code", WHICH_CLASS.dpt_code)
                .whereEqualTo("class_code", WHICH_CLASS.class_code)
                .whereEqualTo("student_id", WHICH_CLASS.student_id)
                .addSnapshotListener { querySnapshot, _ ->
                    if (querySnapshot != null) {
                        for (doc in querySnapshot.documents){
                            docToDelete = doc.id
                        }
                        FirebaseFirestore.getInstance().collection("classes")
                            .document(docToDelete).delete().addOnSuccessListener {
                                Log.d("HERE", docToDelete + " deleted")
                                val intent = Intent(this, SwipeActivity::class.java)
                                intent.putExtra("cur_user", curUser)
                                startActivity(intent)
                            }
                    }
                }

        }

        // Handling setup for drawer and navigation view
        drawer = binding.drawerLayout
        navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)

        FirebaseAuth.getInstance().uid?.let { it ->
            FirebaseFirestore.getInstance()
                .collection("students").document(it).get()
                .addOnSuccessListener {
                    val user = it.toObject(Student::class.java)
                    navigationView.draw_full_name.text =
                        "${user?.first_name} " +
                                "${user?.last_name}"

                    navigationView.draw_email.text =
                        FirebaseAuth.getInstance().currentUser?.email
                    var profilePic = user?.profile_picture_url
                    if (user?.profile_picture_url == null || user.profile_picture_url!!.isEmpty()) {
                        profilePic = SettingsActivity.DEFAULT_PROFILE_PICTURE
                    }
                    Glide.with(this).load(profilePic).into(nav_profile_pic)

                    // Making a query to discover which classes we will seek tutors for
                    val usersClasses = FirebaseFirestore.getInstance()
                        .collection("classes").whereEqualTo(
                            "school",
                            user?.school
                        ).whereEqualTo("dpt_code", WHICH_CLASS.dpt_code)
                        .whereEqualTo("class_code", WHICH_CLASS.class_code)
                        .whereEqualTo("is_tutor", true)
//                    println("THING1 $WHICH_CLASS")

//                    usersClasses.addSnapshotListener { querySnapshot, _ ->
//                        if (querySnapshot != null) {
//                            for (doc in querySnapshot) {
//                                println("THING2 ${doc.data}")
//                            }
//                        }
//                    }

                    val builder = FirestoreRecyclerOptions.Builder<Class>()
                        .setQuery(usersClasses, Class::class.java).setLifecycleOwner(this)

                    val options = builder.build()
                    val adapter = UserListAdapter(options)
                    binding.recViewUserList.adapter = adapter
                }
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}

