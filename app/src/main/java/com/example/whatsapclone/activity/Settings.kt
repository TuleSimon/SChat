package com.example.whatsapclone.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.GenericTransitionOptions.with
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.with
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.whatsapclone.R
import com.example.whatsapclone.firebase.firebase
import com.example.whatsapclone.firebase.firebase.bitmap
import com.example.whatsapclone.firebase.firebase.firebaseauth
import com.example.whatsapclone.firebase.firebase.resultUri
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class Settings : AppCompatActivity(), editDialogFragment.OnSaveClick {

    val bottomSheet = editDialogFragment()
    var storage:StorageReference ?=null
    var database:DatabaseReference ?=null
    var value:Boolean ?=null

    override fun onStart() {
        super.onStart()
        CoroutineScope(Main).launch {
            populate()
            firebase.createProgressDialog(this@Settings,"Loading","Please Wait")
        }

    }

    fun showEdit(content:String,title:String){
        CoroutineScope(Main).launch {
          dos(content,title)
        }
    }
    suspend fun dos(content:String,title:String){
        try{bottomSheet.setContentAndtitle(content,title)
        val transaction=supportFragmentManager.beginTransaction()
        bottomSheet.show(transaction,"bottomsheet")}
        catch (e:Exception){

        }
        //bottomSheet
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        storage=FirebaseStorage.getInstance().reference
        database= FirebaseDatabase.getInstance().reference
        setSupportActionBar(Settingstoolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!! .setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_backing)

        val extra= intent.extras
        value= extra!!.getBoolean("user")
        settings_change_profile_pix.setOnClickListener {
            changeImage()
        }
        if(firebase.userID== FirebaseAuth.getInstance().currentUser!!.uid)
            value= false
        if(value!!) {
            settings_change_profile_pix.apply {
                icon = resources.getDrawable(R.drawable.ic_message)
                text = "Send Message"
            }
        }


    }


    override fun onPause() {
        super.onPause()
        Animatoo.animateFade(this)
    }


    suspend fun populate(){

        YoYo.with(Techniques.BounceInUp).duration(1000).playOn(profile_username_Cardview)
        YoYo.with(Techniques.BounceInUp).duration(2000).playOn(profile_about_you_Cardview)
        YoYo.with(Techniques.BounceInUp).duration(3000).playOn(profile_status_Cardview)
        YoYo.with(Techniques.BounceInUp).duration(4000).playOn(profile_phonenumber_Cardview)
        YoYo.with(Techniques.BounceInUp).duration(5000).playOn(profile_email_Cardview)
       try{
           populate2()
           //bitmap=loadImageBitmap(this,firebaseauth.uid!!,"jpg")
            //settings_profileimage.setImageBitmap(bitmap)
       }
       catch (e:Exception){
           Toast.makeText(this,"Erround Found Here",Toast.LENGTH_LONG).show()

       }
    }

    override fun onSaveButonClick(input: TextInputEditText) {

        val value=input.text.toString()
        when (bottomSheet.title){
            "Edit Email" ->  profile_email.setText(value)
            "About You" ->     updateValue("aboutme",value)
            "Edit Username" -> updateValue("username",value)
            "Edit Phone Number" ->  updateValue("phone",value)
            "Edit Status" ->    updateValue("status",value)
        }


    }

    override fun onBackPressed() {
            finishAfterTransition()
            val Intent = Intent(this,DashBoard::class.java)
            startActivity(Intent)

    }

    fun updateValue(child:String, value:String){
        var database2= FirebaseDatabase.getInstance().reference .child("users").child(firebaseauth.uid!!).child(child)
        database2.setValue(value).addOnCompleteListener { task: Task<Void> ->
            if(task.isSuccessful){
                bottomSheet.dismiss()
                populate2()
                Snackbar.make(profile_email_Cardview, "Field will be update Synchronizationally", Snackbar.LENGTH_LONG).apply {
                    animationMode = Snackbar.ANIMATION_MODE_SLIDE
                    setBackgroundTint(resources.getColor( R.color.colorPrimary))
                    show()

                }
            }
            else if(task.isCanceled){
                bottomSheet.dismiss()
                Snackbar.make(profile_email_Cardview, "Canceled Due to ${task.exception}", Snackbar.LENGTH_LONG).apply {
                    animationMode = Snackbar.ANIMATION_MODE_SLIDE
                    setBackgroundTint(resources.getColor( R.color.colorPrimary))
                    show()

                }
            }
        }

    }



    fun populate2() {
        database = FirebaseDatabase.getInstance() .reference.child("users").child(firebase.userID!!)
        database!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                firebase.progressDialog.dismiss()
            }

            override fun onDataChange(data: DataSnapshot) {
                val userDetails = data.value as HashMap<String, Any>
                   set(userDetails)
                firebase.progressDialog.dismiss()

            }

        })
    }
