package com.example.whatsapclone.adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.whatsapclone.R
import com.example.whatsapclone.activity.Settings
import com.example.whatsapclone.firebase.firebase.userID
import com.example.whatsapclone.model.postModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.firebase.ui.database.paging.DatabasePagingOptions
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter
import com.firebase.ui.database.paging.LoadingState
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.post_item.view.*
import java.lang.Exception
import kotlin.system.exitProcess

class feedsAdapter(var model: DatabasePagingOptions<postModel>,   var mSwipeRefreshLayout:SwipeRefreshLayout,
                   var shimmer:ShimmerFrameLayout,
                   var context:Context): FirebaseRecyclerPagingAdapter
<postModel, feedsAdapter.viewHolder>(model) {

    inner class viewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val body= itemView.post_body
        val title= itemView.post_title
        val image= itemView.post_image
        val userimage= itemView.post_userimage
        val username= itemView.post_username
        var love=itemView.post_lovepost
        val hate= itemView.post_hatepost
        var option1= itemView.option1
        var option2= itemView.option2
        var option3= itemView.option3
        var likes= itemView.post_likes
        var hates=itemView.post_hates


    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.post_item,parent,false)
        return viewHolder(view)
    }


    fun setCheck(CB_1:MaterialCheckBox, CB_2:MaterialCheckBox, CB_3:MaterialCheckBox){
        CB_1 .setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                CB_2.setChecked(false)
                CB_3.setChecked(false)
            }
        }
        CB_2.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                CB_1.setChecked(false)
                CB_3.setChecked(false)
            }
        }
        CB_3.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                CB_2.setChecked(false)
                CB_1.setChecked(false)
            }
        }
    }

    fun addHate(key:String, id:String,image:ImageView,love:ImageView){
        val database= FirebaseDatabase.getInstance().reference.child("hates").child(key).child(id)
        database.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                print("")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    removeHate(key,id,image)
                }
                else{
                    removelove(key,id,love)
                    database.setValue(id).addOnSuccessListener {
                        image.setBackgroundColor(context.resources.getColor(R.color.colorPrimaryDark))

                    }
                }
            }

        })

    }

    fun removeHate(key:String, id:String,image: ImageView){
        var database= FirebaseDatabase.getInstance().reference.child("hates")
            .child(key).child(id).removeValue().addOnSuccessListener {
                image.setBackgroundColor(context.resources.getColor(R.color.transparent))
            }
    }
    fun addlove(key:String, id:String,image: ImageView,hate:ImageView){
        val database= FirebaseDatabase.getInstance().reference.child("loves").child(key).child(id)
        database.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
               print("")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
              if(snapshot.exists()){
                  removelove(key,id,image)
              }
                else{
                  removeHate(key,id,hate)
                  database.setValue(id).addOnSuccessListener {
                      image.setColorFilter(Color.RED)
                      image.setBackgroundColor(context.resources.getColor(R.color.colorPrimaryDark))
                  }
              }
            }

        })

    }
    fun removelove(key:String, id:String, image: ImageView){
        var database= FirebaseDatabase.getInstance().reference.child("loves").child(key)
            .child(id).removeValue().addOnSuccessListener {
                image.setColorFilter(context.resources.getColor(R.color.colorPrimary))
                image.setBackgroundColor(context.resources.getColor(R.color.transparent))
            }
    }

    fun getLoves(view: TextView,id:String,image: ImageView,key: String):String{
        var count="0"
        var database= FirebaseDatabase.getInstance().reference.child("loves").child(key).addValueEventListener(object :
        ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                print("")

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                count=snapshot.childrenCount.toString()
                var list= snapshot.children
                view.text=count + " Likes"

            }

        })
        var database2= FirebaseDatabase.getInstance().reference.child("loves").child(key).child(id)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    print("")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        image.setBackgroundColor(context.resources.getColor(R.color.colorPrimaryDark))
                        image.setColorFilter(Color.RED)
                    }

                }})
        return count
    }
    fun getHates(view:TextView,id: String,image: ImageView,key: String):String{
        var count="0"
        var database= FirebaseDatabase.getInstance().reference.child("hates").child(key)
            .addValueEventListener(object :   ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                print("")

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                count=snapshot.childrenCount.toString()
                var list= snapshot.children
                view.text=count + " Hates"

            }

        })
        var database2= FirebaseDatabase.getInstance().reference.child("hates").child(key).child(id)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    print("")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        image.setBackgroundColor(context.resources.getColor(R.color.colorPrimaryDark))
                    }

                }})
        return count
    }


    override fun onBindViewHolder(holder: viewHolder, position: Int, post: postModel) {
        holder.body.text= post.body
        holder.title.text = post.title
        val ref= getRef(position)
        val dbHate= FirebaseDatabase.getInstance().reference.child("hates").child(post.userid!!)
        val dbLove= FirebaseDatabase.getInstance().reference.child("loves").child(post.userid!!)
        getLoves(holder.likes,FirebaseAuth.getInstance().uid!!,holder.love,getRef(position).key!!)
        getHates(holder.hates,FirebaseAuth.getInstance().uid!!,holder.hate,getRef(position).key!!)
        setCheck(holder.option1,holder.option2,holder.option3)
        if(post.optionslist!=null){
            for(i in 0 until post.optionslist!!.size){
                when(i){
                    0->{
                        holder.option1.text=post.optionslist!![0].title
                        holder.option1.visibility=View.VISIBLE
                        holder.itemView.one.visibility=View.VISIBLE
                    }
                    1->{
                        holder.option2.text=post.optionslist!![1].title
                        holder.option2.visibility=View.VISIBLE
                        holder.itemView.two.visibility=View.VISIBLE
                    }
                    2->{
                        holder.option3.text=post.optionslist!![2].title
                        holder.option3.visibility=View.VISIBLE
                        holder.itemView.three.visibility=View.VISIBLE
                    }
                }
            }
        }

        holder.userimage.setOnClickListener {
            YoYo.with(Techniques.Shake).playOn(holder.userimage)
            userID= post.userid
            val Intents= Intent(context, Settings::class.java)
            Intents.putExtra("user",true)
            context.startActivity(Intents)
            Animatoo.animateShrink(context)
        }
        holder.hate.setOnClickListener {
            YoYo.with(Techniques.Shake).duration(1000).interpolate(BounceInterpolator()).playOn(holder.hate)
            dbHate.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    print("")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        removeHate(ref.key!!,FirebaseAuth.getInstance().uid!!,holder.hate)
                        dbHate.removeEventListener(this)
                    }
                    else{
                        addHate(ref.key!!,FirebaseAuth.getInstance().uid!!,holder.hate,holder.love)
                        dbHate.removeEventListener(this)
                    }
                }

            })

        }

        holder.love.setOnClickListener {
            YoYo.with(Techniques.BounceInUp).duration(1000).interpolate(BounceInterpolator()).playOn(holder.love)
                dbLove.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        print("")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            removelove(ref.key!!,FirebaseAuth.getInstance().uid!!,holder.love)
                            dbLove.removeEventListener(this)
                            Toast.makeText(context,"UnLoved",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            addlove(ref.key!!,FirebaseAuth.getInstance().uid!!,holder.love,holder.hate)
                            dbLove.removeEventListener(this)
                            Toast.makeText(context,"Loved",Toast.LENGTH_SHORT).show()
                        }
                    }

                })
        }

        if(post.image!=null)
            Glide.with(context).load(post.image).into(holder.image)
        if(post.color!=null)
            holder.image.setBackgroundColor(post.color!!)
        fetchUserDetails(holder.userimage,holder.username,post.userid!!)
    }

    fun fetchUserDetails(image:ImageView, text:TextView, userid:String){
        var data = FirebaseDatabase.getInstance().reference.child("users").child(userid)
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



    override fun onLoadingStateChanged( state: LoadingState) {
        when (state) {
            LoadingState.LOADING_INITIAL-> {
                shimmer.startShimmer()
                shimmer.visibility=View.VISIBLE
                mSwipeRefreshLayout.setRefreshing(true)
            }

            LoadingState.LOADING_MORE -> {
                shimmer.startShimmer()
                shimmer.visibility=View.VISIBLE
            }
            // Do your loading animation

            LoadingState.LOADED -> {
                // Stop Animation
                mSwipeRefreshLayout.setRefreshing(false)
                shimmer.stopShimmer()
                shimmer.hideShimmer()
                shimmer.visibility=View.GONE
            }

            LoadingState. FINISHED -> {
            //Reached end of Data set
            mSwipeRefreshLayout.setRefreshing(false)
                shimmer.stopShimmer()
                shimmer.hideShimmer()
                shimmer.visibility=View.GONE
            }

            LoadingState. ERROR -> {
                var dialog= MaterialAlertDialogBuilder(context)
                dialog.setTitle("ERROR")
                dialog.setMessage("It seems an error occurred")
                dialog.setPositiveButton("RETRY",DialogInterface.OnClickListener{dialog, which->
                    retry()
                    dialog.dismiss()
                })
                dialog.setNegativeButton("QUIT", DialogInterface.OnClickListener { dialogs, which ->
                    dialogs.dismiss()
                    exitProcess(0)
                })

            } }
    }
}