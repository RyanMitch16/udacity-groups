package com.codeu.teamjacob.groups.database.base;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.codeu.teamjacob.groups.database.GroupDatabase;
import com.codeu.teamjacob.groups.database.ItemDatabase;
import com.codeu.teamjacob.groups.database.ListDatabase;
import com.codeu.teamjacob.groups.database.UserDatabase;

public class DatabaseContentProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DatabaseHelper mOpenHelper;

    static final int USER = 100;
    static final int GROUP = 200;
    static final int LIST = 300;
    static final int ITEM = 400;

    /**
     * @return
     */
    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DatabaseContract.PATH_USER, USER);
        matcher.addURI(authority, DatabaseContract.PATH_GROUP, GROUP);
        matcher.addURI(authority, DatabaseContract.PATH_LIST, LIST);
        matcher.addURI(authority, DatabaseContract.PATH_ITEM, ITEM);

        return matcher;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    /**
     *
     * @param uri
     * @return
     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case USER:
                return UserDatabase.CONTENT_ITEM_TYPE;
            case GROUP:
                return GroupDatabase.CONTENT_ITEM_TYPE;
            case LIST:
                return ListDatabase.CONTENT_ITEM_TYPE;
            case ITEM:
                return ItemDatabase.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        //Get the database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor retCursor = null;

        //Match the uri to a scheme
        switch (sUriMatcher.match(uri)) {

            //Query the user entry values
            case USER:
                retCursor = db.query(UserDatabase.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case GROUP:
                retCursor = db.query(GroupDatabase.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case LIST:
                retCursor = db.query(ListDatabase.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM:
                retCursor = db.query(ItemDatabase.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new android.database.SQLException("Failed to insert row into " + uri);
        }

        return retCursor;
    }

    /**
     *
     * @param uri
     * @param values
     * @return
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        //Get the database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri = null;

        //Match the uri to a scheme
        final int match = sUriMatcher.match(uri);
        switch (match) {

            //Insert the user entry values
            case USER: {
                long id = db.insert(UserDatabase.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ContentUris.appendId(uri.buildUpon(), id).build();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }


            case GROUP: {
                long id = db.insert(GroupDatabase.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ContentUris.appendId(uri.buildUpon(), id).build();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case LIST: {
                long id = db.insert(ListDatabase.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ContentUris.appendId(uri.buildUpon(), id).build();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case ITEM: {
                long id = db.insert(ItemDatabase.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ContentUris.appendId(uri.buildUpon(), id).build();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new android.database.SQLException("Failed to insert row into " + uri);


        }
        return returnUri;
    }

    /**
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        //Get the database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        //Match the uri to a scheme
        final int match = sUriMatcher.match(uri);
        switch (match) {

            //Update the user entry values
            case USER: {
                db.delete(UserDatabase.TABLE_NAME, selection, selectionArgs);
                break;
            }

            case GROUP: {
                db.delete(GroupDatabase.TABLE_NAME, selection, selectionArgs);
                break;
            }

            case LIST: {
                db.delete(ListDatabase.TABLE_NAME, selection, selectionArgs);
                break;
            }

            case ITEM: {
                db.delete(ItemDatabase.TABLE_NAME, selection, selectionArgs);
                break;
            }

            //Throw exception for unsupported uris
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return 0;
    }

    /**
     *
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //Get the database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        //Match the uri to a scheme
        final int match = sUriMatcher.match(uri);
        switch (match) {

            //Update the user entry values
            case USER: {
                long id = db.update(UserDatabase.TABLE_NAME, values, selection, selectionArgs);
                if (id <= 0) {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            case GROUP: {
                long id = db.update(GroupDatabase.TABLE_NAME, values, selection, selectionArgs);
                if (id <= 0) {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            case LIST: {
                long id = db.update(ListDatabase.TABLE_NAME, values, selection, selectionArgs);
                if (id <= 0) {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            case ITEM: {
                long id = db.update(ItemDatabase.TABLE_NAME, values, selection, selectionArgs);
                if (id <= 0) {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            //Throw exception for unsupported uris
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return 0;
    }

}
