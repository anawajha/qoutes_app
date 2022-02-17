package com.anawajha.quotes.admin

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import com.anawajha.quotes.MainActivity
import com.anawajha.quotes.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var auth: FirebaseAuth
    lateinit var progressDialog: ProgressDialog
    lateinit var fire : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        fire = Firebase.firestore
        sharedPreferences = getSharedPreferences("quote_app", AppCompatActivity.MODE_PRIVATE)
        progressDialog = ProgressDialog(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        if (sharedPreferences.getBoolean("isLogin", false)){
            startActivity(Intent(this, MainActivityAdmin::class.java))
            finish()
        }

//        if (auth.currentUser == null){
//            var intent = Intent(this,sign_up::class.java)
//            startActivity(intent)
//            finish()
//        }

        ed_Email.doOnTextChanged { text, start, before, count ->
            lo_email.isErrorEnabled = false
        }
        ed_password1.doOnTextChanged { text, start, before, count ->
            lo_password.isErrorEnabled = false
        }

        btn_sign_in.setOnClickListener {
            if (ed_Email.text.toString().isNotEmpty()) {
                if (ed_Email.text.toString().contains("@") && ed_Email.text.toString().contains(".")) {
                    if (ed_password1.text.toString().isNotEmpty()) {
                    signIn(ed_Email.text.toString(), ed_password1.text.toString())
                }else {
                    lo_password.error = getString(R.string.password_cant_be_empty)
                    lo_password.helperText = ""
                }
                }else {
                    lo_email.error = getString(R.string.enter_valid_email)
                    lo_email.helperText = ""
                }
            }else {
                lo_email.error = getString(R.string.email_cant_be_empty)
                lo_email.helperText = ""
            }
        }

        layout_sign_in.setOnClickListener {
            hideKeyboard(ed_Email)
            hideKeyboard(ed_password1)
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun signIn(email:String, password:String){
        progressDialog.setTitle(getString(R.string.signing_in))
        progressDialog.show()

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){ task ->
            if (task.isSuccessful){
                val user = auth.currentUser

                val editor = sharedPreferences.edit()
                editor.putBoolean("isLogin", true)
                editor.apply()
                val i = Intent(applicationContext, MainActivityAdmin::class.java)

                i.putExtra("id",user!!.uid)
                progressDialog.dismiss()
                snackBar(getString(R.string.signed_in), Snackbar.LENGTH_SHORT)
                startActivity(i)
                finish()

            }else{
                progressDialog.dismiss()
                snackBar(getString(R.string.username_password_not_correct), Snackbar.LENGTH_SHORT)
            }
        }.addOnFailureListener {
            snackBar(getString(R.string.username_password_not_correct), Snackbar.LENGTH_SHORT)
        }
    }

    fun hideKeyboard(view: View){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
    }

    fun snackBar(msg: String, length: Int = Snackbar.LENGTH_SHORT){
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
        super.onBackPressed()
    }


}