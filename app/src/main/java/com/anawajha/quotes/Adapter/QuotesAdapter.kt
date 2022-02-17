package com.anawajha.quotes.Adapter

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.anawajha.quotes.Adapter.QuotesAdapter.*
import com.anawajha.quotes.R
import com.anawajha.quotes.model.Quote
import database.Database
import kotlinx.android.synthetic.main.quote_design.view.*

class QuotesAdapter(var activity: Activity, var quotesList: ArrayList<Quote>,var recyclerView: RecyclerView) :
    RecyclerView.Adapter<MyViewHolder>() {
    lateinit var database: Database

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_quote = itemView.tv_quote
        val btnFavorite = itemView.btn_addToFavorites
        val btnCopy = itemView.btn_copy
        val btnShare = itemView.btn_share
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val root =
            LayoutInflater.from(activity).inflate(R.layout.quote_design, parent, false)
        root.btn_addToFavorites.setImageResource(R.drawable.ic_favorite)
        return MyViewHolder(root)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tv_quote.text =
            quotesList[position].quote

        holder.btnFavorite.setOnClickListener {
            holder.btnFavorite.setImageResource(R.drawable.ic_favorite_outline)
            database = Database(activity)
            database.delete(quotesList[position].id.toString())
                Toast.makeText(activity, activity.getString(R.string.delete_from_favorite), Toast.LENGTH_SHORT).show()
            notifyDataSetChanged()
            quotesList.removeAt(position)
        }

        holder.btnCopy.setOnClickListener {
            var clipBoard: ClipboardManager =
                activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clip = ClipData.newPlainText("Copied Text", quotesList[position].quote)
            clipBoard.setPrimaryClip(clip)
            Toast.makeText(activity, activity.getString(R.string.copied_ext), Toast.LENGTH_SHORT).show()
        }

        holder.btnShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, quotesList[position].quote)
            intent.type = "text/plain"
            activity.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return quotesList.size
    }



}