package com.anawajha.quotes.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anawajha.quotes.R
import com.anawajha.quotes.model.Category
import kotlinx.android.synthetic.main.category_design.view.*

class TypeAdapter(var activity: Activity, var categories: ArrayList<Category>, var click:onClick) :
    RecyclerView.Adapter<TypeAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageType = itemView.img_category
        val type = itemView.tv_category
        val quotesNumber = itemView.tv_quotesNumber
        val cardView = itemView.cv_category
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val root = LayoutInflater.from(activity).inflate(R.layout.category_design, parent, false)
        return MyViewHolder(root)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.imageType.setImageResource(categories[position].image)
        holder.type.text = categories[position].title
        holder.quotesNumber.text = "${categories[position].quotesNumber} ${activity.getString(R.string.quote)}"

        holder.cardView.setOnClickListener {
            click.onClickItem(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }


    interface onClick{
        fun onClickItem(position: Int)
    }

}