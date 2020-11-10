package com.example.tutorme

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tutorme.models.University
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.user_row.view.*


class SchoolListAdapter(options: FirestoreRecyclerOptions<University>) :
    FirestoreRecyclerAdapter<University, SchoolListAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.user_row, parent, false)

        return ViewHolder(cellForRow)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: University) {
        holder.containerView.setOnClickListener {
            val intent = Intent(holder.containerView.context, EditSettingsActivity::class.java)
            intent.putExtra("school", item.name)
            holder.containerView.context.startActivity(intent)
        }

        var prim = item.name!!
        var second = ""
        if (!item.short_name.toString().isEmpty()) {
            prim = item.short_name!!
            second = item.name!!
        }
        holder.containerView.apply {
            primaryTextView.text = prim
            secondaryTextView.text = second
        }
    }



    inner class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer
}
