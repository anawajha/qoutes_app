package com.anawajha.quotes.admin

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.*
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anawajha.quotes.R
import com.anawajha.quotes.model.Quote
import com.bumptech.glide.util.Util.getSnapshot
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import database.Database
import kotlinx.android.synthetic.main.fragment_quotes.*
import kotlinx.android.synthetic.main.fragment_quotes.view.*
import kotlinx.android.synthetic.main.layout_nodata.*
import kotlinx.android.synthetic.main.layout_nodata.rlNoData
import kotlinx.android.synthetic.main.quote_design.view.*
import org.jetbrains.annotations.NotNull

class QuotesFragment : Fragment() {
    lateinit var database: Database
    lateinit var sharedPreferences: SharedPreferences

    var fire: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null

    var adapterQuotes: FirestorePagingAdapter<Quote, QuotesViewHolder>? = null

    class QuotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val quote = itemView.tv_quote
        val favorite = itemView.btn_addToFavorites
        val copy = itemView.btn_copy
        val share = itemView.btn_share
        var edit = itemView.btn_edit
        var delelte = itemView.btn_delete
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_quotes, container, false)

        fire = Firebase.firestore
        auth = Firebase.auth

        getQuotes()
        root.a_rv_quotes.layoutManager = LinearLayoutManager(requireContext())
        root.a_rv_quotes.adapter = adapterQuotes

        return root
    }


