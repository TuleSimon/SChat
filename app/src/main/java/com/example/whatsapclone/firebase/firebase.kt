package com.example.whatsapclone.firebase

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import com.example.whatsapclone.model.userModel
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Job
import org.joda.time.Days
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.collections.HashMap

object  firebase {

    var bitmap: Bitmap ?= null
    var resultUri: Uri?=null
    var userID: String ?=null
    var tab:TabLayout.Tab?=null
    var firebaseauth: FirebaseAuth =FirebaseAuth.getInstance()
    var firebaseDatabase=FirebaseDatabase.getInstance()
    var userDetails:HashMap<String,Any> ?=null
    var mauth:FirebaseAuth.AuthStateListener ?= null
    var thumbnail:Bitmap ?=null

    lateinit  var progressDialog: ProgressDialog

    var currentuser: FirebaseUser?=null

    fun login(email:String, password:String, text:TextInputLayout, job: Job):Boolean{
        var value=false
        firebaseauth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task: Task<AuthResult> ->
            if(task.isSuccessful){
                currentuser= firebaseauth.currentUser
                try{
                    progressDialog.dismiss()
                }
                catch (e:Exception){

                }

              //  Toast.makeText(progressDialog.context, "Logged In",Toast.LENGTH_LONG).show()
                value=true
                job.start()
            }

            if(task.isCanceled){
                try{
                    Toast.makeText(progressDialog.context, task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                text.error= task.exception!!.message.toString()
                progressDialog.dismiss()
                value=false
            }
                catch (e:Exception){

                }
            }
            }



        return value
    }

    fun logout():Boolean{
        return true

    }
    var show=true

    fun createAccount(username:String, email:String, password:String, phone:String):Boolean{
        firebaseauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task: Task<AuthResult> ->
            if(task.isSuccessful){
                currentuser= firebaseauth.currentUser
                userID= currentuser!!.uid

                val userDatabase= firebaseDatabase.reference.child("users").child(userID!!)

                val user = userModel()
                user.username=username
                user.profilePicture="profilepix"
                user.thumb="thumb"
                user.phone=phone
                user.status="Hello There"

                userDatabase.setValue(user).addOnCompleteListener { task: Task<Void> -> progressDialog.dismiss()
                Toast.makeText(progressDialog.context,"Sucessful",Toast.LENGTH_LONG).show()
                    populate()
                }

            }

            else{
                Toast.makeText(progressDialog.context, task.exception!!.message.toString(),Toast.LENGTH_LONG).show()

            }
        }
        return true
    }

    fun ifLoggedIn():Boolean{
        return true

    }



    fun createProgressDialog(context:Context, title:String, message:String){
        progressDialog= ProgressDialog(context)
        progressDialog.setTitle(title)
        progressDialog.setMessage(message)
        progressDialog.show()
    }

    fun populate(profileUsername: MaterialTextView?) {
        val database = firebase.firebaseDatabase
        val reference = database.reference.child("users").child(currentuser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(data: DataSnapshot) {
                userDetails = data.value as HashMap<String, Any>
                profileUsername!!.text = userDetails!!.get("username").toString()

            }

        })
    }



    fun populate() {
        val reference = FirebaseDatabase.getInstance() .reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(progressDialog.context,"Cancel ${error.message} ${error.details}",Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(data: DataSnapshot) {
                userDetails = data.value as HashMap<String, Any>

            }

        })
    }

    var task:Task<Void>?=null




    fun String.isEmailValid(): Boolean {
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,8}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(this)
        return matcher.matches()
    }

    fun convertDate(date:Date):String{
    var days= TimeUnit.MILLISECONDS.toDays(Calendar.getInstance() .timeInMillis - date.time ).toInt()
        if(days>1){
        return SimpleDateFormat("MMMM d,y", Locale.ENGLISH).format(date)}
        else if(days==1){
            return "yesterday " + SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(date)
        }
        return  "Today " + SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(date)
    }
}