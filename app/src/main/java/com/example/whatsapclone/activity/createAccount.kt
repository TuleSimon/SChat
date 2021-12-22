package com.example.whatsapclone.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog

import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.whatsapclone.R
import com.example.whatsapclone.dialogs.Dialogs
import com.example.whatsapclone.firebase.firebase
import com.example.whatsapclone.firebase.firebase.isEmailValid
import com.example.whatsapclone.model.userModel
import com.github.florent37.viewanimator.ViewAnimator
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import kotlinx.android.synthetic.main.activity_create_account.*
import java.util.regex.Pattern

class createAccount : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        create_createbutton.setOnClickListener(this)
    }


    override fun onPause() {
        super.onPause()

    }

    lateinit var dialogs: MaterialDialog

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.create_createbutton->{
                if(!hasError()) {
                    Snackbar.make(create_createbutton, "Logging in", Snackbar.LENGTH_LONG).show()
                    dialogs = Dialogs().ShowProgressDialogs(this)
                    createAccount(
                        signup_enterusername.text.toString().trim(),
                        signup_enteremail.text.toString().trim(),
                        signup_createpassword.text.toString(),
                        signup_enterphone.text.toString()
                    )


                }


            }
        }
    }

    fun createAccount(username:String, email:String, password:String, phone:String):Boolean{
        firebase.firebaseauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task: Task<AuthResult> ->
            if(task.isSuccessful){
                firebase.currentuser = firebase.firebaseauth.currentUser
                firebase.currentuser!!.sendEmailVerification()
                firebase.userID = firebase.currentuser!!.uid

                val userDatabase= firebase.firebaseDatabase.reference.child("users").child(firebase.userID!!)

                val user = userModel()
                user.username=username
                user.profilePicture="profilepix"
                user.thumb="thumb"
                user.phone=phone
                user.status="Hello There"

                userDatabase.setValue(user).addOnCompleteListener { task: Task<Void> ->
                    dialogs.dismiss()
                    Toast.makeText(
                        this,"Sucessfully created account",
                        Toast.LENGTH_LONG).show()
                    firebase.populate()
                    Dialogs().ShowDoneDialogs (this, "Account Created Successfully","Check your Email and Verify Your Email before Logging In")
                }

            }

            else{
                dialogs.dismiss()
                Toast.makeText(
                    this, task.exception!!.message.toString(),
                    Toast.LENGTH_LONG).show()
                Dialogs().showErrorDialog("Error Occurred", task.exception!!.message.toString(),this)
            }
        }
        return true
    }

    fun hasError():Boolean{
        var errorFound=false
        signup_enteremail_parent.isErrorEnabled=false
        signup_enterusername_parent.isErrorEnabled=false
        signup_verifypassword_parent.isErrorEnabled=false
        signup_createpassword_parent.isErrorEnabled=false

        if(signup_enterusername.text!!.length<3){
            errorFound=true
            signup_enterusername_parent.error="Invalid Username!!"
            YoYo.with(Techniques.Tada).duration(1000).playOn(signup_enterusername_parent)
        }

        else if(signup_enteremail.text!!.isEmpty() || !signup_enteremail.text.toString().isEmailValid()) {
            errorFound = true

            signup_enteremail_parent.error = "Enter Email"
            YoYo.with(Techniques.Tada).duration(1000).playOn(signup_enteremail_parent)
        }

        else if(signup_createpassword.text!!.length<6){
            errorFound=true
            signup_createpassword_parent.error="assword Must Be at Least 6 Characters Long"
            YoYo.with(Techniques.Tada).duration(1000).playOn(signup_createpassword_parent)
        }
        else if(signup_verifypassword.text.toString() != signup_createpassword.text.toString()){
            errorFound=true
            signup_verifypassword_parent.error="Password Doesnt Match"
            YoYo.with(Techniques.Tada).duration(1000).playOn(signup_verifypassword_parent)
        }
        else if(signup_enterphone.text!!.length<5 ){
            errorFound=true
            signup_enterphone_parent.error="Enter a Valid Number"
            YoYo.with(Techniques.Tada).duration(1000).playOn(signup_enterphone_parent)
        }

        if(errorFound)
            return true
        return false

    }




}