var thumbs=""

    fun set( userDetails:HashMap<String, Any>){
        profile_email.text = FirebaseAuth.getInstance().currentUser!!.email
        profile_username.text = userDetails.get("username").toString()
        profile_status.text=userDetails.get("status").toString()
        profile_aboutYou.text=userDetails.get("aboutme").toString()
        profile_number.text=userDetails.get("phone").toString()
        val thumb = userDetails.get("profilePicture").toString()
        thumbs=userDetails.get("thumb").toString()
        val listener:RequestListener<Drawable> = object : RequestListener<Drawable>{

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                Toast.makeText(this@Settings,"Failed to load Image", Toast.LENGTH_LONG).show()
                Glide.with(this@Settings).load(thumb).into(settings_profileimage)
                return true
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                bitmap= resource!!.toBitmap()
                settings_profileimage.setImageBitmap(bitmap)
                settings_profileimage.setOnClickListener {
                    val intent= Intent(this@Settings,viewProfileImage::class.java)
                    startActivity(intent)
                }
                return true
            }
        }

        if(thumb!="profilepix"){
            Glide.with(this@Settings).load(thumb).into(settings_profileimage)
                Glide.with(this@Settings).load(thumb).listener(listener) .into(settings_profileimage)
        }

        if(!value!! ){
         profile_email.visibility=View.VISIBLE
            settings_emailtext.visibility=View.VISIBLE
        profile_about_you_Cardview.setOnClickListener {
            showEdit(profile_aboutYou.text.toString(),"About You")
        }

        profile_username_Cardview.setOnClickListener {
            showEdit(profile_username.text.toString(),"Edit Username")
        }

        profile_phonenumber_Cardview.setOnClickListener {
            showEdit(profile_number.text.toString(),"Edit Phone Number")
        }

        profile_status_Cardview.setOnClickListener {
            showEdit(profile_status.text.toString(),"Edit Status")
        }
        }
    }

    val GALLERYID=1

    fun changeImage(){
        if(value!!){
            val message=Intent(this, messageActivity::class.java)
            message.putExtra("thumb",thumbs)
            message.putExtra("uid",firebase.userID)
            message.putExtra("name",profile_username.text.toString())
            Animatoo.animateSlideRight(this)
            startActivity(message)
        }
        else{
            val galleryIntent = Intent()
            galleryIntent.type="image/*"
            galleryIntent.action=Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent, "Select_Image"),GALLERYID)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode== GALLERYID && resultCode == Activity.RESULT_OK){
            val image: Uri = data?.data!!
            CropImage.activity(image)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
        try{    val result= CropImage.getActivityResult(data)
            if(resultCode == Activity.RESULT_OK){
                firebase.resultUri= result.uri
                val thumbPath= File(resultUri!!.path!!)
                bitmap= BitmapFactory.decodeFile(thumbPath.path)
                Toast.makeText(this,"Updating Profile Image",Toast.LENGTH_LONG).show()
               val compressedImage=compressImage(thumbPath)
                        saveToFirebase(result.uri,compressedImage,thumbPath)
            }
        }
        catch (e:Exception){
                Toast.makeText(this,"There is an error here",Toast.LENGTH_LONG).show() }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }



    fun compressImage(thumbPath:File):ByteArray{
        var compress:File
        val byteArray = ByteArrayOutputStream()
            val bitmap=BitmapFactory.decodeFile(thumbPath.path)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10,byteArray)
        return byteArray.toByteArray()
    }

   fun saveImage(context:Context, bitmap:Bitmap, name:String, extension:String){
        val names = name + "." + extension
        val  fileOutputStream:FileOutputStream
        try {
            fileOutputStream = context.openFileOutput(names, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.close();
        } catch (e:Exception) {
            e.printStackTrace();
        }
    }


    fun loadImageBitmap(context:Context, name:String, extension:String):Bitmap {
        val names = name + "." + extension
     val fileInputStream:   FileInputStream
        try{
            fileInputStream = context.openFileInput(names);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        } catch( e:Exception) {
            e.printStackTrace();
        }
        return bitmap!!;
    }






    fun saveToFirebase(bigFile:Uri,compressed:ByteArray,file: File){
        settings_uploadProgress.visibility= View.VISIBLE
        settings_change_profile_pix.isEnabled=false
        var PixdownloadURL:String?=null
        var thumbdownloadURL:String?=null
        val ref=storage!!.child("images").child("profilePictures").child(firebaseauth.uid +".jpg")
        val ref2=storage!!.child("images").child("profilePictures").child("thumb").child(firebaseauth.uid +".jpg")
        ref.putFile(bigFile).addOnSuccessListener {  } .addOnCompleteListener { task: Task<UploadTask.TaskSnapshot> ->
            if(task.isSuccessful){
                ref.downloadUrl.addOnSuccessListener {
                    PixdownloadURL=it.toString()
                    val uploadTask = ref2.putBytes(compressed)
                    uploadTask.addOnCompleteListener {
                        if(it.isSuccessful)
                            ref2.downloadUrl.addOnSuccessListener {
                                thumbdownloadURL= it.toString()
                                val user= HashMap<String,Any>()
                                user.put("thumb",thumbdownloadURL!!)
                                user.put("profilePicture",PixdownloadURL!!)
                                database!!.updateChildren(user).addOnCompleteListener {
                                    if(it.isSuccessful){
                                        bitmap = BitmapFactory.decodeFile(file.path)
                                        saveImage(this, bitmap!!, firebaseauth.uid!!,"jpg")
                                        //settings_profileimage.setImageBitmap(bitmap)
                                        Toast.makeText(this,"Profile Image Updated",Toast.LENGTH_LONG).show()
                                        settings_uploadProgress.visibility= View.INVISIBLE;  settings_change_profile_pix.isEnabled=true
                                    }
                                    else
                                        Toast.makeText(this,"Image Not Updated",Toast.LENGTH_LONG).show()
                                }
                            }
                        else {

                            Toast.makeText(this,"Image Not Updated",Toast.LENGTH_LONG).show()
                        }
                    }
                }
                }
            else{
                Toast.makeText(this,"Image Not Updated",Toast.LENGTH_LONG).show()
               }
            }
        }

}


