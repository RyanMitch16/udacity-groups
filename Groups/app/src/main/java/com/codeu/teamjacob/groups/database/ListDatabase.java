package com.codeu.teamjacob.groups.database;


import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.codeu.teamjacob.groups.database.base.Database;
import com.codeu.teamjacob.groups.database.base.DatabaseContract;

public class ListDatabase {

    //Build the uri for accessing the grocery list content
    public static final Uri CONTENT_URI =
            DatabaseContract.BASE_CONTENT_URI.buildUpon().
                    appendPath(DatabaseContract.PATH_LIST).build();

    //The constants for if one or multiple entries are selected
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                    DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_LIST;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                    DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_LIST;

    //The static database for users
    public static Database<ListEntry> database = new Database<ListEntry>(CONTENT_URI, ListEntry.class);

    //The table name
    public static final String TABLE_NAME = "lists";

    //The column names
    public static final String COLUMN_ID = ListEntry._ID;
    public static final String COLUMN_LIST_KEY = "list_key";
    public static final String COLUMN_LIST_NAME = "list_name";
    public static final String COLUMN_GROUP_ID = "group_id";
    public static final String COLUMN_LIST_VERSION = "list_version";
    public static final String COLUMN_LIST_DELETED = "list_deleted";

    //The variables for the index of the property columns
    public static final int COL_LIST_KEY;
    public static final int COL_LIST_NAME;
    public static final int COL_GROUP_ID;
    public static final int COL_LIST_VERSION;
    public static final int COL_LIST_DELETED;

    //Add the projections to the columns to return
    static {
        COL_LIST_KEY = database.addProjection(COLUMN_LIST_KEY);
        COL_LIST_NAME = database.addProjection(COLUMN_LIST_NAME);
        COL_GROUP_ID = database.addProjection(COLUMN_GROUP_ID);
        COL_LIST_VERSION = database.addProjection(COLUMN_LIST_VERSION);
        COL_LIST_DELETED = database.addProjection(COLUMN_LIST_DELETED);
    }

    public static final int DELETED_FALSE = 0;
    public static final int DELETED_TRUE = 1;

    /**
     * Query the database for users
     * @param context       the context the users are being retrieved from
     * @param selection     the selection string
     * @param selectionArgs the arguments to the selection string
     * @param sortOrder     the order to return the users in
     * @return              a list entries that match the selection
     */
    public static ListEntry[] query(Context context, @Nullable String selection,
                                    @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        return database.query(context, selection, selectionArgs, sortOrder);
    }

    /**
     * Get the user by its id in the database
     * @param context   the context the user is being retrieved from
     * @param id        the id of the user
     * @return          the user if it exists
     */
    public static ListEntry getById(Context context, long id){
        return database.getById(context, id);
    }

    /**
     * Get the user by its key from app engine
     * @param context   the context the user is being retrieved from
     * @param key       the user key
     * @return          the user it one exists
     */
    public static ListEntry getByKey(Context context, String key){
        ListEntry[] users = database.query(context,
                COLUMN_LIST_KEY + " = ?",
                new String[]{key}, null);

        //Return the user if there is one
        if (users.length == 1){
            return users[0];
        }
        return null;
    }

    /**
     * Inserts or updates the user entry in the database
     * @param context   the context the user is being put from
     * @return          the id of the user
     */
    public static long put(Context context, ListEntry user){
        return database.put(context, user);
    }

    public static void delete(Context context, ListEntry list){
        database.delete(context,list);
    }

}