package com.anawajha.quotes.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.anawajha.quotes.Adapter.QuotesAdapter
import com.anawajha.quotes.R
import com.anawajha.quotes.model.Quote
import database.Database
import kotlinx.android.synthetic.main.fragment_favorites.view.*


class favorites_fragment : Fragment() {
lateinit var adapter:QuotesAdapter
lateinit var qoutes:ArrayList<Quote>
    lateinit var database: Database


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_favorites, container, false)
        database = Database(requireContext())
           qoutes = database.getAllQuotes()

        adapter = QuotesAdapter(requireActivity(),qoutes,root.rv_favorites)

        root.rv_favorites.layoutManager = LinearLayoutManager(requireContext())
        root.rv_favorites.adapter = adapter

        return root

    }


}