package com.example.tutorme.swipe_view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tutorme.*
import com.example.tutorme.databinding.ActivitySwipeBinding
import com.example.tutorme.models.Class
import com.example.tutorme.models.Student
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*
import android.widget.Toast
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


private const val TAG = "SwipeActivity"

class SwipeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ClassListInterface {
    private var internetDisposable: Disposable? = null

    // Handles the item selection in the drawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_add_class -> {
                val intent = Intent(this, AddClassActivity::class.java)
                intent.putExtra("cur_user", curUser)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                intent.putExtra("cur_user", curUser)
                startActivity(intent)
            }
            R.id.nav_chat -> {
                val intent = Intent(this, ChatListActivity::class.java)
                intent.putExtra("cur_user", curUser)
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

    override fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }

    private lateinit var binding: ActivitySwipeBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var curUser: Student

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySwipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        binding.recViewClassList.layoutManager = LinearLayoutManager(this)

        if (savedInstanceState == null) {
            val extras = this.intent.extras
            curUser = extras!!.get("cur_user") as Student
            Log.d("DEBUG", curUser.toString())
        }

        // Handling setup for drawer and navigation view
        drawer = binding.drawerLayout
        navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)

        println("THING ${intent.action}")

        if (intent.action == null) {
            intent.action = "Already created"
            FirebaseAuth.getInstance().uid?.let { it ->
                FirebaseFirestore.getInstance()
                    .collection("students").document(it).get()
                    .addOnSuccessListener {
                        val user = it.toObject(Student::class.java)
                        navigationView.draw_full_name.text =
                            "${user?.first_name} " +
                                    "${user?.last_name}"
                        println("THING1 ${user?.first_name} ${user?.last_name}")
                        navigationView.draw_email.text =
                            FirebaseAuth.getInstance().currentUser?.email
                        var profilePic = user?.profile_picture_url
                        if (user?.profile_picture_url == null || user.profile_picture_url!!.isEmpty()) {
                            profilePic = SettingsActivity.DEFAULT_PROFILE_PICTURE
                        }
                        //Picasso.get().load(profilePic).into(nav_profile_pic)
                        Glide.with(this).load(profilePic).into(nav_profile_pic)
                    }
            }

            val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            drawer.addDrawerListener(toggle)
            toggle.syncState()


            // Making a query to discover which classes we will seek tutors for
            val usersClasses = FirebaseFirestore.getInstance()
                .collection("classes").whereEqualTo("student_id", curUser.id)

            val builder = FirestoreRecyclerOptions.Builder<Class>()
                .setQuery(usersClasses, Class::class.java).setLifecycleOwner(this)

            //) { snapshot ->
            //                            snapshot.toObject(Class::class.java)!!.also {
            //                                it.student_id = snapshot.id
            //                                it.dpt_code = snapshot["dpt_code"].toString()
            //                                it.class_code = snapshot["class_code"].toString()
            //                            }
            //                        }

            val options = builder.build()
            val adapter = ClassListAdapter(options, curUser)
            binding.recViewClassList.adapter = adapter
        }
    }

    override fun onResume() {
        val action = intent.action
        // Prevent endless loop by adding a unique action, don't restart if action is present
        if (action == null || action != "Already created") {
            Log.v("DEBUG", "Force restart")
            val intent = Intent(this, SwipeActivity::class.java)
            intent.putExtra("cur_user", curUser)
            startActivity(intent)
            finish()
        }

        internetDisposable = ReactiveNetwork.observeNetworkConnectivity(this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .distinctUntilChanged()
            .subscribe { connectivity ->
                if (!connectivity.available()) {
                    Toast.makeText(this, "Network currently unavailable.", Toast.LENGTH_LONG).show()
                }
            }

        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        safelyDispose(internetDisposable)
    }

    private fun safelyDispose(disposable: Disposable?) {
        if (disposable != null && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}

