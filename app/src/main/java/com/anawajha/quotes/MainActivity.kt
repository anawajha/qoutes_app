package com.anawajha.quotes


import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.anawajha.quotes.admin.LoginActivity
import com.anawajha.quotes.admin.MainActivityAdmin
import com.anawajha.quotes.fragments.HomeFragment
import com.anawajha.quotes.fragments.favorites_fragment
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import kotlinx.android.synthetic.main.activity_main.*

//  ca-app-pub-1059013449548438/1258650000

class MainActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var mAdView: AdView
    private final var TAG = "MainActivity"
        var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        MobileAds.initialize(this) {}

        val adView = AdView(this)

        adView.adSize = AdSize(300, 45)

        adView.adUnitId = "ca-app-pub-1059013449548438/1952289735"


        mAdView = findViewById(R.id.adView)
        var adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }


        setSupportActionBar(toolbar)
        sharedPreferences = getSharedPreferences("quote_app", AppCompatActivity.MODE_PRIVATE)

        if (sharedPreferences.getBoolean("isLogin", false)) {
            startActivity(Intent(this, MainActivityAdmin::class.java))
            finish()
        }


        val home_fragment = HomeFragment()
        val favoraite_fragment = favorites_fragment()

        addFragment(home_fragment)

        bottom_nav_container.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(home_fragment)
                R.id.favorites -> replaceFragment(favoraite_fragment)
            }
            true
        }


//        InterstitialAd.load(this,"ca-app-pub-1059013449548438/6622904746", adRequest, object : InterstitialAdLoadCallback() {
//            override fun onAdFailedToLoad(adError: LoadAdError) {
//                Log.d(TAG, adError?.message)
//                mInterstitialAd = null
//            }
//
//            override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                Log.d(TAG, "Ad was loaded.")
//                mInterstitialAd = interstitialAd
//
//                if (mInterstitialAd != null) {
//                    mInterstitialAd?.show(this@MainActivity)
//                } else {
//                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
//                }
//            }
//        })
//
//        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
//            override fun onAdDismissedFullScreenContent() {
//                Log.d(TAG, "Ad was dismissed.")
//            }
//
//            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
//                Log.d(TAG, "Ad failed to show.")
//            }
//
//            override fun onAdShowedFullScreenContent() {
//                Log.d(TAG, "Ad showed fullscreen content.")
//                mInterstitialAd = null
//            }
//        }





    }// onCreate


    private fun addFragment(fragment: androidx.fragment.app.Fragment) {
        if (!fragment.isAdded()) {
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment).commit()
        }
    }

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        if (!fragment.isAdded()) {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment)
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_admin_panel -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


//    // Called when leaving the activity
//    public override fun onPause() {
//        adView.pause()
//        super.onPause()
//    }
//
//    // Called when returning to the activity
//    public override fun onResume() {
//        super.onResume()
//        adView.resume()
//    }
//
//    // Called before the activity is destroyed
//    public override fun onDestroy() {
//        adView.destroy()
//        super.onDestroy()
//    }


}