package com.example.whatsapclone.activity

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import at.blogc.android.views.ExpandableTextView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.whatsapclone.R
import com.example.whatsapclone.adapters.tagAdapter
import com.example.whatsapclone.model.postModel
import com.example.whatsapclone.model.tagModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_reviewpost.*
import java.util.*
import kotlin.collections.ArrayList


class Reviewpost : AppCompatActivity() {

    var title: String? = null
    var body: String? = null
    var link: String? = null
    var list: ArrayList<tagModel>? = null
    var list2: ArrayList<tagModel>? = null
    var listener: RequestListener<Drawable>? = null
    var database= FirebaseDatabase.getInstance().reference.child("allpost")
    var database2= FirebaseDatabase.getInstance().reference.child("mypost")
    var colors:Int ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviewpost)

        review_backbutton.setOnClickListener { finish() }
        val extra = intent.extras
        if (extra != null) {
            title = extra.getString("title")
            body = extra.getString("body")
            if (extra.getString("image") != null) {
                link = extra.getString("image")
                Glide.with(this).load(link).into(review_app_bar_image)
            }
            try{
                if(extra.get("color")!=null)
                colors = extra.getInt("color")
                review_app_bar_image.setBackgroundColor(colors!!)
            }
            catch (e:Exception){

            }

        }
        expandable_text.text = body
        review_title .text = title
        list = ArrayList()
        list2 = ArrayList()
        review_tagsrecyclerview.adapter = tagAdapter(list!!, this)
        review_tagsrecyclerview.layoutManager = GridLayoutManager(this, 2)
        review_addTag.setOnClickListener {
            val tag = review_entertag.text.toString().trim()
            if (tag.length > 2) {
                var add = true
                for (i in list!!.iterator()) {
                    if (i.title.equals(tag, true))
                        add = false
                }
                if (add) {
                    list!!.add(tagModel(tag))
                    review_tagsrecyclerview.adapter!!.notifyItemInserted(list!!.size - 1)
                    review_tagsrecyclerview.adapter!!.notifyDataSetChanged()
                }
                else{
                    Toast.makeText(this,"You have already added this", Toast.LENGTH_LONG).show()
                }
            }
        }

        review_Optionrecyclerview.adapter = tagAdapter(list2!!, this)
        review_Optionrecyclerview.layoutManager = GridLayoutManager(this, 2)
        review_addOptions.setOnClickListener {
            val tag = review_enteroptions.text.toString().trim()
            if (tag.length > 0 || tag.length <10 && list2!!.size<2) {
                var add = true
                for (i in list2!!.iterator()) {
                    if (i.title.equals(tag, true))
                        add = false
                }
                if (add) {
                    list2!!.add(tagModel(tag))
                    review_Optionrecyclerview.adapter!!.notifyItemInserted(list!!.size - 1)
                    review_Optionrecyclerview.adapter!!.notifyDataSetChanged()
                }
                else{
                    Toast.makeText(this,"You have already added this", Toast.LENGTH_LONG).show()
                }
            }
            else{
                YoYo.with(Techniques.Shake).playOn(review_addOptions)
            }
        }



        val expandableTextView = expandable_text
        val buttonToggle: Button = button_toggle

// set animation duration via code, but preferable in your layout files by using the animation_duration attribute

// set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        expandableTextView.setAnimationDuration(750L)

        // set interpolators for both expanding and collapsing animations

        // set interpolators for both expanding and collapsing animations
        expandableTextView.setInterpolator(OvershootInterpolator())

// or set them separately

// or set them separately
        expandableTextView.expandInterpolator = OvershootInterpolator()
        expandableTextView.collapseInterpolator = OvershootInterpolator()
        buttonToggle.setOnClickListener {

            if (expandableTextView.isExpanded) {
                expandableTextView.collapse()
                buttonToggle.background= getDrawable( R.drawable.ic_dropdown)
            } else {
                expandableTextView.expand()
                buttonToggle.background= getDrawable( R.drawable.ic_up)
            }
        }


        review_entertag.onFocusChangeListener =  View.OnFocusChangeListener { v, hasFocus ->
            val xx= (review_entertag.parent.parent.parent as (View)).bottom + (review_entertag.parent as (View)).bottom
            review_nestedscroll.smoothScrollTo(0,xx)
            review_appbar.setExpanded(false,true)
            if (expandableTextView.isExpanded) {
                expandableTextView.collapse()
                buttonToggle.background = getDrawable(R.drawable.ic_dropdown)

            }
        }
        review_enteroptions.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            val xx= (review_entertag.parent.parent.parent as (View)).top + (review_enteroptions.parent as (View)).top

            review_nestedscroll.smoothScrollTo(0,xx)
            if (expandableTextView.isExpanded) {
                review_appbar.setExpanded(false,true)
                expandableTextView.collapse()
                buttonToggle.background = getDrawable(R.drawable.ic_dropdown)

            }
        }

        review_button.setOnClickListener {
            click()
        }
    }

    fun click() {
        if (list!!.size > 0 && list2!!.size > 1 || list2!!.size==0 && list!!.size>0) {
            var dialpog=ProgressDialog(this)
            dialpog.setTitle("Posting")
            dialpog.setMessage("Please wait")
            dialpog.show()
            val post = postModel()
            post.body = expandable_text.text.toString()
            if (link != null)
                post.image = link
            if(colors!=null)
                post.color=colors
            post.tag = list
            post.userid = FirebaseAuth.getInstance().uid
            post.title = review_title.text.toString()
            post.date = Calendar.getInstance().time
            post.optionslist=list2

            val push = database.push()
            val ref = push.key
            push.setValue(post).addOnSuccessListener {
                database2.child(ref!!).setValue(ref).addOnSuccessListener {
                    Animatoo.animateSlideLeft(this)
                    var intents=Intent(this,DashBoard()::class.java)
                    dialpog.dismiss()
                    startActivity(intents)
                    finish()

                }
            }
        }
        else{
            if(list!!.size<1){
            val dialogs= MaterialAlertDialogBuilder(this,R.style.MaterialAlertDialog_Rounded)
            dialogs.setTitle("NO TAGS")
            dialogs.setMessage("You haven't added any tags, Tags are needed for easy accessibility")
            dialogs.setIcon(getDrawable( R.drawable.ic_error))
            dialogs.setPositiveButton("OKAY", object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                   dialog!!.dismiss()
                }
            })
            dialogs.create().show()
        }

        else if(list2!!.size<2){
            val dialogs= MaterialAlertDialogBuilder(this,R.style.MaterialAlertDialog_Rounded)
            dialogs.setTitle("Incorrect Options")
            dialogs.setMessage("You must add at least 2 options, So users can answer properly.")
            dialogs.setIcon(getDrawable( R.drawable.ic_error))
            dialogs.setPositiveButton("OKAY", object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                }
            })
            dialogs.create().show()
        }
    }
    }


}


