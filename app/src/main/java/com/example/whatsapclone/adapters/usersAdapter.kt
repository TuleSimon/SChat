package com.example.whatsapclone.adapters

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsapclone.R
import com.example.whatsapclone.activity.Settings
import com.example.whatsapclone.firebase.firebase
import com.example.whatsapclone.model.onlineModel
import com.example.whatsapclone.model.userModel
import com.firebase.ui.auth.data.model.User
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activemessagesrow.*
import kotlinx.android.synthetic.main.usersrow.view.*
import java.io.FileInputStream

class usersAdapter(var model: FirebaseRecyclerOptions<userModel>, var context:Context, var act:Activity):
    FirebaseRecyclerAdapter<userModel, usersAdapter.viewHolder>(model)
     {
         override fun getItemCount(): Int {
             return super.getItemCount()
         }

         override fun onBindViewHolder(holder: viewHolder, position: Int) {
             super.onBindViewHolder(holder, position)
         }

        inner class viewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var userImage =itemView.usrsRow_userImage
        var userName = itemView.usrsRow_userName
        var About = itemView.usrsRow_userAbout
            val online= itemView.userRow_status
        fun click(id:String){
            itemView.setOnClickListener {
                firebase.userID= id
                val Intents= Intent(context,Settings::class.java)
                Intents.putExtra("user",true)
                context.startActivity(Intents)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.usersrow,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int, model: userModel) {
        holder.userName.text= model.username
        holder.About.text = model.aboutme
        var drawable:Drawable
        if( loadImageBitmap(context,
                firebase.firebaseauth.uid!!,"jpg")!=null){
            drawable= BitmapDrawable(loadImageBitmap(context,
                firebase.firebaseauth.uid!!,"jpg"))}
        else
            drawable=context.getDrawable( R.drawable.happy_woman)!!

        Glide.with(context).load(model.thumb).placeholder(R.drawable.blank) .into(holder.userImage)
        holder.click(getRef(position).key!!)
        val onlineDatabase= FirebaseDatabase.getInstance().reference.child("onlineStatus").child(
            getRef(position).key!!)
        onlineDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                print("")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var data = snapshot.getValue(onlineModel::class.java)
                if (data != null) {
                if (data!!.online == "online") {
                    holder.online .text = data!!.online
                    holder.online.setTextColor(context.resources.getColor(R.color.colorPrimary))
                }
                else{
                    holder.online .text ="last seen\n" +firebase.convertDate( data!!.lastseen)
                }
            }
            }
        })


    }

         fun loadImageBitmap(context:Context, name:String, extension:String): Bitmap? {
             val names = name + "." + extension
             val fileInputStream: FileInputStream
             try{
                 fileInputStream = context.openFileInput(names);
                 firebase.bitmap = BitmapFactory.decodeStream(fileInputStream);
                 fileInputStream.close();
             } catch( e:Exception) {
                 e.printStackTrace();
             }
             if(firebase.bitmap!=null)
                 return firebase.bitmap!!
             return null
         }

}