package com.codeu.teamjacob.groups.database.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

public abstract class Entry implements BaseColumns {

    //The id of the entry
    private long _id = -1;

    /**
     * Default constructor
     */
    public Entry() { }

    /**
     * Put the values of the entry into a content values object
     * @return  the content values object with the entry information
     */
    public abstract ContentValues getValues();

    /**
     * Override and set the values of the entry using the cursor
     * @param cursor   the cursor to get the values from
     */
    public abstract void setValues(Cursor cursor);

    /**
     * Retrieve the id of the entry
     * @return  the entry id
     */
    public final long getId() {
        return _id;
    }

    /**
     * Retrieve the id of the entry
     * @return  the entry id
     */
    protected final void setId(long id) {
        _id = id;
    }

}