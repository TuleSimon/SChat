package com.example.whatsapclone.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.whatsapclone.R
import com.example.whatsapclone.firebase.firebase
import com.example.whatsapclone.firebase.firebase.bitmap
import com.example.whatsapclone.firebase.firebase.bitmap2
import com.github.florent37.viewanimator.ViewAnimator
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_view_profile_image.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class viewProfileImage : AppCompatActivity() {

    override fun onStart() {
        super.onStart()


    }

    override fun onPostResume() {
        super.onPostResume()

    }

    override fun onPause() {
        super.onPause()
        CoroutineScope(Main).launch {
            dos()
        }
    }

    suspend fun dos(){
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(viewprofile_image, "translationY", 0f, -300f)
            )
        animatorSet.interpolator = AccelerateInterpolator()
        animatorSet.duration = 1000
        animatorSet.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile_image)
        var mode = intent.extras!!.getString("mode")
        when(mode) {
            "settings" -> {

                viewprofile_image.setImageBitmap(bitmap)
                val animatorSet = AnimatorSet()
                animatorSet.playTogether(
                    ObjectAnimator.ofFloat(viewprofile_image, "translationY", -900f, 0f)
                )
                animatorSet.interpolator = DecelerateInterpolator()
                animatorSet.duration = 1000
                animatorSet.start()
                if (bitmap != null) {
                    Blurry.with(this).from(bitmap).into(viewprofile_bg)
                    Palette.Builder(bitmap!!).generate {
                        it?.let { palette ->
                            val dominantColor =
                                palette.getDominantColor(
                                    ContextCompat.getColor(
                                        this,
                                        R.color.colorPrimary
                                    )
                                )
                            getSupportActionBar()!!.setBackgroundDrawable(
                                ColorDrawable(
                                    dominantColor
                                )
                            );

                        }
                    }
                }
            }
            else -> {
                if (mode!!.length > 3) {


                    val listener: RequestListener<Drawable> = object : RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Toast.makeText(this@viewProfileImage,"Failed to load Image", Toast.LENGTH_LONG).show()
                            //Glide.with(this@Settings).load(thumb).into(settings_profileimage)
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            bitmap2= resource!!.toBitmap()
                            viewprofile_image.setImageBitmap(bitmap2)
                            Blurry.with(this@viewProfileImage).from(bitmap2) .into(viewprofile_bg)

                            return true
                        }
                    }

                    Glide.with(this).load(mode).listener(listener).into(viewprofile_image)
                    val animatorSet = AnimatorSet()
                    animatorSet.playTogether(
                        ObjectAnimator.ofFloat(viewprofile_image, "translationY", -900f, 0f)
                    )
                    animatorSet.interpolator = DecelerateInterpolator()
                    animatorSet.duration = 1000
                    animatorSet.start()
                    if (bitmap2 != null) {
                        Blurry.with(this).from(bitmap2) .into(viewprofile_bg)
                        Palette.Builder(bitmap2!!).generate {
                            it?.let { palette ->
                                val dominantColor =
                                    palette.getDominantColor(
                                        ContextCompat.getColor(
                                            this,
                                            R.color.colorPrimary
                                        )
                                    )
                                getSupportActionBar()!!.setBackgroundDrawable(
                                    ColorDrawable(
                                        dominantColor
                                    )
                                );

                            }
                        }
                    }
                }

            }
        }

    }
}
