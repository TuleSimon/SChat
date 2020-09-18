package com.example.whatsapclone.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.whatsapclone.R
import com.example.whatsapclone.firebase.firebase
import com.example.whatsapclone.firebase.firebase.bitmap
import com.github.florent37.viewanimator.ViewAnimator
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_view_profile_image.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class viewProfileImage : AppCompatActivity() {
    override fun onStart() {
        super.onStart()

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(viewprofile_image, "translationY", -900f, 0f),
            ObjectAnimator.ofFloat(login_login, "scaleY", -900f, 0f),
            ObjectAnimator.ofFloat(logincardview, "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(logincardview, "scaleX", -900f, 0f)
        )
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.duration = 1000
        animatorSet.start()
        Blurry.with(this).from(bitmap).into(viewprofile_bg)
        Palette.Builder(bitmap!!).generate { it?.let { palette ->
            val dominantColor = palette.getDominantColor(ContextCompat.getColor(this, R.color.colorPrimary))
            getSupportActionBar()!!.setBackgroundDrawable(ColorDrawable(dominantColor));


        } }

    }

    override fun onPostResume() {
        super.onPostResume()

    }

    override fun onPause() {
        super.onPause()
        Animatoo.animateFade(this)
        CoroutineScope(Main).launch {
            dos()
        }
    }

    suspend fun dos(){
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(viewprofile_image, "translationY", 0f, -300f),
            ObjectAnimator.ofFloat(login_login, "scaleY", -0f, 1900f),
            ObjectAnimator.ofFloat(logincardview, "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(logincardview, "scaleX", 0f, -1900f)
        )
        animatorSet.interpolator = AccelerateInterpolator()
        animatorSet.duration = 1000
        animatorSet.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile_image)
        viewprofile_image.setImageBitmap(bitmap)
    }
}
