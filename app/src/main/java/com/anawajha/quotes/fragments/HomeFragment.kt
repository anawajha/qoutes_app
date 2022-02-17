package com.anawajha.quotes.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anawajha.quotes.MainActivity
import com.bumptech.glide.Glide
import com.anawajha.quotes.R
import com.anawajha.quotes.admin.*
import com.anawajha.quotes.model.Category
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.category_design.view.*
import kotlinx.android.synthetic.main.fragment_home2.view.*
import kotlinx.android.synthetic.main.view_no_internet.*
import kotlinx.android.synthetic.main.view_no_internet.view.*

class HomeFragment : Fragment() {
    lateinit var sharedPreferences: SharedPreferences
    var fire: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    var adapterCategories: FirestoreRecyclerAdapter<Category, ProductsViewHolder>? = null
    lateinit var progressDialog: ProgressDialog
    lateinit var connectivityManager: ConnectivityManager
     var isConnected:Boolean = false



    class ProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var category = itemView.tv_category
        var image = itemView.img_category
        var q_num = itemView.tv_quotesNumber
        var cv_product = itemView.cv_category
        var more = itemView.img_more
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home2, container, false)

        fire = Firebase.firestore
        auth = Firebase.auth

//        if (checkConnection()){
//            root.lottieNoInternet.visibility = View.GONE
//        }else             root.lottieNoInternet.visibility = View.VISIBLE
//
//        root.tv_retry.setOnClickListener {
//            if (checkConnection()){
//                root.lottieNoInternet.visibility = View.GONE
//            }else             root.lottieNoInternet.visibility = View.VISIBLE
//        }


        getAllcategories()
        root.rv_category.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        root.rv_category.adapter = adapterCategories

        return root
    }


    private fun getUser_id() {
        auth!!.currentUser!!.uid
    }


    private fun getAllcategories() {
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle(getString(R.string.loading))
        progressDialog.show()

        var product_id: String? = null

        val query = fire!!.collection("categories")
        val options =
            FirestoreRecyclerOptions.Builder<Category>().setQuery(query, Category::class.java)
                .build()
        adapterCategories =
            object : FirestoreRecyclerAdapter<Category, ProductsViewHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ProductsViewHolder {
                    val view = LayoutInflater.from(activity)
                        .inflate(R.layout.category_design, parent, false)

                    return ProductsViewHolder(view)
                }


                override fun onBindViewHolder(
                    holder: ProductsViewHolder,
                    position: Int,
                    model: Category
                ) {
                    Glide.with(requireContext()).load(model.image).into(holder.image)
                    holder.category.text = model.title.toString()
                    holder.q_num.text =
                        "${model.quotesNumber.toString()} ${getString(R.string.quote)}"

                    product_id = model.id

                    sharedPreferences = requireActivity().getSharedPreferences(
                        "quote_app",
                        AppCompatActivity.MODE_PRIVATE
                    )
                    if (sharedPreferences.getBoolean("isLogin", false)) {
                        holder.more.visibility = View.VISIBLE
                    } else holder.more.visibility = View.GONE

                    holder.cv_product.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("id", getId(position))
                        val frag = QuotesFragment()
                        frag.arguments = bundle
                        if (sharedPreferences.getBoolean("isLogin", false)) {
                            activity!!.supportFragmentManager.beginTransaction()
                                .replace(R.id.lo_fragment_container_admin, frag).addToBackStack(null).commit()
                        } else {
                            activity!!.supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainer, frag)
                                .addToBackStack(null).commit()
                        }
                    }

                    holder.more.setOnClickListener {
                        val popupMenu = PopupMenu(activity, holder.cv_product)
                        popupMenu.inflate(R.menu.popup)
                        popupMenu.setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.op_delete -> {
                                    val alertDialog = AlertDialog.Builder(activity)
                                    alertDialog.setTitle(getString(R.string.delete_category))
                                    alertDialog.setMessage(getString(R.string.are_you_sure_delete_category))
                                    alertDialog.setIcon(R.drawable.ic_delete_forever)
                                    alertDialog.setCancelable(false)

                                    alertDialog.setPositiveButton(requireActivity().getString(R.string.delete)) { d, c ->
                                        fire!!.collection("categories")
                                            .whereEqualTo(FieldPath.documentId(), getId(position))
                                            .get().addOnSuccessListener {
                                                fire!!.collection("categories")
                                                    .document(it.documents.get(0).id).delete()
                                                    .addOnSuccessListener {
                                                        fire!!.collection("quotes")
                                                            .whereEqualTo("category", model.title)
                                                            .get()
                                                            .addOnSuccessListener {
                                                                for (document in it) {
                                                                    fire!!.collection("quotes")
                                                                        .document(document.id)
                                                                        .delete()
                                                                }

                                                                snackBar(
                                                                    "${model.title} ${
                                                                        requireActivity().getString(
                                                                            R.string.deleted_successfully
                                                                        )
                                                                    }", Snackbar.LENGTH_SHORT
                                                                )
                                                            }
                                                    }
                                            }
                                    }

                                    alertDialog.setNegativeButton(requireActivity().getString(R.string.cancel)) { d, c ->
                                        d.cancel()
                                    }
                                    alertDialog.create().show()
                                    true
                                }

                                R.id.op_edit -> {
                                    val bundle = Bundle()
                                    bundle.putString("id", getId(position))
                                    val frag = EditCategoryFragment()
                                    frag.arguments = bundle
                                    activity!!.supportFragmentManager.beginTransaction()
                                        .replace(R.id.lo_fragment_container_admin, frag)
                                        .addToBackStack(null).commit()
                                    true
                                }
                                else -> false
                            }
                        }

                        try {
                            val force = popupMenu::class.java.getDeclaredField("mPopup")
                            force.isAccessible = true
                            val mPopup = force.get(popupMenu)
                            mPopup.javaClass
                                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                                .invoke(mPopup, true)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            popupMenu.show()
                        }
                    }
                }


                fun getId(position: Int): String {
                    val id = snapshots.getSnapshot(position).reference.id
                    return id
                }
            }
        progressDialog.dismiss()
    }


    override fun onStart() {
        adapterCategories!!.startListening()
        super.onStart()
    }

    override fun onStop() {
        adapterCategories!!.stopListening()
        super.onStop()
    }


    fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.lo_fragment_container_admin, fragment)
            .commit()
    }


    fun snackBar(msg: String, length: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(requireActivity().findViewById(android.R.id.content), msg, length).show()
    }

//    fun checkConnection():Boolean{
//        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
//        return activeNetwork?.isConnectedOrConnecting == true
//    }


}