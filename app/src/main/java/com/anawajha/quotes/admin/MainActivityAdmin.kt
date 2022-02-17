package com.anawajha.quotes.admin

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.Switch
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.anawajha.quotes.MainActivity
import com.anawajha.quotes.R
import com.anawajha.quotes.fragments.HomeFragment
import com.google.android.gms.ads.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_admin.*
import kotlinx.android.synthetic.main.toolbar_admin.*

class MainActivityAdmin : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var auth: FirebaseAuth
    lateinit var progressDialog: ProgressDialog
    lateinit var fire: FirebaseFirestore
    lateinit var storage: FirebaseStorage
    lateinit var mAdView: AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)

        auth = Firebase.auth
        fire = Firebase.firestore
        storage = Firebase.storage
        sharedPreferences = getSharedPreferences("quote_app", MODE_PRIVATE)
        progressDialog = ProgressDialog(this)


        MobileAds.initialize(this) {}

        val adView = AdView(this)

        adView.adSize = AdSize(300, 45)

        adView.adUnitId = "ca-app-pub-1059013449548438/8499825710"


        mAdView = findViewById(R.id.adView_admin)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
            }

            override fun onAdOpened() {
            }

            override fun onAdClicked() {
            }

            override fun onAdClosed() {
            }
        }


        if (loadState() == true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.Theme_Quotes);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.Theme_Quotes);
        }



        addFragment(HomeFragment())

        setSupportActionBar(toolbar_admin)
        val toggle = ActionBarDrawerToggle(this, drawer_layout_admin, toolbar_admin, 0, 0)
        drawer_layout_admin.addDrawerListener(toggle)
        toggle.syncState()
        nav_view_admin.setNavigationItemSelectedListener(this)
        toolbar_admin.setNavigationIcon(R.drawable.ic_menu)


        bottom_nav_admin.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.a_mi_home -> replaceFragment(HomeFragment())
//                R.id.a_mi_profile -> replaceFragment()
//                R.id.a_mi_analytics -> replaceFragment()
            }
            true
        }
    }


    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(
            R.id.lo_fragment_container_admin,
            fragment
        )
            .commit()
    }

    fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().add(R.id.lo_fragment_container_admin, fragment).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.a_mi_add_category -> {
                replaceFragment(AddCategoryFragment())
                drawer_layout_admin.closeDrawer(GravityCompat.START)
            }
            R.id.a_mi_add_quote -> {
                replaceFragment(addQuoteFragment())
                drawer_layout_admin.closeDrawer(GravityCompat.START)
            }
            R.id.mi_dark_theme -> switchTheme(item)
            R.id.mi_logout -> {
                logout()
                drawer_layout_admin.closeDrawer(GravityCompat.START)
            }
        }
        return true
    }


//    private fun getUserData() {
//        if (auth.currentUser != null) {
//            fire.collection("users").whereEqualTo(FieldPath.documentId(), auth.currentUser!!.uid).get()
//                .addOnSuccessListener {
//                    tv_username.text = it.documents.get(0).get("name").toString()
//                    val uri = Uri.parse(it.documents.get(0).get("image").toString())
//                    it.documents.get(0).get("image").toString()
//                    Glide.with(this).load(uri).placeholder(R.drawable.ic_user)
//                        .into(img_user)
//                }.addOnFailureListener {
//                }
//            progressDialog.dismiss()
//        }
//    }

    fun logout() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.signing_out))
        progressDialog.show()
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLogin", false)
        editor.apply()

        auth.signOut()

        val i = Intent(this, MainActivity::class.java)
        progressDialog.dismiss()
        startActivity(i)
        finish()
    }



fun switchTheme(item: MenuItem){
    item.setActionView(R.layout.theme_switch);
    var themeSwitch:Switch = item.getActionView().findViewById(R.id.action_switch) as Switch
    if (loadState() == true) {
        themeSwitch.setChecked(true);
    }
    themeSwitch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                saveState(true);
                recreate();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                saveState(false);
            }
        }
    })
}


    private fun saveState(state: Boolean) {
        val sharedPreferences = getSharedPreferences("Quotes", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("NightMode", state)
        editor.apply()
    }

    private fun loadState(): Boolean? {
        val sharedPreferences = getSharedPreferences("Quotes", MODE_PRIVATE)
        return sharedPreferences.getBoolean("NightMode", false)
    }



    // Called when leaving the activity
    public override fun onPause() {
        adView_admin.pause()
        super.onPause()
    }

    // Called when returning to the activity
    public override fun onResume() {
        super.onResume()
        adView_admin.resume()
    }

    // Called before the activity is destroyed
    public override fun onDestroy() {
        adView_admin.destroy()
        super.onDestroy()
    }


}