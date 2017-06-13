package com.example.gio.kotlindemo.datas

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.example.gio.kotlindemo.R
import com.example.gio.kotlindemo.models.bus_stops.PlaceStop
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * Copyright by Gio.
 * Created on 6/13/2017.
 */
class BusStopDatabase(private val mContext: Context,
                      private val DATABASE_NAME: String = "busstop.sqlite",
                      private val TABLE_PLACES: String = "busplaces",
                      private val DATABASE_VERSION: Int = 1)
    : SQLiteOpenHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION) {
    private var mSqLiteDatabase: SQLiteDatabase? = null
    private val DATABASE_PATH = "/data/data/com.example.gio.kotlindemo/databases/"

    init {
        // checking database and open it if exists
        if (checkDataBase()) {
            openDataBase()
            Log.d("DB", "Database exist")
        } else {
            try {
                this.readableDatabase
                copyDataBase()
                this.close()
                openDataBase()

            } catch (e: IOException) {
                Toast.makeText(mContext, R.string.error_message_error_copying_database, Toast.LENGTH_LONG).show()
            }

            Toast.makeText(mContext, R.string.text_message_initial_database, Toast.LENGTH_LONG).show()
        }
    }

    @Throws(IOException::class)
    private fun copyDataBase() {
        val myInput = mContext.assets.open(DATABASE_NAME)
        val outFileName = DATABASE_PATH + DATABASE_NAME
        val myOutput = FileOutputStream(outFileName)

        val buffer = ByteArray(1024)
        val length: Int = myInput.read(buffer)
        while (length > 0) myOutput.write(buffer, 0, length)

        myOutput.flush()
        myOutput.close()
        myInput.close()
    }

    @Throws(SQLException::class)
    private fun openDataBase() {
        val dbPath = DATABASE_PATH + DATABASE_NAME
        val dataBase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
//        val database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
    }

    private fun checkDataBase(): Boolean {
        val dbFile = mContext.getDatabasePath(DATABASE_NAME)
        return dbFile.exists()
    }

    val allPlaces: List<PlaceStop>
        get() {
            mSqLiteDatabase = writableDatabase
            val listPlaces = ArrayList<PlaceStop>()
            val cursor = mSqLiteDatabase!!.rawQuery("select * from " + TABLE_PLACES, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                listPlaces.add(PlaceStop(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3),
                        cursor.getString(4), cursor.getString(5)))
                cursor.moveToNext()
            }
            cursor.close()
            return listPlaces
        }

    fun getPlacesByIdCarriage(idCarriage: String): List<PlaceStop> {
        mSqLiteDatabase = writableDatabase
        val listPlaces = ArrayList<PlaceStop>()
        val cursor = mSqLiteDatabase!!.rawQuery("select * from $TABLE_PLACES where idCarriage like '%$idCarriage%'", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            listPlaces.add(PlaceStop(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3),
                    cursor.getString(4), cursor.getString(5)))
            cursor.moveToNext()
        }
        cursor.close()
        return listPlaces
    }

    //    public int getCountPlaces(String idCarriage) {
    //        SQLiteDatabase mSqLiteDatabase = getWritableDatabase();
    //        ArrayList<PlaceStop> listPlaces = new ArrayList<>();
    //        Cursor cursor = mSqLiteDatabase.rawQuery("select * from " + TABLE_PLACES + " where idCarriage like '%" + idCarriage + "%'", null);
    //        return cursor.getCount();
    //    }
    //
    //    public PlaceStop getPlaceById(int id) {
    //        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
    //        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_PLACES + " where " + PlaceStop._ID + " = " + id, null);
    //        if (cursor.moveToNext()) {
    //            return new PlaceStop(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3),
    //                    cursor.getString(4), cursor.getString(5));
    //        }
    //        return null;
    //    }

    // Unused implemented-functions
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {

    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {

    }
}