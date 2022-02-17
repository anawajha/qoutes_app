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
import androidx.core.widget.doOnTextChanged
import com.anawajha.quotes.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_add_quote.*
import kotlinx.android.synthetic.main.fragment_add_quote.view.*

class EditQuoteFragment : Fragment() {
    lateinit var fire: FirebaseFirestore
    lateinit var storage: FirebaseStorage
    lateinit var reference: StorageReference
    lateinit var progressDialog: ProgressDialog
    lateinit var path: String
    lateinit var dialog: Dialog
    lateinit var procuct_id: String

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

        root.btn_add_quote.setText(requireActivity().getString(R.string.save_changes))
        root.btn_add_quote.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_save,
            0
        )
        getQuote()

        root.ed_quote_text.doOnTextChanged { text, start, before, count ->
            lo_quote_text.isErrorEnabled = false
        }

        root.ed_category.doOnTextChanged { text, start, before, count ->
            lo_category.isErrorEnabled = false
        }

        root.btn_add_quote.setOnClickListener {

            if (root.ed_quote_text.text.toString().isNotEmpty()) {
                if (root.ed_category.text.toString().isNotEmpty()) {

                    updateQuote()
                } else root.lo_category.error = requireActivity().getString(R.string.category_doesnt_selected)
            } else root.lo_quote_text.error = requireActivity().getString(R.string.quote_cant_be_empty)
        }

        hideKeyboard(root.ed_category)

        return root
    }


    private fun updateQuote() {
        fire.collection("quotes").whereEqualTo(FieldPath.documentId(), arguments?.getString("id"))
            .get()
            .addOnSuccessListener {
                val newData = mapOf(
                    "quote" to ed_quote_text.text.toString(),
                    "category" to ed_category.text.toString(),
                    "timestamp" to System.currentTimeMillis()
                )
                fire.collection("quotes").document(it.documents.get(0).id).update(newData)
                    .addOnSuccessListener {
                        snackBar(requireActivity().getString(R.string.updated_successfully), Snackbar.LENGTH_SHORT)


                        updtaeQuotesNumber1()


                        val bundle = Bundle()
                        bundle.putString("id", ed_category.text.toString())
                        val frag = QuotesFragment()
                        frag.arguments = bundle
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.lo_fragment_container_admin, frag).commit()
                    }.addOnFailureListener {
                    snackBar(requireActivity().getString(R.string.update_failed), Snackbar.LENGTH_SHORT)
                }
            }.addOnFailureListener {
                Toast.makeText(context, getString(R.string.quote_may_be_not_exist), Toast.LENGTH_SHORT).show()
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



    private fun getQuote() {
        fire.collection("quotes").whereEqualTo(FieldPath.documentId(), arguments?.getString("id"))
            .get()
            .addOnSuccessListener {
                ed_quote_text.setText(it.documents.get(0).get("quote").toString())
                ed_category.setText(it.documents.get(0).get("category").toString())
            }.addOnFailureListener {
            }
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


}
