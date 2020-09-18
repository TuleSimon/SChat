package com.example.whatsapclone.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.example.whatsapclone.R
import com.example.whatsapclone.activity.messageActivity
import com.example.whatsapclone.firebase.firebase
import com.example.whatsapclone.model.activeChats
import com.example.whatsapclone.model.onlineModel
import com.example.whatsapclone.model.postModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activemessagesrow.*
import kotlinx.android.synthetic.main.activemessagesrow.view.*
import kotlinx.android.synthetic.main.activemessagesrow.view.active_onlineStatus
import kotlinx.android.synthetic.main.post_item.view.*
import java.io.FileInputStream

class feedsAdapter2(var model: FirebaseRecyclerOptions<postModel>, var context:Context): FirebaseRecyclerAdapter<postModel, feedsAdapter2.viewHolder>(model)
     {
         override fun getItemCount(): Int {
             return super.getItemCount()
         }



        inner class viewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val body= itemView.post_body
            val title= itemView.post_title
            val image= itemView.post_image
            val userimage= itemView.post_userimage
            val username= itemView.post_username

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.post_item,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int, model: postModel) {
            holder.body.text= model.body
            holder.title.text = model.title
        if(model.image!=null)
            Glide.with(context).load(model.image).into(holder.image)
        fetchUserDetails(holder.userimage,holder.username,model.userid!!)
        }


fun fetchUserDetails(image:ImageView, text:TextView, userid:String){
    var data = FirebaseDatabase.getInstance().reference.child("users").child(userid!!)
        .addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                text.text = "user"
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var data = snapshot.value as HashMap<String, Any>
                val name = data.get("username").toString()
                text.text = name
                var thumb = data.get("thumb").toString()
                Glide.with(context).load(thumb).into(image)
            }

        })
}




     }