//    private fun getQuotes() {
//        var product_id: String? = null
//        fire!!.collection("quotes").whereEqualTo("category", arguments?.getString("id")).get().addOnSuccessListener {
//            if (it.count() > 0) {
//                lo_NoData.visibility = View.GONE
//            } else {
//                lo_NoData.visibility = View.VISIBLE
//            }
//        }
//
//
//        val query = fire!!.collection("quotes").whereEqualTo("category", arguments?.getString("id"))
//                val options =
//                    FirestoreRecyclerOptions.Builder<Quote>().setQuery(query, Quote::class.java)
//                        .build()
//                adapterQuotes =
//                    object : FirestoreRecyclerAdapter<Quote, QuotesViewHolder>(options) {
//                        override fun onCreateViewHolder(
//                            parent: ViewGroup,
//                            viewType: Int
//                        ): QuotesViewHolder {
//                            val view = LayoutInflater.from(activity)
//                                .inflate(R.layout.quote_design, parent, false)
//                            return QuotesViewHolder(view)
//                        }
//
//
//                        override fun onBindViewHolder(
//                            holder: QuotesViewHolder,
//                            position: Int,
//                            model: Quote
//                        ) {
//                            database = Database(requireContext())
//
//                            holder.quote.text = model.quote
//
//                            sharedPreferences = requireActivity().getSharedPreferences(
//                                "quote_app",
//                                AppCompatActivity.MODE_PRIVATE
//                            )
//                            if (sharedPreferences.getBoolean("isLogin", false)) {
//                                holder.edit.visibility = View.VISIBLE
//                                holder.delelte.visibility = View.VISIBLE
//                                holder.share.visibility = View.GONE
//                                holder.favorite.visibility = View.GONE
//                                holder.copy.visibility = View.GONE
//
//
//                            } else {
//                                holder.edit.visibility = View.GONE
//                                holder.delelte.visibility = View.GONE
//                                holder.share.visibility = View.VISIBLE
//                                holder.favorite.visibility = View.VISIBLE
//                                holder.copy.visibility = View.VISIBLE
//                            }
//
//                            if (database.isFavorite(getId(position))) {
//                                holder.favorite.setImageResource(R.drawable.ic_favorite)
//                                holder.favorite.setOnClickListener {
//                                    database.delete(getId(position))
//                                    Toast.makeText(
//                                        activity,
//                                        requireActivity().getString(R.string.delete_from_favorite),
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                    holder.favorite.setImageResource(R.drawable.ic_favorite_outline)
//                                }
//                            } else {
//                                holder.favorite.setOnClickListener {
//                                    database.insertQuote(getId(position), model.quote.toString())
//                                    Log.d("d", model.id.toString())
//                                    holder.favorite.setImageResource(R.drawable.ic_favorite)
//                                    Toast.makeText(
//                                        activity,
//                                        getString(R.string.added_to_favorite),
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                            }
//
//
//                            holder.copy.setOnClickListener {
//                                var clipBoard: ClipboardManager =
//                                    activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                                var clip =
//                                    ClipData.newPlainText("Copied Text", model.quote.toString())
//                                clipBoard.setPrimaryClip(clip)
//                                Toast.makeText(
//                                    activity,
//                                    requireActivity().getString(R.string.copied_ext),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//
//                            holder.share.setOnClickListener {
//                                val intent = Intent(Intent.ACTION_SEND)
//                                intent.putExtra(Intent.EXTRA_TEXT, model.quote.toString())
//                                intent.type = "text/plain"
//                                activity!!.startActivity(intent)
//                            }
//
//                            holder.edit.setOnClickListener {
//                                val bundle = Bundle()
//                                bundle.putString("id", getId(position))
//                                val frag = EditQuoteFragment()
//                                frag.arguments = bundle
//                                activity!!.supportFragmentManager.beginTransaction()
//                                    .replace(R.id.lo_fragment_container_admin, frag).commit()
//                            }
//
//                            holder.delelte.setOnClickListener {
//                                val alertDialog = AlertDialog.Builder(activity)
//                                alertDialog.setTitle(getString(R.string.delete_quote))
//                                alertDialog.setMessage(getString(R.string.are_you_sure_delete))
//                                alertDialog.setIcon(R.drawable.ic_delete_forever)
//                                alertDialog.setCancelable(false)
//
//                                alertDialog.setPositiveButton(getString(R.string.delete)) { d, c ->
//                                    fire!!.collection("quotes")
//                                        .whereEqualTo(FieldPath.documentId(), getId(position)).get()
//                                        .addOnSuccessListener {
//                                            fire!!.collection("quotes")
//                                                .document(it.documents.get(0).id).delete()
//                                                .addOnSuccessListener {
//                                                    updtaeQuotesNumber1()
//                                                    snackBar(
//                                                        getString(R.string.deleted_successfully),
//                                                        Snackbar.LENGTH_SHORT
//                                                    )
//                                                }
//                                        }
//                                }
//
//                                alertDialog.setNegativeButton(getString(R.string.cancel)) { d, c ->
//                                    d.cancel()
//                                }
//                                alertDialog.create().show()
//                            }
//
//                        }
//
//
//                        fun getId(position: Int): String {
//                            val id = snapshots.getSnapshot(position).reference.id
//                            return id
//                        }
//                    }
//    }



    fun updtaeQuotesNumber(category: String){
        var number = 0
        fire!!.collection("quotes").whereEqualTo("category",category).get().addOnSuccessListener {
            number = it.count()
        }

        fire!!.collection("categories").whereEqualTo(FieldPath.documentId(), category).get()
            .addOnSuccessListener {
                fire!!.collection("categories").document(it.documents.get(0).id).update("quotesNumber",number)
            }.addOnFailureListener {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    override fun onStart() {
        adapterQuotes!!.startListening()
        super.onStart()
    }

    override fun onStop() {
        adapterQuotes!!.stopListening()
        super.onStop()
    }

    fun snackBar(msg: String, length: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(requireActivity().findViewById(android.R.id.content), msg, length).show()
    }



    fun updtaeQuotesNumber1(){
        fire!!.collection("categories").get().addOnSuccessListener { document ->
            for (doc in document) {

                var cat = doc["title"]
                fire!!.collection("quotes").whereEqualTo("category",cat).get().addOnSuccessListener {quotes ->
                    Log.d("count", "$cat ${quotes.count()}")
                    fire!!.collection("categories").whereEqualTo("title", cat).get().addOnSuccessListener { update ->
                        fire!!.collection("categories").document(update.documents.get(0).id).update("quotesNumber",quotes.count())
                    }.addOnFailureListener {
                        Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    fun getQuotes(){
        var qoute_Id: String? = null
        fire!!.collection("quotes").whereEqualTo("category", arguments?.getString("id")).orderBy("timestamp",
            Query.Direction.DESCENDING) .get().addOnSuccessListener {
            if (it.count() > 0) {
                lo_NoData.visibility = View.GONE
            } else {
                lo_NoData.visibility = View.VISIBLE
            }
        }


        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPrefetchDistance(2)
            .setPageSize(6)
            .build()

        val query = fire!!.collection("quotes").whereEqualTo("category", arguments?.getString("id"))
        val options =
            FirestorePagingOptions.Builder<Quote>()
                .setLifecycleOwner(this)
                .setQuery(query,config,Quote::class.java)
                .build()

//        object :SnapshotParser<Quote>{
//            override fun parseSnapshot(snapshot: DocumentSnapshot): Quote {
//                var quote = snapshot.toObject(Quote::class.java)
//                var ID = snapshot.id
//                qoute_Id = ID
//                return quote!!
//            }
//        }

        adapterQuotes =
            object : FirestorePagingAdapter<Quote, QuotesViewHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): QuotesViewHolder {
                    val view = LayoutInflater.from(activity)
                        .inflate(R.layout.quote_design, parent, false)
                    return QuotesViewHolder(view)
                }


                override fun onBindViewHolder(
                    holder: QuotesViewHolder,
                    position: Int,
                    model: Quote
                ) {
                    database = Database(requireContext())

                    holder.quote.text = model.quote
                    qoute_Id = model.id

                    sharedPreferences = requireActivity().getSharedPreferences(
                        "quote_app",
                        AppCompatActivity.MODE_PRIVATE
                    )
                    if (sharedPreferences.getBoolean("isLogin", false)) {
                        holder.edit.visibility = View.VISIBLE
                        holder.delelte.visibility = View.VISIBLE
                        holder.share.visibility = View.GONE
                        holder.favorite.visibility = View.GONE
                        holder.copy.visibility = View.GONE


                    } else {
                        holder.edit.visibility = View.GONE
                        holder.delelte.visibility = View.GONE
                        holder.share.visibility = View.VISIBLE
                        holder.favorite.visibility = View.VISIBLE
                        holder.copy.visibility = View.VISIBLE
                    }

//                    if (database.isFavorite(qoute_Id!!)) {
//                        holder.favorite.setImageResource(R.drawable.ic_favorite)
//                        holder.favorite.setOnClickListener {
//                            database.delete(qoute_Id!!)
//                            Toast.makeText(
//                                activity,
//                                requireActivity().getString(R.string.delete_from_favorite),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            holder.favorite.setImageResource(R.drawable.ic_favorite_outline)
//                        }
//                    } else {
//
//                        holder.favorite.setOnClickListener {
//                            database.insertQuote(qoute_Id!!, model.quote.toString())
//                            Log.d("d", model.id.toString())
//                            holder.favorite.setImageResource(R.drawable.ic_favorite)
//                            Toast.makeText(
//                                activity,
//                                getString(R.string.added_to_favorite),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }


                    holder.copy.setOnClickListener {
                        var clipBoard: ClipboardManager =
                            activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        var clip =
                            ClipData.newPlainText("Copied Text", model.quote.toString())
                        clipBoard.setPrimaryClip(clip)
                        Toast.makeText(
                            activity,
                            requireActivity().getString(R.string.copied_ext),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    holder.share.setOnClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.putExtra(Intent.EXTRA_TEXT, model.quote.toString())
                        intent.type = "text/plain"
                        activity!!.startActivity(intent)
                    }


                }

                override fun onError(e: Exception) {
                    super.onError(e)
                    Log.e("MainActivity", e.message.toString())
                }

                override fun onLoadingStateChanged(state: LoadingState) {
                    when (state) {
                        LoadingState.LOADING_INITIAL -> {
//                            swipeRefreshLayout.isRefreshing = true
                        }

                        LoadingState.LOADING_MORE -> {
//                            swipeRefreshLayout.isRefreshing = true
                        }

                        LoadingState.LOADED -> {
//                            swipeRefreshLayout.isRefreshing = false
                        }

                        LoadingState.ERROR -> {
                            Toast.makeText(
                                context,
                                "Error Occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
//                            swipeRefreshLayout.isRefreshing = false
                        }

                        LoadingState.FINISHED -> {//                            swipeRefreshLayout.isRefreshing = false

                        }
                    }
                }

//
//                fun getId(position: Int): String {
//                    val id = getSnapshot(position).reference.id
//                    return id
//                }
                }

            }

}