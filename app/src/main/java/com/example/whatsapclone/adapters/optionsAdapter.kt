package com.example.whatsapclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapclone.R
import com.example.whatsapclone.model.tagModel
import kotlinx.android.synthetic.main.tagitem.view.*

class optionsAdapter(var model: ArrayList<tagModel>, var context:Context): RecyclerView.Adapter<optionsAdapter.viewHolder>() {
    override fun getItemCount(): Int {
        return model.size
    }


    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title=itemView.tag_title
        var delete=itemView.tag_deletebutton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.tagitem, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val models = model[position]
        holder.title.text=models.title
        holder.delete.setOnClickListener {
            model.remove(model[position])
            notifyItemRemoved(position)
        }


    }



}

