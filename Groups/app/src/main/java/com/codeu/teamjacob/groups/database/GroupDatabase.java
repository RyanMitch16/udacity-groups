package com.codeu.teamjacob.groups.database;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.codeu.teamjacob.groups.database.base.Database;
import com.codeu.teamjacob.groups.database.base.DatabaseContract;

public class GroupDatabase {

    //Build the uri for accessing the grocery list content
    public static final Uri CONTENT_URI =
            DatabaseContract.BASE_CONTENT_URI.buildUpon().
                    appendPath(DatabaseContract.PATH_GROUP).build();

    //The constants for if one or multiple entries are selected
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                    DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_GROUP;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                    DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_GROUP;

    //The static database for users
    public static Database<GroupEntry> database = new Database<GroupEntry>(CONTENT_URI, GroupEntry.class);

    //The table name
    public static final String TABLE_NAME = "groups";

    //The column names
    public static final String COLUMN_ID = GroupEntry._ID;
    public static final String COLUMN_GROUP_KEY = "group_key";
    public static final String COLUMN_GROUP_NAME = "group_name";
    public static final String COLUMN_GROUP_USERS = "user_ids";
    public static final String COLUMN_GROUP_PENDING_USERS = "pending_user_ids";
    public static final String COLUMN_GROUP_VERSION = "group_version";
    public static final String COLUMN_GROUP_REMOVED = "group_removed";
    public static final String COLUMN_GROUP_PHOTO = "group_photo";
    public static final String COLUMN_GROUP_PHOTO_VERSION = "group_photo_version";

    //The variables for the index of the property columns
    public static final int COL_GROUP_KEY;
    public static final int COL_GROUP_NAME;
    public static final int COL_GROUP_USERS;
    public static final int COL_GROUP_PENDING_USERS;
    public static final int COL_GROUP_VERSION;
    public static final int COL_GROUP_REMOVED;
    public static final int COL_GROUP_PHOTO;
    public static final int COL_GROUP_PHOTO_VERSION;


    //Add the projections to the columns to return
    static {
        COL_GROUP_KEY = database.addProjection(COLUMN_GROUP_KEY);
        COL_GROUP_NAME = database.addProjection(COLUMN_GROUP_NAME);
        COL_GROUP_USERS = database.addProjection(COLUMN_GROUP_USERS);
        COL_GROUP_PENDING_USERS = database.addProjection(COLUMN_GROUP_PENDING_USERS);
        COL_GROUP_VERSION = database.addProjection(COLUMN_GROUP_VERSION);
        COL_GROUP_REMOVED = database.addProjection(COLUMN_GROUP_REMOVED);
        COL_GROUP_PHOTO = database.addProjection(COLUMN_GROUP_PHOTO);
        COL_GROUP_PHOTO_VERSION = database.addProjection(COLUMN_GROUP_PHOTO_VERSION);
    }

    public static final int GROUP_REMOVED_FALSE = 0;
    public static final int GROUP_REMOVED_TRUE = 1;


    /**
     * Query the database for users
     * @param context       the context the users are being retrieved from
     * @param selection     the selection string
     * @param selectionArgs the arguments to the selection string
     * @param sortOrder     the order to return the users in
     * @return              a list entries that match the selection
     */
    public static GroupEntry[] query(Context context, @Nullable String selection,
                                    @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        return database.query(context, selection,selectionArgs, sortOrder);
    }

    /**
     * Get the user by its id in the database
     * @param context   the context the user is being retrieved from
     * @param id        the id of the user
     * @return
     */
    public static GroupEntry getById(Context context, long id){
        return database.getById(context, id);
    }

    /**
     *
     * @param context
     * @param key
     * @return
     */
    public static GroupEntry getByKey(Context context, String key){
        GroupEntry[] users = database.query(context,
                COLUMN_GROUP_KEY + " = ?",
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
    public static long put(Context context, GroupEntry user){
        return database.put(context, user);
    }

    public static void delete(Context context, GroupEntry group){
        database.delete(context, group);
    }

}