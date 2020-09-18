package com.example.whatsapclone.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.example.whatsapclone.R
import com.example.whatsapclone.firebase.firebase
import com.example.whatsapclone.fragments.selectBg
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream


class createPost : AppCompatActivity(), selectBg.OnBgClick {
    var bgFrad= selectBg()
     var image:String ?=null
    var colors:Int ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        post_changebg.setOnClickListener {
            click()
        }

        createpost_review.setOnClickListener {
            if(createpost_title.text.length>5 && createpost_body.text!!.length>10){
            val intents=Intent(this,
                Reviewpost::class.java)
            intents.putExtra("title",createpost_title.text.toString())
            intents.putExtra("body",createpost_body.text.toString())
                intents.putExtra("image",image)
                intents.putExtra("color",colors)

            startActivity(intents)
            Animatoo.animateFade(this)
            }

            else{
                Toast.makeText(this,"Please Input Something Reasonable", Toast.LENGTH_LONG).show()
            }

        }
        post_changecolor.setOnClickListener {
            setColor()
        }
    }

    fun click(){
        try{
            val transaction=supportFragmentManager.beginTransaction()
             bgFrad. show(transaction,"bgsheet")}
        catch (e:Exception){

        }
    }
    private val GALLERYID = 3
    override fun onImageClick(link: String?, vals:Boolean) {

        try{
            if(vals) {
                Glide.with(this).load(link).into(post_placeholder)
                image=link
                Toast.makeText(this, "clicked $link", Toast.LENGTH_LONG).show()
                bgFrad.dismiss()
            }
            else{
                val galleryIntent = Intent()
                galleryIntent.type="image/*"
                galleryIntent.action= Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(galleryIntent, "Select_Image"),GALLERYID)
                Toast.makeText(this,"click",Toast.LENGTH_LONG).show()
            }
        }
        catch (e:Exception){
           Toast.makeText(this, "An error occured", Toast.LENGTH_SHORT).show()
        }
    }

    var progress:ProgressDialog ?=null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERYID && resultCode == Activity.RESULT_OK) {
            val images = data!!.data
            CropImage.activity(images)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);
        }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val thumbPath = File(result.uri.path!!)
                    progress = ProgressDialog(this)
                    progress!!.setCancelable(false)
                    progress!!.setCanceledOnTouchOutside(false)
                    progress!!.setTitle("Uploading Image")
                    progress!!.setMessage("Please Wait, this might take some time")
                    progress!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                    progress!!.max=100
                    progress!!.show()
                    CoroutineScope(Main).launch {
                        saveToFirebase(result.uri, thumbPath)
                    }
                }
            }



    }

    fun compressImage(thumbPath:Uri,size:Int):ByteArray{
        var compress:File
        val byteArray = ByteArrayOutputStream()
        val bitmap=BitmapFactory.decodeFile(thumbPath.path)
        bitmap.compress(Bitmap.CompressFormat.JPEG, size,byteArray)
        return byteArray.toByteArray()
    }

    fun loadImageBitmap(context: Context, name:String, extension:String): Bitmap {
        val names = name + "." + extension
        val fileInputStream: FileInputStream
        try{
            fileInputStream = context.openFileInput(names);
            firebase.bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        } catch( e:Exception) {
            e.printStackTrace();
        }
        return firebase.bitmap!!;
    }




    fun setColor(){
        MaterialColorPickerDialog
            .Builder(this)        				// Pass Activity Instance
            .setColorShape(ColorShape.SQAURE)   	// Default ColorShape.CIRCLE
            .setColorSwatch(ColorSwatch._300)
            .setColors(							// Pass Predefined Hex Color
                arrayListOf(
                    "#05B5EA", "#F1CD02", "#035291", "#8208E6", "#E608D0",
                    "#08E622", "#D4E608", "#E67308"
                )
            )
            // Default ColorSwatch._500
            .setTitle("Select Background Color")
            // Pass Default Color
            .setColorListener { color, colorHex ->
                post_placeholder.setImageResource(0)
                colors=color
                post_placeholder.setBackgroundColor(colors!!)
            }
            .show()
    }




    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    suspend fun saveToFirebase(bigFile:Uri, file:File){
       // val byte2= compressImage(bigFile,2)
       // val byte= compressImage(bigFile,30)
        val files=Compressor.compress(this,file) {
            resolution(180, 180)
            quality(70)
            format(Bitmap.CompressFormat.JPEG)
            size(98_102) // 2 MB
        }
        val name=getFileName(bigFile)!!.replace(" ","")

        val ref=FirebaseStorage.getInstance().reference.child("mybg").child(
            FirebaseAuth.getInstance().uid.toString() ).child(name)
        val ref2=FirebaseStorage.getInstance().reference.child("mybg").child(
            FirebaseAuth.getInstance().uid.toString() ).child("thumb_"+name+".jpg")
       val tasks =ref .putFile(Uri.fromFile(files)) .addOnCompleteListener { task: Task<UploadTask.TaskSnapshot> ->

            if(task.isSuccessful){
                ref.downloadUrl.addOnSuccessListener {
                              image=it.toString()
                           Toast.makeText(this, "sucess + $image",Toast.LENGTH_LONG).show()
                            Glide.with(this@createPost).load(it).into(post_placeholder)
                              progress!!.dismiss()
                               bgFrad.dismiss()
                }

                    }
        }
        tasks.addOnProgressListener { taskSnapshot: UploadTask.TaskSnapshot ->
            val progres = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            progress!!.progress=progres.toInt()
        }
    }



}
