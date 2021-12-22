package com.example.whatsapclone.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsapclone.Authentication
import com.example.whatsapclone.R
import com.example.whatsapclone.firebase.firebase
import com.example.whatsapclone.firebase.firebase.mauth
import com.github.florent37.viewanimator.ViewAnimator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception


class MainActivity : AppCompatActivity() {



    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    override fun onStart() {
        super.onStart()
        firebase.firebaseauth.addAuthStateListener(mauth!!)

    }


    override fun onStop() {
        super.onStop()
        firebase.firebaseauth.removeAuthStateListener(mauth!!)
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideSystemUI()
        mauth = FirebaseAuth.AuthStateListener {
            var user = it.currentUser
            if (it.currentUser?.email != null &&  it.currentUser?.isEmailVerified!!) {
                it.currentUser!! .getIdToken(true).addOnCompleteListener {
                    if(it.isSuccessful){
                        startActivity(Intent(this, DashBoard::class.java))
                    finish()}
                    else{
                        startActivity(Intent(this, Authentication::class.java))
                        finish()
                    }
                }
            }
            else{
                startActivity(Intent(this, Authentication::class.java))
                finish()
            }
        }
    }


    }
