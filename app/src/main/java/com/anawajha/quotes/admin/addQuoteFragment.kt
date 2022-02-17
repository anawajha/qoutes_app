package com.anawajha.quotes.admin

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import com.anawajha.quotes.R
import com.anawajha.quotes.fragments.HomeFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_add_quote.view.*
import com.google.firebase.firestore.QuerySnapshot

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnCompleteListener




class addQuoteFragment : Fragment() {
    lateinit var fire: FirebaseFirestore
    lateinit var storage: FirebaseStorage
    lateinit var reference: StorageReference
    lateinit var progressDialog: ProgressDialog
    lateinit var path: String
     var cat_id = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_add_quote, container, false)
        fire = Firebase.firestore
        storage = Firebase.storage
        reference = storage.reference
        progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false)


        val categories = getCategories()
        val arrayAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_category, categories)
        root.ed_category.setAdapter(arrayAdapter)


        root.btn_add_quote.setOnClickListener {

            if (root.ed_quote_text.text.toString().isNotEmpty()) {
                if (root.ed_category.text.toString().isNotEmpty()) {

                    addQuote(
                        root.ed_quote_text.text.toString(),
                        root.ed_category.text.toString())
                } else root.lo_category.error = getString(R.string.category_doesnt_selected)
            } else root.lo_quote_text.error = getString(R.string.quote_cant_be_empty)
        }

        hideKeyboard(root.ed_category)

        return root
    }


    private fun addQuote(
        quote: String,
        category: String) {
        var c = hashMapOf(
            "quote" to quote,
            "category" to category,
        "timestamp" to System.currentTimeMillis())

        fire.collection("quotes").add(c).addOnSuccessListener {
            snackBar(getString(R.string.quotes_added_successfully), Snackbar.LENGTH_SHORT)
            cat_id = category

            a()
            replaceFragment(HomeFragment())

        }.addOnFailureListener { exception ->
            snackBar(requireActivity().getString(R.string.something_went_wrong), Snackbar.LENGTH_SHORT)
            Log.e("ex", exception.toString())
        }
    }



    fun hideKeyboard(view: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun snackBar(msg: String, length: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(requireActivity().findViewById(android.R.id.content), msg, length).show()
    }

    fun updtaeQuotesNumber(category: String){
        var number = 0
        fire.collection("quotes").whereEqualTo("category",category).get().addOnSuccessListener {
            number = it.count()
        }

        fire.collection("categories").whereEqualTo(FieldPath.documentId(), category).get()
            .addOnSuccessListener {
                fire.collection("categories").document(it.documents.get(0).id).update("quotesNumber",number)
            }.addOnFailureListener {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    private fun getCategories(): ArrayList<String> {
        val categories = ArrayList<String>()
        fire.collection("categories").get()
            .addOnSuccessListener {
                for (document in it.documents) {
                    categories.add(document.get("title").toString())
                }
            }.addOnFailureListener {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
        progressDialog.dismiss()
        return categories
    }

    fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.lo_fragment_container_admin, fragment)
            .commit()
    }


    fun updtaeQuotesNumber1(){
        fire.collection("categories").get().addOnSuccessListener { document ->
            for (doc in document) {

                var cat = doc["title"]
                fire.collection("quotes").whereEqualTo("category",cat).get().addOnSuccessListener {quotes ->
                    Log.d("count", "$cat ${quotes.count()}")
                    fire.collection("categories").whereEqualTo("title", cat).get().addOnSuccessListener { update ->
                        fire.collection("categories").document(update.documents.get(0).id).update("quotesNumber",quotes.count())
                    }.addOnFailureListener {
                        Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }


    fun a(){
        fire.collection("categories").get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot?> { task ->
                if (task.isSuccessful) {
                    fire.collection("categories").get().addOnSuccessListener { update ->
                        for ( doc in 0 until update.size()){
                            fire.collection("categories").document(update.documents.get(doc).id).update("quotesNumber", task.result!!.size())
                        }
                }
                } else {
                    Toast.makeText(activity, "Error : ", Toast.LENGTH_SHORT).show()
                }
            })
    }


}
