package com.example.tutorme

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tutorme.databinding.ActivityChatListBinding
import com.example.tutorme.models.ChatLog
import com.example.tutorme.models.ChatMessage
import com.example.tutorme.models.Student
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.android.synthetic.main.latest_message_row.view.*


class ChatListActivity : AppCompatActivity() {

    companion object {
        var currentUser: Student? = null
        const val USER_KEY = "USER_KEY"
    }

    private val adapter = GroupAdapter<GroupieViewHolder>()
    val latestMessagesMap = HashMap<String, ChatMessage>()
    private lateinit var curUser: Student
    private var internetDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        recyclerview_latestmessages.adapter = adapter
        recyclerview_latestmessages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        if (savedInstanceState == null) {
            val extras = this.intent.extras
            curUser = extras!!.get("cur_user") as Student
            Log.d("DEBUG", curUser.toString())
        }

        val parIntent = Intent()
        parIntent.putExtra("cur_user", curUser)
        setResult(RESULT_OK, parIntent)

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra(USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        listenForLatestMessage()
        fetchCurrentUser()
    }

    override fun onResume() {
        super.onResume()

        internetDisposable = ReactiveNetwork.observeNetworkConnectivity(this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .distinctUntilChanged()
            .subscribe { connectivity ->
                if (!connectivity.available()) {
                    Toast.makeText(this, "Network currently unavailable - cannot chat at this time.", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onPause() {
        super.onPause()
        ObservableUtils.safelyDispose(internetDisposable)
    }

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessage(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-message/$fromId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }



        })
    }

    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseFirestore.getInstance().collection("students")
            .document(uid!!).get()
            .addOnSuccessListener {
                currentUser = it.toObject(Student::class.java)
            }
    }

}

class LatestMessageRow(private val chatMessage: ChatMessage): Item<GroupieViewHolder>(){

    var chatPartnerUser: Student? = null
    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.latest_message_textview_latestmessage.text = chatMessage.text
        val chatPartnerId = if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatMessage.toId
        } else {
            chatMessage.fromId
        }
        FirebaseFirestore.getInstance().collection("students")
            .document(chatPartnerId).get()
            .addOnSuccessListener {
                chatPartnerUser = it.toObject(Student::class.java)
                viewHolder.itemView.user_name_textview_latestmessage.text = "${chatPartnerUser?.first_name} ${chatPartnerUser?.last_name}"
                Glide.with(viewHolder.itemView).load(chatPartnerUser?.profile_picture_url).into(viewHolder.itemView.profilepic_imageview_latest_message)
        }

    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}