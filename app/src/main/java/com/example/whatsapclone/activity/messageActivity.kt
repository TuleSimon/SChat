package com.example.whatsapclone.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.whatsapclone.R
import com.example.whatsapclone.adapters.messageAdapter
import com.example.whatsapclone.encryption.AESCrptoChst
import com.example.whatsapclone.firebase.firebase
import com.example.whatsapclone.model.activeChats
import com.example.whatsapclone.model.messageModel
import com.example.whatsapclone.model.onlineModel
import com.example.whatsapclone.model.userModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_message.*
import java.util.*

class messageActivity : AppCompatActivity() {

    var database: DatabaseReference ?=null
    var options:FirebaseRecyclerOptions<messageModel> ?=null
    var adapter:messageAdapter ?=null
    var layoutmanager: LinearLayoutManager ?= null
    var uid:String ?=null
    var listener: ValueEventListener?=null
    var aes: AESCrptoChst = AESCrptoChst("lv39eptlvuhaqqsr")

    var listener2: ValueEventListener?=null
    var database2: DatabaseReference?=null
    var database3: DatabaseReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        if(intent.extras!=null){
            val thumb=intent.extras!!.get("thumb").toString()
            uid=intent.extras!!.get("uid").toString()
            Glide.with(this).load(thumb) .into(message_userIcon)
            val userDatabase= FirebaseDatabase.getInstance().reference.child("users").child(uid!!).addListenerForSingleValueEvent(
                object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        print("")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(userModel::class.java)
                        message_username.text = user!!.username
                    }})

            setSupportActionBar(messagetoolbar)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!! .setHomeButtonEnabled(true)
        }
        val database= FirebaseDatabase.getInstance().reference.child("messages").child(FirebaseAuth.getInstance().currentUser!!.uid).child(uid!!)
        options= FirebaseRecyclerOptions.Builder<messageModel>().setQuery(database, messageModel::class.java) .build()
        database2= FirebaseDatabase.getInstance().reference.child("messages")
            .child(uid!!).child(FirebaseAuth.getInstance().currentUser!!.uid)
        database3=  FirebaseDatabase.getInstance().reference.child("activeChats").child(uid!!)
            .child(FirebaseAuth.getInstance().currentUser!!.uid!!)

        listener2=object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                print("")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                val snao=snapshot.getValue(activeChats::class.java)
                if(snao!!.userid!=FirebaseAuth.getInstance().currentUser!!.uid){
                    Toast.makeText(this@messageActivity,"Setting as tru", Toast.LENGTH_LONG).show()

                    FirebaseDatabase.getInstance().reference.child(
                        "activeChats")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid!!).child(uid!!).
                        child("seen") .setValue(true)
                    database3!!.removeEventListener(this)
                }}
            }}

        listener= object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                print("")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach {
                    val databases =
                        FirebaseDatabase.getInstance().reference.child("messages")
                            .child(uid!!)
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(it.key!!).child("seen")
                            .setValue(true)
                }
                database3!!.addValueEventListener(listener2!!)

            }

            }

        adapter = messageAdapter(options!!, uid!!,this,database2!!,listener!!)
        layoutmanager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        layoutmanager!!.stackFromEnd=true
        messageRecyclerView.setHasFixedSize(true)
        messageRecyclerView.layoutManager= layoutmanager
        messageRecyclerView.adapter = adapter
        messageRecyclerView.requestFocus()
        adapter!!.startListening()
        message_sendMessage.setOnClickListener {
            sendMessage()
        }
        database.keepSynced(true)

        database.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {


            }

            override fun onDataChange(snapshot: DataSnapshot) {
                messageRecyclerView.scrollToPosition(snapshot.childrenCount.toInt()-1)
            }

        })

        val onlineDatabase= FirebaseDatabase.getInstance().reference.child("onlineStatus").child(
            uid!!)

        onlineDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                print("")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var data = snapshot.getValue(onlineModel::class.java)
                if (data != null) {
                    if (data.online == "online") {
                      message_status.text = data.online
                    }
                    else{
                        message_status.text = firebase.convertDate( data.lastseen)
                    }
                }
            }
        })


    }
    override fun onStart() {
        adapter = messageAdapter(options!!, uid!!,this,database2!!,listener!!)
        adapter!!.notifyDataSetChanged()
        adapter!!.startListening()
        database2!!.addValueEventListener(listener!!)
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
        finish()
    }

    override fun onPause() {
        database2!!.removeEventListener(listener!!)
        database3!!.removeEventListener(listener2!!)
        super.onPause()
    }

    private fun sendMessage(){
        if(message_enterMessage.text!!.isNotEmpty()) {
            YoYo.with(Techniques.Shake).duration(1000) .playOn(message_sendMessage)
            val message = messageModel()
            message.date = Calendar.getInstance().time
            message.sender = FirebaseAuth.getInstance().currentUser!!.uid
            message.receiver = uid
            val encryptedMessage = aes.encrypt(message_enterMessage.text.toString().trim())
            message.message = encryptedMessage
            message.seen = false

            val activeChat = activeChats()
            activeChat.lastMessage = message.message
            activeChat.date = message.date!!
            activeChat.userid = FirebaseAuth.getInstance().currentUser!!.uid
            activeChat.receiver=uid

            val activeChat2 = activeChats()
            activeChat2.lastMessage = message.message
            activeChat2.date = message.date!!
            activeChat2.userid = FirebaseAuth.getInstance().currentUser!!.uid
            activeChat2.receiver=uid
             FirebaseDatabase.getInstance().reference.child("messages")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child(uid!!)
                .push() .setValue(message) .addOnCompleteListener {
                    if (it.isSuccessful) {
                        var active = FirebaseDatabase.getInstance().reference.child("activeChats")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(uid!!).setValue(activeChat).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    var data2 =
                                        FirebaseDatabase.getInstance().reference.child("messages")
                                            .child(uid!!)
                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                            .push().setValue(message).addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    var active2 =
                                                        FirebaseDatabase.getInstance().reference.child(
                                                            "activeChats"
                                                        ).child(uid!!)
                                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                            .setValue(activeChat2)
                                                            .addOnCompleteListener {
                                                                if (it.isSuccessful) {
                                                                    Toast.makeText(
                                                                        applicationContext,
                                                                        "Message Sent",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()

                                                                }
                                                            }
                                                }
                                            }
                                                }
                                            }
                                    }
                                }




        }
        message_enterMessage.text!!.clear()
    }
}
