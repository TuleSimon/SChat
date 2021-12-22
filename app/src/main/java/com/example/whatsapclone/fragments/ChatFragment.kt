package com.example.whatsapclone.fragments

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

import com.example.whatsapclone.R
import com.example.whatsapclone.adapters.activechatsAdapter
import com.example.whatsapclone.adapters.usersAdapter
import com.example.whatsapclone.dialogs.Notifications
import com.example.whatsapclone.firebase.firebase
import com.example.whatsapclone.firebase.firebase.bitmap3
import com.example.whatsapclone.model.activeChats
import com.example.whatsapclone.model.messageModel
import com.example.whatsapclone.model.userModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.activity_view_profile_image.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.users_fragments.*

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {


    var database: DatabaseReference?=null
    var options: FirebaseRecyclerOptions<activeChats> ?=null
    var adapter: activechatsAdapter ?= null
    var contexts: Context?=null
    val userDatabase= FirebaseDatabase.getInstance().reference.child("activeChats").child(FirebaseAuth.getInstance().currentUser!!.uid)
    var linearLayoutManager:LinearLayoutManager ?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    init {
        val userDatabase= FirebaseDatabase.getInstance().reference.child("activeChats").child(FirebaseAuth.getInstance().currentUser!!.uid)
            userDatabase.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChildren())
                        spin_kit2.visibility = View.INVISIBLE
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


        userDatabase.keepSynced(true)
        options = FirebaseRecyclerOptions.Builder <activeChats> ()
            .setQuery(userDatabase,  activeChats::class.java) .build()
    }




    override fun onResume() {
        val userDatabase= FirebaseDatabase.getInstance().reference.child("activeChats").child(FirebaseAuth.getInstance().currentUser!!.uid)

        userDatabase.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if (snapshot.hasChildren() && snapshot.exists()) {

                        actie_startChat.visibility = View.GONE
                        active_emptytitle.visibility = View.GONE
                    } else {
                        actie_startChat.visibility = View.VISIBLE
                        active_emptytitle.visibility = View.VISIBLE
                    }
                }
                catch (e:Exception){
                    print("")
                }
            }

        })
        options = FirebaseRecyclerOptions.Builder <activeChats> ()
            .setQuery(userDatabase,  activeChats::class.java) .build()
        adapter=activechatsAdapter(options!!,context!!)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter=activechatsAdapter(options!!,context!!)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        activeChats_recyclerview.setHasFixedSize(true);
        activeChats_recyclerview.layoutManager =linearLayoutManager;
        activeChats_recyclerview.adapter= adapter
        userDatabase.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                print("")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChildren() && actie_startChat !=null ){
                    actie_startChat.visibility= View.GONE
                    active_emptytitle.visibility= View.GONE
                }
                else{
                    if( actie_startChat !=null ){
                    actie_startChat.visibility= View.VISIBLE
                    active_emptytitle.visibility= View.VISIBLE}
                }
            }

        })


        adapter!!.startListening()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        adapter!!.startListening()

        super.onStart()
    }

    override fun onStop() {
        adapter!!.stopListening()
        super.onStop()
    }



}
