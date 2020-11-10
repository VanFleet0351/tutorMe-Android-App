package com.example.tutorme.swipe_view

import android.annotation.SuppressLint
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.example.tutorme.models.Class
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.user_row.view.*
import android.os.Handler
import android.util.Log
import com.example.tutorme.models.Student


class ClassListAdapter(
    options: FirestoreRecyclerOptions<Class>,
    curUser: Student
) :
    FirestoreRecyclerAdapter<Class, ClassListAdapter.ViewHolder>(options) {
    private lateinit var toDelete: Class
    private lateinit var vg: ViewGroup
    private var stud = curUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow =
            layoutInflater.inflate(com.example.tutorme.R.layout.user_row, parent, false)
        vg = parent
        this.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
        })
        Log.d("HERE", "ADDED")
        return ViewHolder(cellForRow)
    }

    @SuppressLint("InflateParams")
    private fun onButtonShowPopupWindowClick(view: View, position: Int) {

        // inflate the layout of the popup window
        val inflater = LayoutInflater.from(view.context)
        val popupView = inflater!!.inflate(com.example.tutorme.R.layout.delete_class_window, null)

        // create the popup window
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        // dismiss the popup window when touched
        popupView.setOnTouchListener { _, _ ->
            popupWindow.dismiss()
            var dDoc = "default"
            FirebaseFirestore.getInstance().collection("classes")
                .whereEqualTo("school", toDelete.school)
                .whereEqualTo("dpt_code", toDelete.dpt_code)
                .whereEqualTo("class_code", toDelete.class_code)
                .whereEqualTo("student_id", toDelete.student_id)
                .get()
                .addOnSuccessListener { docToDelete ->
                    dDoc = docToDelete.documents.last().id
                    Log.d("HERE", "dDoc set to $dDoc")
                }
                .addOnFailureListener { exception ->
                    Log.d("HERERROR", "Error getting documents: ", exception)
                }
            val handler = Handler()
            handler.postDelayed({
                Log.d("HERE", dDoc +" is this" )
                if (dDoc != "default"){
                    Log.d("HERE", dDoc)
                    FirebaseFirestore.getInstance().collection("classes").document(dDoc).delete().addOnSuccessListener {
                        Log.d("HERE", dDoc + " deleted")
                    }
                    notifyDataSetChanged()
                }
            }, 250) // 250ms delay


            true
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: Class) {
        holder.containerView.setOnClickListener {
            if (item.is_tutor == true) {
                toDelete = item
                onButtonShowPopupWindowClick(holder.containerView, position)
            } else {
                val intent = Intent(holder.containerView.context, UserListActivity::class.java)
                intent.putExtra("WHICH_CLASS", item)
                intent.putExtra("cur_user", stud)
                holder.containerView.context.startActivity(intent)
            }

        }

        holder.containerView.apply {
            primaryTextView.text = "${item.dpt_code} " +
                    item.class_code
            if (item.is_tutor == true) {
                secondaryTextView.text =
                    context.getString(com.example.tutorme.R.string.class_list_tutor)
            } else {
                secondaryTextView.text =
                    context.getString(com.example.tutorme.R.string.class_list_student)
            }
        }
    }

    class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer
}
