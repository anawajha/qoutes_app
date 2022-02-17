package com.anawajha.quotes.admin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.Secure.getString
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import kotlinx.android.synthetic.main.fragment_add_category.*
import kotlinx.android.synthetic.main.fragment_add_category.view.*
import java.util.*

class AddCategoryFragment : Fragment() {
    lateinit var fire: FirebaseFirestore
    lateinit var storage: FirebaseStorage
    lateinit var reference: StorageReference
    lateinit var progressDialog: ProgressDialog
    lateinit var path: String
    lateinit var lastPath: String
    var imageUri: Uri? = null
    lateinit var cat_id: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_add_category, container, false)


        fire = Firebase.firestore
        storage = Firebase.storage
        reference = storage.reference
        progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false)

        root.img_add_category.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK)
            i.type = "image/*"
            startActivityForResult(i, 111)
        }

        root.btn_add_category.setOnClickListener {
            if (ed_category_title.text.toString().isNotEmpty()) {
                uploadImg(imageUri)
                addCategory(ed_category_title.text.toString())
            } else lo_categoryName.error = getString(R.string.title_cant_be_empty)
        }
        hideKeyboard(root.ed_category_title)
        return root
    }


    private fun addCategory(title: String) {
        var c = hashMapOf(
            "title" to title,
            "quotesNumber" to 0
        )

        fire.collection("categories").document(title).set(c).addOnSuccessListener {
            cat_id = title

        }.addOnFailureListener { exception ->
            progressDialog.dismiss()
            snackBar(getString(R.string.something_went_wrong), Snackbar.LENGTH_SHORT)
            Log.e("ex", exception.toString())
        }
    }


    private fun uploadImg(uri: Uri?) {
        if (uri != null) {
            progressDialog.setTitle(getString(R.string.loading))
            progressDialog.show()
            reference.child("category cover/" + UUID.randomUUID().toString()).putFile(uri)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    snackBar(getString(R.string.category_added_successfully), Snackbar.LENGTH_SHORT)
                    replaceFragment(HomeFragment())
                    it.storage.downloadUrl.addOnSuccessListener {
                        path = it.toString()
                        updatePath(path)
                    }.addOnFailureListener {

                    }
                }.addOnFailureListener {
                }
        }
    }


    private fun updatePath(path: String) {
        fire.collection("categories").whereEqualTo(FieldPath.documentId(), cat_id).get()
            .addOnSuccessListener {
                fire.collection("categories").document(it.documents.get(0).id).update("image", path)
            }.addOnFailureListener {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            img_add_category.setImageURI(data?.data)
            val uri = data?.data
            imageUri = uri
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


    fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.lo_fragment_container_admin, fragment)
            .commit()
    }





}

