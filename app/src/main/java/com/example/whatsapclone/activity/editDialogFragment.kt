package com.example.whatsapclone.activity

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.whatsapclone.R
import com.example.whatsapclone.firebase.firebase.isEmailValid
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.fragment_edit_dialog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    editDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */


class editDialogFragment : BottomSheetDialogFragment() {
    var content:String="Edit SOmething"
    var title="Edit"
    var value:String?=null

    var interfaces: OnSaveClick ?=null

    var mOnClickListener:View.OnClickListener ?=null



    override fun onStart() {
        super.onStart()

//        (this as BottomSheetDialog).dismissWithAnimation = true
        CoroutineScope(Main).launch {
            edit_sheet_content.setText(content)
            edit_sheet_title.text= title
            append()
            edit_sheet_save_button.setOnClickListener {
                if(!check())
                    interfaces!!.onSaveButonClick(edit_sheet_content); edit_sheet_progress.visibility= View.VISIBLE
            }
        }


    }

    suspend fun append(){
        when (edit_sheet_title.text.toString()){
            "Edit Email" ->   edit_sheet_content.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
            "About You" ->   edit_sheet_content.inputType = InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE
            "Edit Username" ->   edit_sheet_content.inputType = InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE
            "Edit Phone Number" ->   edit_sheet_content.inputType = InputType.TYPE_CLASS_NUMBER
            "Edit Status" ->   edit_sheet_content.inputType = InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE
        }

        edit_sheet_content.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                edit_sheet_save_button.isEnabled = !check()


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
               print("")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                print("")
            }

        })


    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        edit_content_parent.isErrorEnabled = false
    }

    override fun onDetach() {
        super.onDetach()
        interfaces=null
    }

     fun check():Boolean{
        var errorFound=false
         edit_content_parent.isErrorEnabled = false
        when (edit_sheet_title.text.toString()){
            "Edit Email" ->  {
                            if( edit_sheet_content.text!!.isEmpty() || ! edit_sheet_content.text.toString().isEmailValid()) {
                            errorFound = true
                            edit_content_parent.error = "Invalid Email"
                            YoYo.with(Techniques.Tada).duration(1000).playOn(edit_content_parent)
                            }
            }
            "About You","Edit Status" ->   {
                if( edit_sheet_content.text!!.isEmpty() ||  edit_sheet_content.text!!.length < 3) {
                    errorFound = true
                    edit_content_parent.error = "Enter A Readable Text"
                    YoYo.with(Techniques.Tada).duration(1000).playOn(edit_content_parent)
                }
            }

            "Edit Username"  ->    {
                edit_content_parent.counterMaxLength=10
                if(edit_sheet_content.text!!.length < 3 || edit_sheet_content.text!!.length > 13 || edit_sheet_content.text.toString().contains(" ",true)){
                    errorFound = true
                    edit_content_parent.error = "Enter A Valid Username"
                    YoYo.with(Techniques.Tada).duration(1000).playOn(edit_content_parent)
                }
            }

            "Edit Phone Number" ->    {
                if( edit_sheet_content.text!!.isEmpty() ||  edit_sheet_content.text!!.length < 5) {
                    errorFound = true
                    edit_content_parent.error = "Enter A Valid Phone Number"
                    YoYo.with(Techniques.Tada).duration(1000).playOn(edit_content_parent)
                }
            }
        }
        return errorFound
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_dialog, container, false)
    }

    fun setContentAndtitle(content:String, title:String){
       this.content=content
        this.title=title
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnSaveClick){
            interfaces= context
           }
         else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    interface OnSaveClick{
        fun onSaveButonClick(input:TextInputEditText)
    }



}
