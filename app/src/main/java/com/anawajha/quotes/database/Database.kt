package database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.view.View
import com.anawajha.quotes.MainActivity
import com.anawajha.quotes.R
import com.anawajha.quotes.model.Quote
import com.google.android.material.snackbar.Snackbar


class Database(context: Context) : SQLiteOpenHelper(context, "DataBase", null, 1) {

    private var write: SQLiteDatabase
    private var read: SQLiteDatabase

    init {
        write = this.writableDatabase
        read = this.readableDatabase
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(Quote.CreateCommand)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS ${Quote.tableName};")
        onCreate(db)
    }


    fun insertQuote(id: String, quote: String): Long {
        val cv = ContentValues()
        cv.put(Quote.colId, id)
        cv.put(Quote.colQuote, quote)
        return write.insert(Quote.tableName, null, cv)
    }

    fun getAllQuotes(): ArrayList<Quote> {
        var quotes = ArrayList<Quote>()
        val c = write.rawQuery("SELECT * FROM ${Quote.tableName}", null)
        c.moveToFirst()
        while (!c.isAfterLast) {
            val quote = Quote(
                c.getString(0), c.getString(1),System.currentTimeMillis()
            )
            quotes.add(quote)
            c.moveToNext()
        }
        c.close()
        return quotes
    }

    fun getSpesifecQuote(id:String): Quote {

        val c = write.rawQuery("SELECT * FROM ${Quote.tableName} WHERE ${Quote.colId}=$id", null)
        c.moveToFirst()
        val note = Quote(
            c.getString(0), c.getString(1),System.currentTimeMillis()
        )
        c.moveToNext()
        c.close()
        return note
    }


    fun isFavorite(id:String): Boolean {

        val c = write.rawQuery("SELECT ${Quote.colId} FROM ${Quote.tableName} WHERE ${Quote.colId}='$id'", null)
        var id = false
        if (c.count > 0) {
           id = true
        }
        return id
    }


    fun delete(id: String): Boolean {
        return write.delete("${Quote.tableName}", "${Quote.colId} = '$id'", null) > 0
    }

    fun update(oldId: String, quote: String): Boolean {

        val cv = ContentValues()
        cv.put(Quote.colQuote,quote )

        return write.update("${Quote.tableName}", cv, "${Quote.colId}= $oldId", null) > 0
    }

}