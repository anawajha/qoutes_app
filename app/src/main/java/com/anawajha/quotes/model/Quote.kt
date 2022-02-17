package com.anawajha.quotes.model

import com.google.firebase.Timestamp

class Quote {
    var id:String?=null
    var quote:String?=null
    var timestamp:Long?=null
//    var category:String?=

    constructor()

    constructor(id:String,quote:String,timestamp: Long/*,category:String*/){
        this.id=id
        this.quote=quote
        this.timestamp= timestamp
//        this.category=category
    }


    companion object{
        const val tableName = "Quotes"
        const val colId = "id"
        const val colQuote="quote"
//        const val colTimeStamp="timestamp"


        const val CreateCommand="CREATE TABLE ${tableName} (" +
                "${colId} TEXT PRIMARY KEY ," +
                "${colQuote} TEXT NOT NULL);"
    }

    fun getTime():Any{
       return java.util.Calendar.getInstance().timeInMillis
    }


}