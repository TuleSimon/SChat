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
import com.blogspot.atifsoftwares.animatoolib.Animatoo
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


class MainActivity : AppCompatActivity(), View.OnClickListener {




    override fun onStart() {
        super.onStart()
        firebase.firebaseauth.addAuthStateListener(mauth!!)
        CoroutineScope(Main).launch {
            animate()
        }
    }


    override fun onStop() {
        super.onStop()
        firebase.firebaseauth.removeAuthStateListener(mauth!!)
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainalreadyhave.setOnClickListener(this)
        maincreateacc.setOnClickListener(this)
        mauth = FirebaseAuth.AuthStateListener {
            var user = it.currentUser
            if (it.currentUser?.email != null) {
                Animatoo.animateSlideLeft(this)
                switchScenes2(DashBoard())

            }
        }
    }


        override fun onResume() {
            super.onResume()
            Animatoo.animateSwipeRight(this)
        }


        suspend fun animate() {

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(
                ObjectAnimator.ofFloat(mainlogo, "translationY", -1000f, 0f),
                ObjectAnimator.ofFloat(mainlogo, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(maintitle, "translationY", -1000f, 0f),
                ObjectAnimator.ofFloat(mainalreadyhave, "alpha", 0f, 0f),
                ObjectAnimator.ofFloat(maincreateacc, "alpha", 0f, 0f),
                ObjectAnimator.ofFloat(maincardview, "translationY", 1300f, 0f)
            )
            animatorSet.interpolator = DecelerateInterpolator()
            animatorSet.duration = 1000
            animatorSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    val animatorSet2 = AnimatorSet()
                    animatorSet2.playTogether(
                        ObjectAnimator.ofFloat(mainalreadyhave, "translationX", -100f, 0f),
                        ObjectAnimator.ofFloat(maincreateacc, "translationX", -50f, 0f),
                        ObjectAnimator.ofFloat(mainalreadyhave, "alpha", 0f, 1f),
                        ObjectAnimator.ofFloat(maincreateacc, "alpha", 0f, 1f)
                        //ObjectAnimator.ofFloat(mainlogo, "scaleY", 1f, 0.5f, 1f)
                    )
                    ViewAnimator().addAnimationBuilder(mainlogo).rotation(360f).duration(1500)
                        .start()
                    animatorSet2.interpolator = OvershootInterpolator()
                    animatorSet2.duration = 1000
                    animatorSet2.start()
                }
            })
            animatorSet.start()
        }

        var loginintents: Intent? = null
        override fun onClick(v: View?) {
            when (v!!.id) {
                R.id.mainalreadyhave -> {
                    firebase.createProgressDialog(this, "Loading", "Please Wait")
                    switchScenes(loginActivity())
                }
                R.id.maincreateacc -> {
                    firebase.createProgressDialog(this, "Loading", "Please Wait")
                    switchScenes(createAccount())
                }

            }

        }

        fun switchScenes(activity: AppCompatActivity) {
            CoroutineScope(Main).launch {
                launchs(activity)
            }
        }

        fun switchScenes2(activity: AppCompatActivity) {
            CoroutineScope(Main).launch {
                launchs(activity)
            }
        }

        suspend fun launchs(activity: AppCompatActivity) {
            loginintents = Intent(this, activity::class.java)
            startActivity(loginintents)
            Animatoo.animateSwipeLeft(this)
            try {
                firebase.progressDialog.dismiss()
            } catch (e: Exception) {

            }
        }

        suspend fun launchs2(activity: AppCompatActivity) {
            loginintents = Intent(this, activity::class.java)
            startActivity(loginintents)
            Animatoo.animateSwipeLeft(this)
            try {
                firebase.progressDialog.dismiss()
            } catch (e: Exception) {

            }
            delay(400)
            finish()
        }


    }
