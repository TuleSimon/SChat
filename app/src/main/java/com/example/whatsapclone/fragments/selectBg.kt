package com.example.whatsapclone.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.whatsapclone.R
import com.example.whatsapclone.adapters.selectbgAdapter
import com.example.whatsapclone.adapters.selectbgAdapter2
import com.example.whatsapclone.model.bgModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_selectbg.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    editDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */


class selectBg : BottomSheetDialogFragment() {

    var interfaces: OnBgClick?=null
    var options: FirebaseRecyclerOptions<bgModel> ?=null
    var adapter:selectbgAdapter ?= null
    var mOnClickListener:View.OnClickListener ?=null
    var storage= FirebaseStorage.getInstance().getReference("backgrounds")
    var storage2= FirebaseStorage.getInstance().getReference("mybg")
        .child(FirebaseAuth.getInstance().uid!!)
    var bglist=ArrayList<bgModel>()
    var bglist2=ArrayList<bgModel>()

    override fun onStart() {
        super.onStart()
//        (this as BottomSheetDialog).dismissWithAnimation = true
        CoroutineScope(Main).launch {

        }
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

    }

    override fun onDetach() {
        super.onDetach()
        interfaces=null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage .listAll() .addOnCompleteListener {
            val list= it.result!!.items
            bglist.clear()
            bglist.add(bgModel())
            for(i in list.iterator()){
                if(i.name.endsWith(".jpg",true) || i.name.endsWith(".png",true)){
                    i.downloadUrl.addOnSuccessListener {
                        val models= bgModel()
                        models.imageLink=it.toString()
                        bglist.add(models)
                        if(bg_recycler!=null){
                            adapter= selectbgAdapter(bglist,context!!,interfaces!!)
                        bg_recycler.adapter!!.notifyDataSetChanged()
                        bg_progressBar.visibility=View.GONE

                        }
                    } }

            }
        }
        populate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_selectbg, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter= selectbgAdapter(bglist,context!!,interfaces!!)
        bg_recycler.layoutManager= GridLayoutManager(context,3)
        bg_recycler.adapter= adapter

        bg_searchBut.setOnClickListener { sort(bg_searcheditText.text.toString().trim()) }
        bg_searcheditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
               if(s!!.length<1)
                   bg_clear.visibility= View.GONE
                else
                   bg_clear.visibility= View.VISIBLE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if(s!!.length<1)
                    bg_clear.visibility= View.GONE
                else
                    bg_clear.visibility= View.VISIBLE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.length<1)
                    bg_clear.visibility= View.GONE
                else
                    bg_clear.visibility= View.VISIBLE
            }

        })

        bg_clear.setOnClickListener { bg_searcheditText.text.clear()
        sort("")
        }
    }

    fun sort(string:String){
        bg_searchBut.isEnabled=false
        bg_searchprogressBar.visibility= View.VISIBLE
        storage .listAll() .addOnCompleteListener {
            val list= it.result!!.items
            bglist.clear()
            bglist.add(bgModel())
            for(i in list.iterator()){
                if(i.name.endsWith(".jpg",true) && i.name.contains(string,true) ||
                            i.name.endsWith(".png",true) && i.name.contains(string,true)
                ){
                    i.downloadUrl.addOnSuccessListener {
                        val models= bgModel()
                        models.imageLink=it.toString()
                        bglist.add(models)
                        Toast.makeText(context,models.imageLink,Toast.LENGTH_SHORT).show()
                        adapter= selectbgAdapter(bglist,context!!,interfaces!!)
                        bg_recycler.layoutManager= GridLayoutManager(context,3)
                        bg_recycler.adapter= adapter
                        adapter!!.notifyDataSetChanged()
                        bg_searchBut.isEnabled=true
                        bg_searchprogressBar.visibility= View.GONE
                    } }

            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnBgClick){
            interfaces= context
           }
         else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }

    }

    interface OnBgClick{
        fun onImageClick(link:String?,vals:Boolean)
    }


    fun populate(){
        storage2 .listAll() .addOnCompleteListener {
            val list= it.result!!.items
            bglist2.clear()
            for(i in list.iterator()){
                val models= bgModel()
                    i.downloadUrl.addOnSuccessListener {
                        models.imageLink=it.toString()
                        models.name= i.name
                        if(!bglist2.contains(models))
                            bglist2.add(models)
                        if(bg_recycler2!=null){
                            bg_recycler2.adapter= selectbgAdapter2(bglist2,context!!,interfaces!!)
                            bg_recycler2.layoutManager= GridLayoutManager(context,3)
                            bg_recycler2.adapter!!.notifyDataSetChanged()
                        }
                    } }

            }
        }


}
