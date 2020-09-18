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
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.daimajia.androidanimations.library.attention.TadaAnimator
import com.example.whatsapclone.R
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
                firebase.createProgressDialog(this,"Logging In", "Please Wait")
                login(email,password)
              }
            else{
                login_enteremail_parent.error="Enter a valid Email Address"
                login_enterpassword_parent.error= "Enter a Password"}

        }
    }

    override fun onPause() {
        super.onPause()
        Animatoo.animateSwipeRight(this)
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
        Animatoo.animateSplit(this)
        delay(1000)
        finish()
    }


    suspend fun animate(){

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(imageView2, "translationY", -1000f, 0f),
            ObjectAnimator.ofFloat(imageView2, "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(login_login, "translationY", -1000f, 0f),
            ObjectAnimator.ofFloat(logincardview, "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(logincardview, "translationY", 1300f, 0f)
        )
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.duration = 1000
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                val animatorSet2 = AnimatorSet()
                animatorSet2.playTogether(
                    ObjectAnimator.ofFloat(login_enteremail_parent, "translationX", -100f, 0f),
                    ObjectAnimator.ofFloat(login_enterpassword_parent, "translationX", -50f, 0f),
                    ObjectAnimator.ofFloat(login_loginbutton, "translationX", -50f, 0f),
                    ObjectAnimator.ofFloat(mainalreadyhave, "alpha", 0f, 1f),
                    ObjectAnimator.ofFloat(maincreateacc, "alpha", 0f, 1f)

                    //ObjectAnimator.ofFloat(mainlogo, "scaleY", 1f, 0.5f, 1f)
                )
                ViewAnimator().addAnimationBuilder(imageView2).rotation(360f).duration(1500).start()
                ViewAnimator().addAnimationBuilder(login_loginbutton).shake().pulse().duration(1000).start();
                animatorSet2.interpolator = BounceInterpolator()
                animatorSet2.duration = 1000
                animatorSet2.start()
            }
        })
        animatorSet.start()
    }


    fun login(email:String, password:String){
        var value=false
        val firebaseAuth= FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task: Task<AuthResult> ->
            if(task.isSuccessful){
                currentuser= firebaseAuth.currentUser
                try{
                    progressDialog.dismiss()
                }
                catch (e:Exception){

                }

                //  Toast.makeText(progressDialog.context, "Logged In",Toast.LENGTH_LONG).show()
                switchScenes(DashBoard())

            }

            if(task.isCanceled){
                try{
                    Toast.makeText(this, task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                    login_enterpassword_parent.error= task.exception!!.message.toString()
                    progressDialog.dismiss()
                    value=false
                }
                catch (e:Exception){

                }
            }
        }
    }

}
