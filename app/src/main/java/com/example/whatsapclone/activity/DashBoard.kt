package com.example.whatsapclone.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


import com.example.whatsapclone.R
import com.example.whatsapclone.adapters.viewpagerAdapter
import com.example.whatsapclone.dialogs.Notifications
import com.example.whatsapclone.firebase.firebase
import com.example.whatsapclone.fragments.ChatFragment
import com.example.whatsapclone.fragments.UsersFragments
import com.example.whatsapclone.model.activeChats
import com.example.whatsapclone.model.onlineModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.system.exitProcess


class DashBoard : AppCompatActivity() {


    var UsersFragment:Fragment ?=null
    var list:MutableList<Fragment>?=null
    var list2:MutableList<String>?=null
    var storage= FirebaseStorage.getInstance().getReference("backgrounds")



    var notificationManager: NotificationManager?=null
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(){
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(Notifications().CHANNEL_ID,"New message", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager!!.createNotificationChannel(channel)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createChannel()
        }
        val chatFragment=ChatFragment()
        UsersFragment= UsersFragments()
        firebase.bg=bg

        list= mutableListOf(chatFragment,UsersFragment!!)
        list2 = mutableListOf("Messages","Users")


        DashBoard_viewpager.adapter= viewpagerAdapter(this,list!!,list2!!)
        TabLayoutMediator(dashboardTablayout, DashBoard_viewpager){ tab, position ->
            tab.text=list2!!.get(position)
                when(position){
                0 ->   {tab.icon= resources.getDrawable(R.drawable.ic_message)
                setBadge(tab)
                    firebase.tab = tab
                }
                1 -> {  tab.icon= resources.getDrawable(R.drawable.ic_person_low)}
                //0->    { tab.icon= resources.getDrawable(R.drawable.ic_feeds) }
                }

        }.attach()
        val online= FirebaseDatabase.getInstance().getReference(".info/connected")
        val onlineDatabase= FirebaseDatabase.getInstance().reference.child("onlineStatus").child(FirebaseAuth.getInstance().uid!!)
        online.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                print("")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val conneected = snapshot.getValue(Boolean::class.java)
                if(conneected!!){
                    var status=onlineModel()
                    status.online="online"
                    onlineDatabase.setValue(status)
                    Toast.makeText(applicationContext,"Online",Toast.LENGTH_LONG).show()
                    onlineDatabase. onDisconnect().setValue(status.apply {this.online ="offline"
                    lastseen=Calendar.getInstance().time
                    })
                }
            }

        })




    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
        exitProcess(0)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.dashboard_menu,menu)
        return true
    }

    fun setBadge(tab: TabLayout.Tab){

        val userDatabase= FirebaseDatabase.getInstance().reference.child("activeChats")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(
            object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var count=0
                    val list= snapshot.children
                    for(i in list.iterator()){
                        val lists=i.getValue(activeChats::class.java)
                        if(lists!!.receiver==FirebaseAuth.getInstance().currentUser!!.uid)
                            if(!lists.seen){
                                count++}

                    }

                    tab.orCreateBadge.number=count
                    if(count<1)
                        tab.orCreateBadge.isVisible=false
                }

            }
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.dashboardmenu_Settings ->{
                firebase.userID=FirebaseAuth.getInstance().currentUser!!.uid
                switchScenes(Settings())
            }
            R.id.dashboardmenu_logout -> {FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this,MainActivity::class.java)
                )
                finish() }
        }
        return super.onOptionsItemSelected(item)
    }

    private var sc:Job?=null
    fun switchScenes(activity: AppCompatActivity){
        sc= CoroutineScope(Dispatchers.Main).launch {
            launchs(activity)
        }

    }



    lateinit var Intents: Intent;

    suspend fun launchs(activity: AppCompatActivity){
        Intents= Intent(this,activity::class.java)
        Intents.putExtra("user",false)
        startActivity(Intents)
        delay(1000)
    }

    suspend fun fetch(){
    }



}
