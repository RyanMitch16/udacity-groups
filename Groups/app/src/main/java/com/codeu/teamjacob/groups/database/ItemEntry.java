package com.codeu.teamjacob.groups.database;


import android.content.ContentValues;
import android.database.Cursor;

import com.codeu.teamjacob.groups.database.base.Entry;

public class ItemEntry extends Entry {

    //Set the log tag
    public static String LOG_TAG = ItemEntry.class.getSimpleName();

    //The group properties
    public String itemAppEngineId;
    public String itemName;
    public long itemListId;
    public boolean itemPut;
    public boolean isChecked;

    //Default constructor
    public ItemEntry(){ }

    /**
     * Create a new group entry object to be manipulated
     * @param groupKey  the key of the group from app engine
     * @param groupName the name of the group
     * @param users     the grocery lists in the
     */
    public ItemEntry(String itemAppEngineId, String itemName, long itemListId){
        super();
        this.itemAppEngineId = itemAppEngineId;
        this.itemName = itemName;
        this.itemListId = itemListId;
        this.itemPut = false;
        this.isChecked = false;
    }

    /**
     * Set the item properties from the given cursor
     * @param values    the cursor to take the values from
     */
    @Override
    public void setValues(Cursor values){
        itemAppEngineId = values.getString(ItemDatabase.COL_ITEM_APP_ENGINE_ID);
        itemName = values.getString(ItemDatabase.COL_ITEM_NAME);
        itemListId = values.getLong(ItemDatabase.COL_ITEM_LIST_ID);
        itemPut = (values.getInt(ItemDatabase.COL_ITEM_PUT)  == ItemDatabase.PUT_TRUE);
        isChecked = (values.getInt(ItemDatabase.COL_ITEM_CHECKED) == ItemDatabase.CHECKED);
    }

    /**
     * Get the values of the item properties
     * @return  the content values object that holds the user properties
     */
    @Override
    public ContentValues getValues() {

        //Set the values to put into the table
        ContentValues groupValues = new ContentValues();
        groupValues.put(ItemDatabase.COLUMN_ITEM_APP_ENGINE_ID, itemAppEngineId);
        groupValues.put(ItemDatabase.COLUMN_ITEM_NAME, itemName);
        groupValues.put(ItemDatabase.COLUMN_ITEM_LIST_ID, itemListId);
        groupValues.put(ItemDatabase.COLUMN_ITEM_PUT, itemPut ?
                ItemDatabase.PUT_TRUE : ItemDatabase.PUT_FALSE);
        groupValues.put(ItemDatabase.COLUMN_ITEM_CHECKED, isChecked ?
                ItemDatabase.CHECKED : ItemDatabase.UNCHECKED);
        return groupValues;

    }
}