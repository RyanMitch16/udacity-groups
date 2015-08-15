package com.codeu.teamjacob.groups.database.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.codeu.teamjacob.groups.database.GroupDatabase;
import com.codeu.teamjacob.groups.database.ItemDatabase;
import com.codeu.teamjacob.groups.database.ListDatabase;
import com.codeu.teamjacob.groups.database.UserDatabase;

public class DatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 43;
    static final String DATABASE_NAME = "groups.db";

    /**
     * Constructor for GroceryDatabaseHelper
     *
     * @param context of the activity
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * SQL statement to create a table to store data retrieved from the cloud
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Create the user table
        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " +
                UserDatabase.TABLE_NAME + " (" +
                UserDatabase.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                UserDatabase.COLUMN_USER_KEY + " TEXT , " +
                UserDatabase.COLUMN_USERNAME + " TEXT NOT NULL," +
                UserDatabase.COLUMN_GROUP_IDS + " TEXT, " +
                UserDatabase.COLUMN_VERSION + " INTEGER " +
                " )";

        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);

        //Create the group table
        final String SQL_CREATE_GROUP_TABLE = "CREATE TABLE " +
                GroupDatabase.TABLE_NAME + " (" +
                GroupDatabase.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                GroupDatabase.COLUMN_GROUP_KEY + " TEXT , " +
                GroupDatabase.COLUMN_GROUP_NAME + " TEXT NOT NULL," +
                GroupDatabase.COLUMN_GROUP_USERS + " TEXT, " +
                GroupDatabase.COLUMN_GROUP_PENDING_USERS + " TEXT, " +
                GroupDatabase.COLUMN_GROUP_VERSION + " INTEGER, " +
                GroupDatabase.COLUMN_GROUP_REMOVED + " INTEGER, " +
                GroupDatabase.COLUMN_GROUP_PHOTO + " TEXT, " +
                GroupDatabase.COLUMN_GROUP_PHOTO_VERSION + " INTEGER " +
                " )";
        sqLiteDatabase.execSQL(SQL_CREATE_GROUP_TABLE);

        //Create the list table
        final String SQL_CREATE_LIST_TABLE = "CREATE TABLE " +
                ListDatabase.TABLE_NAME + " (" +
                ListDatabase.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                ListDatabase.COLUMN_LIST_KEY + " TEXT , " +
                ListDatabase.COLUMN_LIST_NAME + " TEXT NOT NULL," +
                ListDatabase.COLUMN_GROUP_ID + " INTEGER, " +
                ListDatabase.COLUMN_LIST_VERSION + " INTEGER, " +
                ListDatabase.COLUMN_LIST_DELETED + " INTEGER " +
                " )";
        sqLiteDatabase.execSQL(SQL_CREATE_LIST_TABLE);

        //Create the item table
        final String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " +
                ItemDatabase.TABLE_NAME + " (" +
                ItemDatabase.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                ItemDatabase.COLUMN_ITEM_APP_ENGINE_ID + " TEXT , " +
                ItemDatabase.COLUMN_ITEM_NAME + " TEXT NOT NULL , " +
                ItemDatabase.COLUMN_ITEM_LIST_ID + " INTEGER , " +
                ItemDatabase.COLUMN_ITEM_PUT + " INTEGER , " +
                ItemDatabase.COLUMN_ITEM_CHECKED + " INTEGER " +
                " )";
        sqLiteDatabase.execSQL(SQL_CREATE_ITEM_TABLE);

    }

    /**
     * Constructor for GroceryDatabaseHelper
     *
     * @param sqLiteDatabase the database to upgrade
     * @param oldVersion     the current version of the database
     * @param newVersion     the new version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserDatabase.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GroupDatabase.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ListDatabase.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ItemDatabase.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

