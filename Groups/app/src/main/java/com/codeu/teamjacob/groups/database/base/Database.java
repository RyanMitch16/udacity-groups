package com.codeu.teamjacob.groups.database.base;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Database<T extends Entry> {

    //The log tag of the class
    public static final String LOG_TAG = Database.class.getSimpleName();

    //The content uri to send back on requests
    public Uri contentUri;

    //The type of entry class
    public Class<T> classType;

    //The columns to retrieve from the entry
    public List<String> projection = new ArrayList<String>();


    public Database(){ }

    /**
     * The database constructor for a specific entry type
     * @param contentUri    the content uri of the entry type
     * @param classType     the entry class
     */
    public Database(Uri contentUri, Class<T> classType){
        this.contentUri = contentUri;
        this.classType = classType;

        //Add the hidden id column to the projections to return
        projection.add(BaseColumns._ID);
    }

    /**
     * Add a new column to the columns to be returned when the entry is queried
     * @param columnName    the name of the column in the table
     * @return              the column value
     */
    public int addProjection(String columnName){
        projection.add(columnName);
        return projection.size()-1;
    }

    /**
     * Query the database for entries
     * @param context       the context the entries are being retrieved from
     * @param selection     the selection string
     * @param selectionArgs the arguments to the selection string
     * @param sortOrder     the order to return the entries in
     * @return              a list entries that match the selection
     */
    public T[] query(Context context, @Nullable String selection,
                     @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //Retrieve the users following the selection
        Cursor cursor = context.getContentResolver().query(
                contentUri, projection.toArray(new String[projection.size()]), selection, selectionArgs, sortOrder);

        try {
            //Create an array to hold the selected users
            int count = cursor.getCount();
            cursor.moveToFirst();
            T[] entries = (T[]) Array.newInstance(classType, count);

            //Instantiate an return the entries
            for (int i = 0; i < count; i++) {
                //Instantiate the entries
                T entry = classType.newInstance();
                entry.setValues(cursor);

                //Set the id of the entry
                entry.setId(cursor.getLong(0));

                entries[i] = entry;
                cursor.moveToNext();
            }

            //Return the list of entries
            cursor.close();
            return entries;
        }catch (Exception e){
            Log.e(LOG_TAG, "Query error: "+e.toString());
        }

        return (T[]) Array.newInstance(classType, 0);
    }

    /**
     * Get the entry by its id
     * @param context   the context the entry is being retrieved from
     * @param id        the id of the entry
     * @return
     */
    public T getById(Context context, long id){
        //Retrieve the users following the selection
        Cursor cursor = context.getContentResolver().query(
                contentUri, projection.toArray(new String[projection.size()]),
                T._ID + " =? ", new String[]{"" + id}, null);

        //Check if there are no entries with that id
        int count = cursor.getCount();
        if (count == 0) {
            cursor.close();
            return null;
        }

        //Check if multiple entries have the same id
        if (count > 1){
            Log.e(LOG_TAG, "Get by id error: Multiple entries of type "+classType.getName()+
                "with the _id of "+id);
            return null;
        }

        //Instantiate an return the entry
        try{
            //Instantiate the entry
            cursor.moveToFirst();
            T entry = classType.newInstance();
            entry.setValues(cursor);

            //Set the id of the new entry instance
            entry.setId(cursor.getLong(0));

            //Return the found user
            cursor.close();
            return entry;
        }catch (Exception e){
            Log.e(LOG_TAG, "Get by id error: "+e.toString());
        }

        return null;
    }


    /**
     * Inserts or updates the entry in the databse
     * @param context   the context the entry is being put from
     * @return          the id of the entry
     */
    public long put(Context context, T entry){

        //Get the values of the entry
        ContentValues values = entry.getValues();
        long _id = entry.getId();

        if (getById(context,_id) != null){
            //Update if the user exists
            context.getContentResolver().update(
                    contentUri,values, T._ID +" =? ", new String[]{""+ _id});
            return _id;
        }
        else{
            //Insert the user entry if it does not exist
            long id = getAppendedId(context.getContentResolver().insert(contentUri,values));
            entry.setId(id);
            return id;
        }
    }

    public void delete(Context context, T entry){

        context.getContentResolver().delete(contentUri,
                T._ID +" =? ", new String[]{""+ entry.getId()});

        entry.setId(-2);
    }

    public void deleteBulk(Context context,@Nullable String selection,
                           @Nullable String[] selectionArgs){

        context.getContentResolver().delete(contentUri, selection, selectionArgs);
    }


    /**
     * Build the query path to get the list with the specified id
     * @param id        the user id
     * @return          the built content uri
     */
    public Uri buildEntryUri(long id) {
        return ContentUris.withAppendedId(contentUri, id);
    }

    /**
     * Retrieve the id from the uri
     * @param uri   the uri to retrieve the id from
     * @return      the key included in the uri if present
     */
    public long getAppendedId(Uri uri) {
        return ContentUris.parseId(uri);
    }
}
