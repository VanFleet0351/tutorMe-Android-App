package com.example.tutorme.swipe_view

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tutorme.ChatActivity
import com.example.tutorme.ChatListActivity
import com.example.tutorme.R
import com.example.tutorme.SettingsActivity
import com.example.tutorme.models.Class
import com.example.tutorme.models.Student
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.user_row.view.*


class UserListAdapter(options: FirestoreRecyclerOptions<Class>) :
    FirestoreRecyclerAdapter<Class, UserListAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.user_row, parent, false)

        return ViewHolder(cellForRow)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: Class) {
        holder.containerView.setOnClickListener {

            //Don't allow user to create a chat with themselves
            if (item.student_id == FirebaseAuth.getInstance().uid) return@setOnClickListener

            item.student_id?.let {
                FirebaseFirestore.getInstance()
                    .collection("students").document(it).get().addOnSuccessListener {
                        val user = it.toObject(Student::class.java)

                        val intent = Intent(holder.containerView.context, ChatActivity::class.java)
                        intent.putExtra(ChatListActivity.USER_KEY, user)
                        holder.containerView.context.startActivity(intent)
                    }
            }
        }

        holder.containerView.apply {
            item.student_id
            item.student_id?.let {
                FirebaseFirestore.getInstance()
                    .collection("students").document(it).get().addOnSuccessListener {
                        val user = it.toObject(Student::class.java)
                        primaryTextView.text = "${user?.first_name} " +
                                user?.last_name
                        secondaryTextView.text = "Tutor rate: $${item.tutor_price}"
                        var profilePic = user?.profile_picture_url
                        if (user != null) {
                            if (user.profile_picture_url == null || user.profile_picture_url!!.isEmpty()) {
                                profilePic = SettingsActivity.DEFAULT_PROFILE_PICTURE
                            }
                        }
                        //Picasso.get().load(profilePic).into(profilepic_imageview_user_row)
                        Glide.with(this).load(profilePic).into(profilepic_imageview_user_row)
                    }
            }

            }
        }

    class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer
}



