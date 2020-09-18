package com.example.whatsapclone.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.LayoutDirection
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.example.whatsapclone.R
import com.example.whatsapclone.activity.Settings
import com.example.whatsapclone.firebase.firebase
import com.example.whatsapclone.model.messageModel
import com.example.whatsapclone.model.userModel
import com.firebase.ui.auth.data.model.User
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.messsage_item.view.*
import kotlinx.android.synthetic.main.messsage_item.view.message_cardview
import kotlinx.android.synthetic.main.messsage_item.view.message_date
import kotlinx.android.synthetic.main.messsage_item.view.message_message
import kotlinx.android.synthetic.main.messsage_item2.view.*
import kotlinx.android.synthetic.main.usersrow.view.*
import java.io.FileInputStream

class messageAdapter(var models: FirebaseRecyclerOptions<messageModel>, var uid:String, var context:Context,
                     var database:DatabaseReference, var listener: ValueEventListener): FirebaseRecyclerAdapter<messageModel, messageAdapter.viewHolder>(models)
     {


        inner class viewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var userImage =itemView.message_userImage
        var messageTextView = itemView.message_message
        var dateTextView = itemView.message_date
            var cardview= itemView.message_cardview
            var seen=itemView.message_seen
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

         override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
             when (viewType){
                1-> {val view= LayoutInflater.from(context).inflate(R.layout.messsage_item,parent,false)
                 return viewHolder(view)
                }
                 0->{
                     val view= LayoutInflater.from(context).inflate(R.layout.messsage_item2,parent,false)
                     return viewHolder(view)
                 }
             }
             val view= LayoutInflater.from(context).inflate(R.layout.messsage_item,parent,false)
             return viewHolder(view)

         }

         override fun getItemViewType(position: Int): Int {
            if(models.snapshots.get(position).sender!!.equals(uid)){
                return 1}
             return 0
         }


         override fun onBindViewHolder(holder: viewHolder, position: Int, model: messageModel) {
             holder.dateTextView.text= firebase.convertDate( model.date!!)
             holder.messageTextView.text = model.message


            if(!model.sender.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {

                 if(models.snapshots.size>position+1 && models.snapshots.get(position+1).sender==uid){
                     holder.userImage.visibility = View.INVISIBLE
                 }
                 else {
                     var database =
                         FirebaseDatabase.getInstance().reference.child("users").child(uid)
                             .addValueEventListener(object : ValueEventListener {
                                 override fun onCancelled(error: DatabaseError) {
                                     print("")
                                 }

                                 override fun onDataChange(snapshot: DataSnapshot) {
                                     val data = snapshot.value as HashMap<String, Any>
                                     val thumb = data.get("thumb")
                                     Glide.with(context).load(thumb).into(holder.userImage)
                                     firebase.show = false

                                     }



                             })

                 }

                   }

             else{
                if(model.seen==true){
                    holder.seen.visibility = View.VISIBLE}
                else{
                    holder.seen.visibility= View.GONE

                }
            }

         }



     }