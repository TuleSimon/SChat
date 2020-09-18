package com.example.whatsapclone.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.whatsapclone.R
import com.example.whatsapclone.adapters.usersAdapter
import com.example.whatsapclone.firebase.firebase.firebaseDatabase
import com.example.whatsapclone.model.onlineModel
import com.example.whatsapclone.model.userModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activemessagesrow.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.users_fragments.*

/**
 * A simple [Fragment] subclass.
 */
class UsersFragments : Fragment() {
    var database: DatabaseReference?=null
    var options: FirebaseRecyclerOptions<userModel> ?=null
    var adapter: usersAdapter ?= null
    var contexts:Context?=null
    var linearLayoutManager:LinearLayoutManager ?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contexts=context
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.users_fragments, container, false)
    }

  init {
      val userDatabase= FirebaseDatabase.getInstance().reference.child("users")
      options = FirebaseRecyclerOptions.Builder <userModel> ()
          .setQuery(userDatabase,  userModel::class.java) .build()
      userDatabase.keepSynced(true)
  }


    override fun onResume() {
        val userDatabase= FirebaseDatabase.getInstance().reference.child("users")
        options = FirebaseRecyclerOptions.Builder <userModel> ()
            .setQuery(userDatabase,  userModel::class.java) .build()
        adapter=usersAdapter(options!!,context!!,context as Activity)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter=usersAdapter(options!!,context!!, context as Activity)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        users_recyclerview.setHasFixedSize(true);
        users_recyclerview.layoutManager =linearLayoutManager;
        users_recyclerview.adapter= adapter
        adapter!!.startListening()
        Toast.makeText(context,"view created ${adapter!!.itemCount}",Toast.LENGTH_LONG).show()
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }





}
