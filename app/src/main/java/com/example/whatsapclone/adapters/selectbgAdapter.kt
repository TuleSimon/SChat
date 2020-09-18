package com.example.whatsapclone.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.whatsapclone.R
import com.example.whatsapclone.fragments.selectBg
import com.example.whatsapclone.model.bgModel
import kotlinx.android.synthetic.main.bg_item.view.*
import java.io.FileInputStream
import java.io.FileOutputStream

class selectbgAdapter(var model: ArrayList<bgModel>, var context:Context, var click: selectBg.OnBgClick): RecyclerView.Adapter<selectbgAdapter.viewHolder>() {
    override fun getItemCount(): Int {
        return model.size
    }


    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        when (viewType) {
            0->{
                val view = LayoutInflater.from(context).inflate(R.layout.bg_item2, parent, false)
                return viewHolder(view)
            }
            1->{       val view = LayoutInflater.from(context).inflate(R.layout.bg_item, parent, false)
                        return viewHolder(view)
            }
        }
        val view = LayoutInflater.from(context).inflate(R.layout.bg_item, parent, false)
        return viewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        if(position<1)
            return 0
        return 1
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val models = model[position]

        if (position > 0) {
            val image = holder.itemView.bg_item_bg
            val progress = holder.itemView.bg_progressBar
            try{
                val bitma:Bitmap = loadImageBitmap(context, models.imageLink!!,".jpg")
                image.setImageBitmap(bitma)
                image.setOnClickListener {
                    click.onImageClick(models.imageLink, true)
                }
            }
            catch (e:Exception){
                print("")
                Glide.with(context).load(models.imageLink)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            image.setImageDrawable(context.getDrawable(R.drawable.bg2))
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            saveImage(context, resource!!.toBitmap(), models.imageLink, ".jpg")
                            image.setImageDrawable(resource)
                            image.setOnClickListener {
                                click.onImageClick(models.imageLink, true)
                            }
                            progress.visibility = View.GONE
                            return true
                        }

                    }).into(image)
            }


        }
        else{
            holder.itemView.setOnClickListener {
                click.onImageClick(null,false)
            }
        }
    }


    fun saveImage(context: Context, bitmap: Bitmap, name: String?, extension: String) {
        val names = name!!.replace("/","") + "" + extension
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = context.openFileOutput(names, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.close();
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }


    fun loadImageBitmap(context:Context, name:String, extension:String):Bitmap {

        val names = name.replace("/","") + "" + extension
        val fileInputStream:   FileInputStream

            fileInputStream = context.openFileInput(names);
           val bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();

        return bitmap
    }
}

