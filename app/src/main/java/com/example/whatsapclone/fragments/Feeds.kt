package com.example.whatsapclone.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.whatsapclone.R
import com.example.whatsapclone.activity.createPost
import com.example.whatsapclone.adapters.feedsAdapter
import com.example.whatsapclone.model.postModel
import com.firebase.ui.database.paging.DatabasePagingOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_feeds.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [post.newInstance] factory method to
 * create an instance of this fragment.
 */
class Feeds : Fragment() {

    var options: DatabasePagingOptions<postModel>?=null
    var adapter: feedsAdapter?= null
    var contexts: Context?=null
    var database= FirebaseDatabase.getInstance().reference.child("allpost")
    var linearLayoutManager: LinearLayoutManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feeds, container, false)
    }

    init {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(5)
            .setPageSize(5)
            .build()
      database.keepSynced(true)
        options = DatabasePagingOptions.Builder <postModel> ().setLifecycleOwner(this)
            .setQuery(database, config, postModel::class.java) .build()
        database.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                print("error")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
              if(!snapshot.hasChildren()){
                  mSwipeRefreshLayout.visibility = View.GONE
                schimmer.visibility= View.GONE
              }

            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter= feedsAdapter(options!!,mSwipeRefreshLayout, schimmer, context!!)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        feeds_recyclerview.setHasFixedSize(true);
        feeds_recyclerview.layoutManager =linearLayoutManager;
        feeds_recyclerview.adapter= adapter
        mSwipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.colorAccent),resources.getColor(R.color.colorPrimary))
        feeds_new.setOnClickListener {
            val intent= Intent(context,createPost::class.java)
            startActivity(intent)
            Animatoo.animateSlideLeft(context)
        }
        feeds_recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy<0){
                    feeds_new.visibility=View.VISIBLE
                }
                else if(dy>0) feeds_new.visibility=View.INVISIBLE


            }


        })
    }

    override fun onStart() {
        adapter!!.startListening()

        super.onStart()
    }

    override fun onStop() {
        adapter!!.stopListening()
        super.onStop()
    }

}
