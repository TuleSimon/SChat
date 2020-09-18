package com.example.whatsapclone.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.example.whatsapclone.R
import com.example.whatsapclone.activity.messageActivity
import com.example.whatsapclone.firebase.firebase
import com.example.whatsapclone.model.activeChats
import com.example.whatsapclone.model.onlineModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activemessagesrow.*
import kotlinx.android.synthetic.main.activemessagesrow.view.*
import kotlinx.android.synthetic.main.activemessagesrow.view.active_onlineStatus
import java.io.FileInputStream

class activechatsAdapter(var model: FirebaseRecyclerOptions<activeChats>, var context:Context): FirebaseRecyclerAdapter<activeChats, activechatsAdapter.viewHolder>(model)
     {
         override fun getItemCount(): Int {
             return super.getItemCount()
         }

         override fun onBindViewHolder(holder: viewHolder, position: Int) {
             super.onBindViewHolder(holder, position)
         }

        inner class viewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var userImage =itemView.active_userImage
            var userName = itemView.active_userName
            var message = itemView.active_lastmessage
            var date = itemView.active_date
            var online = itemView.active_onlineStatus
            var read= itemView.active_seen
            var count= itemView.active_unseenCount
            var onlineImage=itemView.active_userOnline

        fun click(id:String, thumbs:String,name: String){
            itemView.setOnClickListener {
                val message=Intent(context, messageActivity::class.java)
                message.putExtra("thumb",thumbs)
                message.putExtra("uid",id)
                message.putExtra("name", name)
                Animatoo.animateSlideRight(context)
                context.startActivity(message)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.activemessagesrow,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int, model: activeChats) {
        if(model.userid!=FirebaseAuth.getInstance().uid) {
            var data = FirebaseDatabase.getInstance().reference.child("users").child(model.userid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        holder.userName.text = "user"
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        var data = snapshot.value as HashMap<String, Any>
                        val name = data.get("username").toString()
                        holder.userName.text = name
                        var thumb = data.get("thumb").toString()
                        Glide.with(context).load(thumb).into(holder.userImage)
                        holder.click(getRef(position).key!!, thumb, name)
                    }

                })
        }
        else{
            var data = FirebaseDatabase.getInstance().reference.child("users").child(model.receiver!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        holder.userName.text = "user"
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        var data = snapshot.value as HashMap<String, Any>
                        val name = data.get("username").toString()
                        holder.userName.text = name
                        var thumb = data.get("thumb").toString()
                        Glide.with(context).load(thumb).into(holder.userImage)
                        holder.click(getRef(position).key!!, thumb, name)
                    }

                })
        }
       holder.message.text = model.lastMessage + model.seen
        if(model.userid==FirebaseAuth.getInstance().currentUser!!.uid)
            holder.date.text ="sent on\n"+firebase.convertDate( model.date!!)
        else
            holder.date.text ="Received on\n"+firebase.convertDate( model.date!!)
        val onlineDatabase= FirebaseDatabase.getInstance().reference.child("onlineStatus").child(
            getRef(position).key!!)

        onlineDatabase.addValueEventListener(object :ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                print("")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var datas = snapshot.getValue(onlineModel::class.java)
                if(datas!=null){
                if (datas.online == "online") {
                    holder.online .text = datas.online
                    holder.online.setTextColor(context.resources.getColor(R.color.colorPrimary))
                    holder.onlineImage.visibility=View.VISIBLE
                } else {
                    holder.online.text ="last seen\n" +firebase.convertDate( datas.lastseen)
                    holder.onlineImage.visibility=View.GONE
                }
            }
            }
        })
        if(model.userid==FirebaseAuth.getInstance().currentUser!!.uid) {
            FirebaseDatabase.getInstance().reference.child(
                "activeChats"
            ).child(model.receiver!!)
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child("seen")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        print("")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        var sen = snapshot.getValue(Boolean::class.java)

                        if (sen!! && model.userid == FirebaseAuth.getInstance().currentUser!!.uid) {
                            holder.read.visibility = View.VISIBLE
                        } else if(!sen && model.userid != FirebaseAuth.getInstance().currentUser!!.uid){
                            holder.read.visibility = View.GONE
                            holder.count.visibility = View.VISIBLE
                            firebase.tab!!.orCreateBadge.number = 1
                        }
                    }

                })


        }

        }




         fun loadImageBitmap(context:Context, name:String, extension:String): Bitmap {
             val names = name + "." + extension
             val fileInputStream: FileInputStream
             try{
                 fileInputStream = context.openFileInput(names);
                 firebase.bitmap = BitmapFactory.decodeStream(fileInputStream);
                 fileInputStream.close();
             } catch( e:Exception) {
                 e.printStackTrace();
             }
             return firebase.bitmap!!;
         }

}