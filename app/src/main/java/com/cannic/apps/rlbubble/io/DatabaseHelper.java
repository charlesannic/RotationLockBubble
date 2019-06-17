package com.cannic.apps.rlbubble.io;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cannic.apps.rlbubble.java.App;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "traintrain_database";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase database;
    private static DatabaseHelper helper;

    public ExceptionHelper exceptionHelper;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();

        initHelpers();
    }

    public static DatabaseHelper getInstance(Context context) {
        if (helper == null) {
            helper = new DatabaseHelper(context);
        }
        return helper;
    }

    private void initHelpers() {
        Log.i(TAG, "initHelpers: ");

        if (exceptionHelper == null)
            exceptionHelper = new ExceptionHelper(this, database);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Création de la Base de données
        if (database == null)
            database = db;

        //Création des tables
        db.execSQL(Database.Exception.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Database.Exception.DROP_TABLE);

        onCreate(db);
    }

    public static class ExceptionHelper {

        private DatabaseHelper databaseHelper;
        private static SQLiteDatabase db;

        ExceptionHelper(DatabaseHelper databaseHelper, SQLiteDatabase database) {
            this.databaseHelper = databaseHelper;
            db = database;
        }

        public static List<String> getAllExceptions() {
            List<String> exceptions = new ArrayList<>();

            if (db == null)
                return exceptions;

            Cursor c = db.rawQuery(Database.Exception.GET_ALL_EXCEPTIONS, null);

            if (c.moveToFirst()) {
                do {
                    exceptions.add(c.getString(0));
                } while (c.moveToNext());
            }

            c.close();

            return exceptions;
        }

        public static long insertException(App app) {
            if (db == null)
                return -1;

            ContentValues values = new ContentValues();
            //values.put(Database.Exception.ID, app.());
            values.put(Database.Exception.PACKAGE_NAME, app.getPackageName());

            return db.insert(Database.Exception.TABLE_NAME, null, values);
        }

        public static void deleteException(App app) {
            if (db == null)
                return;
            db.delete(Database.Exception.TABLE_NAME,
                    Database.Exception.PACKAGE_NAME + "='" + app.getPackageName() + "'",
                    null);
            //db.execSQL(String.format(Database.Exception.REMOVE_EXCEPTION_FROM_PACKAGE_NAME, app.getPackageName()));
        }

        public static void deleteAllExceptions() {
            if (db == null)
                return;
            db.delete(Database.Exception.TABLE_NAME,
                    null,
                    null);
        }
    }
}
