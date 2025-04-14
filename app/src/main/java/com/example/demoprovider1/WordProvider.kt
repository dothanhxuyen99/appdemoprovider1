package com.example.demoprovider1

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.content.UriMatcher
import android.util.Log

class WordProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.demoprovider1"
        const val TABLE_NAME = "words"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")

        const val CODE_WORD_DIR = 1
        const val CODE_WORD_ITEM = 2

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, TABLE_NAME, CODE_WORD_DIR)
            addURI(AUTHORITY, "$TABLE_NAME/#", CODE_WORD_ITEM)
        }
    }

    private lateinit var dbHelper: WordDbHelper

    override fun onCreate(): Boolean {
        dbHelper = WordDbHelper(context!!)
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        val db = dbHelper.readableDatabase
        Log.d("WordProvider", "Query called with uri: $uri")
        return db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase
        val id = db.insert(TABLE_NAME, null, values)
        context?.contentResolver?.notifyChange(uri, null)
        return ContentUris.withAppendedId(CONTENT_URI, id)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = dbHelper.writableDatabase
        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = dbHelper.writableDatabase
        return db.update(TABLE_NAME, values, selection, selectionArgs)
    }

    override fun getType(uri: Uri): String? = when (uriMatcher.match(uri)) {
        CODE_WORD_DIR -> "vnd.android.cursor.dir/vnd.$AUTHORITY.$TABLE_NAME"
        CODE_WORD_ITEM -> "vnd.android.cursor.item/vnd.$AUTHORITY.$TABLE_NAME"
        else -> throw IllegalArgumentException("Unknown URI: $uri")
    }
}