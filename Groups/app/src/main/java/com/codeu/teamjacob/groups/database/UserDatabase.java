package com.codeu.teamjacob.groups.database;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codeu.teamjacob.groups.database.base.Database;
import com.codeu.teamjacob.groups.database.base.DatabaseContract;

public class UserDatabase {

    //Build the uri for accessing the grocery list content
    public static final Uri CONTENT_URI =
            DatabaseContract.BASE_CONTENT_URI.buildUpon().
                    appendPath(DatabaseContract.PATH_USER).build();

    //The constants for if one or multiple entries are selected
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                    DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_USER;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                    DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_USER;

    //The static database for users
    public static Database<UserEntry> database = new Database<UserEntry>(CONTENT_URI, UserEntry.class);

    //The table name
    public static final String TABLE_NAME = "users";

    //The column names
    public static final String COLUMN_ID = UserEntry._ID;
    public static final String COLUMN_USER_KEY = "user_key";
    public static final String COLUMN_USERNAME = "user_username";
    public static final String COLUMN_GROUP_IDS = "user_groups";
    public static final String COLUMN_VERSION = "user_version";

    //The variables for the index of the property columns
    public static final int COL_USER_KEY;
    public static final int COL_USERNAME;
    public static final int COL_GROUP_IDS;
    public static final int COL_VERSION;

    //Add the projections to the columns to return
    static {
        COL_USER_KEY = database.addProjection(COLUMN_USER_KEY);
        COL_USERNAME = database.addProjection(COLUMN_USERNAME);
        COL_GROUP_IDS = database.addProjection(COLUMN_GROUP_IDS);
        COL_VERSION = database.addProjection(COLUMN_VERSION);
    }

    /**
     * Query the database for users
     * @param context       the context the users are being retrieved from
     * @param selection     the selection string
     * @param selectionArgs the arguments to the selection string
     * @param sortOrder     the order to return the users in
     * @return              a list entries that match the selection
     */
    public static UserEntry[] query(Context context, @Nullable String selection,
        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        return database.query(context, selection, selectionArgs, sortOrder);
    }

    /**
     * Get the user by its id in the database
     * @param context   the context the user is being retrieved from
     * @param id        the id of the user
     * @return          the user if it exists
     */
    public static UserEntry getById(Context context, long id){
        return database.getById(context, id);
    }

    /**
     * Get the user by its key from app engine
     * @param context   the context the user is being retrieved from
     * @param key       the user key
     * @return          the user it one exists
     */
    public static UserEntry getByKey(Context context, String key){
        UserEntry[] users = database.query(context,
                COLUMN_USER_KEY + " = ?",
                new String[]{key}, null);

        //Return the user if there is one
        Log.d("VV",users.length+"");
        if (users.length == 1){
            return users[0];
        }
        return null;
    }

    public static UserEntry getByUsername(Context context, String userName){

        UserEntry[] users = database.query(context,
                COLUMN_USERNAME + " = ?",
                new String[]{userName}, null);

        //Return the user if there is one
        Log.d("VV",users.length+"");
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
    public static long put(Context context, UserEntry user){
        return database.put(context, user);
    }

}
