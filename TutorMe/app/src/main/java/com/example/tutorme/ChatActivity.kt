package com.example.tutorme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.tutorme.models.ChatMessage
import com.example.tutorme.models.Student
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.from_chat_row.view.*
import kotlinx.android.synthetic.main.to_chat_row.view.*

private const val TAG = "chatAct"

class ChatActivity : AppCompatActivity() {

    private var internetDisposable: Disposable? = null

    val adapter = GroupAdapter<GroupieViewHolder>()
    var toStudent: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        toStudent = intent.getParcelableExtra<Student>(ChatListActivity.USER_KEY)
        supportActionBar?.title = "${toStudent?.first_name} ${toStudent?.last_name}"

        recycleview_chat.adapter = adapter

        listenForMessages()

        send_button_chat.setOnClickListener {
            performSendMessage()
        }

        recycleview_chat.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if(oldBottom > bottom){
                recycleview_chat.postDelayed({ recycleview_chat.smoothScrollToPosition(bottom) }, 100)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        internetDisposable = ReactiveNetwork.observeNetworkConnectivity(this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isConnectedToInternet ->
                edittext_chat.isEnabled = isConnectedToInternet.available()
                send_button_chat.isEnabled = isConnectedToInternet.available()
            }
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

    private fun listenForMessages(){
        val fromId = FirebaseAuth.getInstance().currentUser?.uid
        val toId = toStudent?.id
        val ref = FirebaseDatabase.getInstance().getReference("/messages/$fromId/$toId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage != null){
                    if(chatMessage.fromId == FirebaseAuth.getInstance().currentUser?.uid){
                        Log.d(TAG, "Create new From row")
                        adapter.add(ChatFromItem(chatMessage.text))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text))
                    }
                }
                recycleview_chat.scrollToPosition(adapter.itemCount - 1)
            }
            //Not used but needed
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
        })
    }

    private fun performSendMessage(){
        val fromId = FirebaseAuth.getInstance().currentUser?.uid
        val toId = toStudent?.id
        val textMessage = edittext_chat.text.toString()
        val fromRef = FirebaseDatabase.getInstance().getReference("/messages/$fromId/$toId").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(fromRef.key!!, textMessage, fromId!!, toId!!, System.currentTimeMillis()/1000)
        toRef.setValue(chatMessage)
        fromRef.setValue(chatMessage).addOnSuccessListener {
            edittext_chat.text.clear()
            recycleview_chat.smoothScrollToPosition(adapter.itemCount - 1)
        }

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-message/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-message/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }
}

class ChatFromItem(val text: String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.from_textview_chat.text = text
        Log.d(TAG , "Created From row with: $text")
    }

    override fun getLayout(): Int {
        return R.layout.from_chat_row
    }
}

class ChatToItem(val text: String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.to_textview_chat.text = text
    }

    override fun getLayout(): Int {
        return R.layout.to_chat_row
    }
}