package com.codeu.teamjacob.groups.database;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codeu.teamjacob.groups.database.base.Database;
import com.codeu.teamjacob.groups.database.base.DatabaseContract;

public class ItemDatabase {

    //Build the uri for accessing the grocery list content
    public static final Uri CONTENT_URI =
            DatabaseContract.BASE_CONTENT_URI.buildUpon().
                    appendPath(DatabaseContract.PATH_ITEM).build();

    //The constants for if one or multiple entries are selected
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                    DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_ITEM;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                    DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.PATH_ITEM;

    //The static database for users
    public static Database<ItemEntry> database = new Database<ItemEntry>(CONTENT_URI, ItemEntry.class);

    //The table name
    public static final String TABLE_NAME = "items";

    //The column names
    public static final String COLUMN_ID = ItemEntry._ID;
    public static final String COLUMN_ITEM_APP_ENGINE_ID = "item_app_engine_id";
    public static final String COLUMN_ITEM_LIST_ID = "item_list_id";
    public static final String COLUMN_ITEM_NAME = "item_name";
    public static final String COLUMN_ITEM_PUT = "item_put";
    public static final String COLUMN_ITEM_CHECKED = "item_checked";

    //The variables for the index of the property columns
    public static final int COL_ITEM_APP_ENGINE_ID;
    public static final int COL_ITEM_LIST_ID;
    public static final int COL_ITEM_NAME;
    public static final int COL_ITEM_PUT;
    public static final int COL_ITEM_CHECKED;

    public static final int PUT_TRUE = 1;
    public static final int PUT_FALSE = 0;

    public static final int CHECKED = 1;
    public static final int UNCHECKED = 0;

    //Add the projections to the columns to return
    static {
        COL_ITEM_APP_ENGINE_ID = database.addProjection(COLUMN_ITEM_APP_ENGINE_ID);
        COL_ITEM_LIST_ID = database.addProjection(COLUMN_ITEM_LIST_ID);
        COL_ITEM_NAME = database.addProjection(COLUMN_ITEM_NAME);
        COL_ITEM_PUT = database.addProjection(COLUMN_ITEM_PUT);
        COL_ITEM_CHECKED = database.addProjection(COLUMN_ITEM_CHECKED);
    }

    /**
     * Query the database for item
     * @param context       the context the users are being retrieved from
     * @param selection     the selection string
     * @param selectionArgs the arguments to the selection string
     * @param sortOrder     the order to return the users in
     * @return              a list entries that match the selection
     */
    public static ItemEntry[] query(Context context, @Nullable String selection,
                                    @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        return database.query(context, selection, selectionArgs, sortOrder);
    }

    /**
     * Get the item by its id in the database
     * @param context   the context the user is being retrieved from
     * @param id        the id of the user
     * @return          the user if it exists
     */
    public static ItemEntry getById(Context context, long id){
        return database.getById(context, id);
    }

    public static ItemEntry getByAppEngineId(Context context, String appEngineID){
        ItemEntry[] itemEntries = database.query(context, COLUMN_ITEM_APP_ENGINE_ID + " = ?", new String[]{appEngineID}, null);

        if (itemEntries.length == 0){
            return null;
        } else if (itemEntries.length == 1) {
            return itemEntries[0];
        }
        Log.e("ERR", "Multiple items with id = "+appEngineID);
        return null;
    }

    /**
     * Inserts or updates the item entry in the database
     * @param context   the context the user is being put from
     * @return          the id of the user
     */
    public static long put(Context context, ItemEntry item){
        return database.put(context, item);
    }

    public static void delete(Context context, ItemEntry item){
        database.delete(context, item);
    }

    public static void deleteBulk(Context context, String selection, String[] selectionArgs){
        database.deleteBulk(context, selection, selectionArgs);
    }

}