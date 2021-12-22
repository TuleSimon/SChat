package com.example.whatsapclone.dialogs

import android.content.Context
import android.widget.TextView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog

import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.example.whatsapclone.R
import com.google.android.material.button.MaterialButton


class Dialogs {

    fun showAlertDialog(title:String, Message:String, context: Context){
        MaterialDialog(context).show {
            title(text = title)
            message(text = Message )
            cancelable(false)
            positiveButton (text = "Ok" )
            positiveButton { this.dismiss()
            }
        }
    }

    fun showNoNetworkDialog(title:String, Message:String, context: Context){
        MaterialDialog(context).show {
            title(text = title)
            message(text = Message )
            cancelable(false)
            positiveButton (text = "Ok" )
            icon(R.drawable.ic_no_network)
            positiveButton { this.dismiss() }
        }
    }

    fun showErrorDialog(title:String, Message:String, context: Context){
        MaterialDialog(context).show {
            title(text = title)
            message(text = Message )
            cancelable(false)
            positiveButton (text = "Ok" )
            icon(R.drawable.ic_error)
            positiveButton { this.dismiss() }
        }
    }

    fun getCustomViewDialogs(context: Context, view:Int):MaterialDialog{
        val dialog = MaterialDialog(context)
            .customView(view, scrollable = true)
            .cancelable(false)
                .cornerRadius(10f)
        return dialog
    }

    fun ShowDoneDialogs(context: Context, header:String, message:String):MaterialDialog{
        val dialog = MaterialDialog(context)
            .customView(R.layout.done, scrollable = true)
            .cancelable(false)
            .cornerRadius(10f)

        dialog.getCustomView().findViewById<TextView>(R.id.dialog_header).text = header
        dialog.getCustomView().findViewById<TextView>(R.id.dialog_message).text = message
        dialog.show()
        dialog.getCustomView().findViewById<MaterialButton>(R.id.dialog_dismiss).setOnClickListener {
            dialog.dismiss()
        }
        return dialog
    }
    fun ShowProgressDialogs(context: Context):MaterialDialog{
        val dialog = MaterialDialog(context)
            .customView(R.layout.loading, scrollable = true)
            .cancelable(false)
            .cornerRadius(10f)
        dialog.show()
        return dialog
    }

    fun getAnAlertDialog(title:String, Message:String, context: Context):MaterialDialog{
        var dialog = MaterialDialog(context)
            dialog.show {
            title(text = title)
            message(text = Message )
            positiveButton (text = "Ok" )

        }
        return dialog
    }





}