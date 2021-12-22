package com.example.whatsapclone.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.daimajia.androidanimations.library.attention.TadaAnimator
import com.example.whatsapclone.R
import com.example.whatsapclone.dialogs.Dialogs
import com.example.whatsapclone.firebase.firebase
import com.example.whatsapclone.firebase.firebase.currentuser
import com.example.whatsapclone.firebase.firebase.progressDialog
import com.github.florent37.viewanimator.ViewAnimator
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

lateinit var dialogs: MaterialDialog
class loginActivity : AppCompatActivity() {
    override fun onStart() {
        super.onStart()
        CoroutineScope(Main).launch {
            animate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_loginbutton.setOnClickListener {
            val email= login_enteremail.text.toString().trim()
            val password=login_enterpassword.text.toString().trim()

            if(email.isNotEmpty() && password.isNotEmpty()){
                dialogs = Dialogs().ShowProgressDialogs(this)
                login(email,password)
              }
            else{
                login_enteremail_parent.error="Enter a valid Email Address"
                login_enterpassword_parent.error= "Enter a Password"}

        }
    }

    override fun onPause() {
        super.onPause()
    }


    fun switchScenes(activity: AppCompatActivity){
       CoroutineScope(Dispatchers.Main).launch {
            launchs(activity)
        }

    }

    lateinit var dashBoardIntent:Intent;

    suspend fun launchs(activity: AppCompatActivity){
        dashBoardIntent= Intent(this,activity::class.java)
        startActivity(dashBoardIntent)
        delay(1000)
        finish()
    }


    suspend fun animate(){

        YoYo.with(Techniques.SlideInRight).interpolate(OvershootInterpolator()).onStart {
            YoYo.with(Techniques.SlideInRight).interpolate(OvershootInterpolator()).onStart {
                YoYo.with(Techniques.SlideInRight).interpolate(OvershootInterpolator()).delay(500).playOn(login_loginbutton)
            }.delay(500) .playOn(login_enterpassword_parent)
        }.playOn(login_enteremail_parent)

    }


    fun login(email:String, password:String){
        var value=false
        val firebaseAuth= FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task: Task<AuthResult> ->
            if(task.isSuccessful){

                currentuser= firebaseAuth.currentUser

                try{
                   dialogs.dismiss()
                }
                catch (e:Exception){

                }

                //  Toast.makeText(progressDialog.context, "Logged In",Toast.LENGTH_LONG).show()
                if(currentuser!!.isEmailVerified){
                switchScenes(DashBoard())}
                else{
                    Dialogs().showErrorDialog("Email Not Verified","Please Verify Your Email before Logging In",this)
                    currentuser!!.sendEmailVerification()
                }


            }
            task.addOnFailureListener {
                Toast.makeText(this, task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                login_enterpassword_parent.error= task.exception!!.message.toString()
                dialogs.dismiss()
                value=false
            }
            if(task.isCanceled){
                try{
                    Toast.makeText(this, task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                    login_enterpassword_parent.error= task.exception!!.message.toString()
                    dialogs.dismiss()
                    value=false
                }
                catch (e:Exception){

                }
            }
        }
    }

}
