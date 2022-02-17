package com.anawajha.quotes.admin

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.bumptech.glide.Glide
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
import kotlinx.android.synthetic.main.category_design.*
import kotlinx.android.synthetic.main.fragment_add_category.*
import kotlinx.android.synthetic.main.fragment_add_category.view.*
import kotlinx.android.synthetic.main.fragment_add_quote.*
import kotlinx.android.synthetic.main.fragment_add_quote.view.*
import kotlinx.android.synthetic.main.nav_header.*
import java.util.*

class EditCategoryFragment : Fragment() {
    lateinit var fire: FirebaseFirestore
    lateinit var storage: FirebaseStorage
    lateinit var reference: StorageReference
    lateinit var progressDialog: ProgressDialog
    lateinit var path: String
    lateinit var imagePath:String
    lateinit var image_path:String
    lateinit var quotesCategory: String
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

        root.btn_add_category.setText(getString(R.string.save_changes))
        root.btn_add_category.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_save,
            0
        )

        getcategory()

        root.img_add_category.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK)
            i.type = "image/*"
            startActivityForResult(i, 111)

        }

        root.btn_add_category.setOnClickListener {
            if (ed_category_title.text.toString().isNotEmpty()) {
                uploadImg(imageUri)
                updateCategory(image_path)

            } else lo_categoryName.error = requireActivity().getString(R.string.title_cant_be_empty)
        }
        hideKeyboard(root.ed_category_title)
        return root
    }




    private fun uploadImg(uri: Uri?) {
        if (uri != null) {
            progressDialog.setTitle(requireActivity().getString(R.string.loading))
            progressDialog.show()
            reference.child("category cover/" + UUID.randomUUID().toString()).putFile(uri)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    replaceFragment(HomeFragment())
                    it.storage.downloadUrl.addOnSuccessListener {
                        path = it.toString()
                        updatePath(path)
                        image_path = path

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
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            img_add_category.setImageURI(data?.data)
            val uri = data?.data
            imageUri = uri
            super.onActivityResult(requestCode, resultCode, data)
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


    private fun updateCategory(image_path:String) {
        fire.collection("categories").whereEqualTo(FieldPath.documentId(), arguments?.getString("id")).get()
            .addOnSuccessListener {
                fire.collection("categories").document(quotesCategory).delete()
                    .addOnSuccessListener {
                        var c = hashMapOf(
                            "title" to ed_category_title.text.toString(),
                            "image" to image_path
                        )
                        fire.collection("categories").document(ed_category_title.text.toString()).set(c)
                        cat_id = ed_category_title.text.toString()

                        updateQuote(ed_category_title.text.toString())
                        updtaeQuotesNumber1()

                        snackBar(getString(R.string.updated_successfully), Snackbar.LENGTH_SHORT)
                        val bundle = Bundle()
                        bundle.putString("id", cat_id)
                        val frag = HomeFragment()
                        frag.arguments = bundle
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.lo_fragment_container_admin, frag).commit()

                    }.addOnFailureListener {
                        snackBar(getString(R.string.update_failed), Snackbar.LENGTH_SHORT)
                    }
            }.addOnFailureListener {
                Toast.makeText(context, getString(R.string.category_may_be_not_exist), Toast.LENGTH_SHORT).show()
            }
    }



        private fun getcategory() {
            progressDialog.setTitle("Loading ...")
            progressDialog.show()
            fire.collection("categories").whereEqualTo(FieldPath.documentId(),arguments?.getString("id")).get()
                .addOnSuccessListener {
                    quotesCategory = it.documents.get(0).get("title").toString()
                    ed_category_title.setText(quotesCategory)
                    imagePath = it.documents.get(0).get("image").toString()
                    val uri = Uri.parse(imagePath)
                    it.documents.get(0).get("image").toString()
                    Glide.with(this).load(uri).placeholder(R.drawable.ic_user)
                        .into(img_add_category)
                    image_path = imagePath

                    progressDialog.dismiss()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(),"Some thing went error",Toast.LENGTH_SHORT).show()
                    replaceFragment(HomeFragment())
                    progressDialog.dismiss()
                }
    }


    private fun updateQuote(newCategory:String) {
        fire.collection("quotes").whereEqualTo("category", quotesCategory)
            .get()
            .addOnSuccessListener {

                fire!!.collection("quotes").whereEqualTo("category", quotesCategory).get()
                    .addOnSuccessListener {
                        for (document in it) {
                            fire.collection("quotes").document(document.id)
                                .update("category", newCategory)
                        }


                    }.addOnFailureListener {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
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


}

