package com.codeu.teamjacob.groups.database;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.codeu.teamjacob.groups.database.base.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

public class ListEntry extends Entry {

    //Set the log tag
    public static String LOG_TAG = ListEntry.class.getSimpleName();

    //The list properties
    public String listKey;
    public String listName;
    public long groupId;
    public long version;
    public boolean removed;

    //Default constructor
    public ListEntry(){ }

    /**
     * Create a new list entry object to be manipulated
     * @param listKey  the key of the list from app engine
     * @param listName  the name of the list
     * @param groupId  the key of the group the list belongs to
     */
    public ListEntry(String listKey, String listName, long groupId, long version){
        this.listKey = listKey;
        this.listName = listName;
        this.groupId = groupId;
        this.version = version;
        removed = false;
    }

    /**
     * Set the list properties from the given cursor
     * @param values    the cursor to take the values from
     */
    @Override
    public void setValues(Cursor values){

        listKey = values.getString(ListDatabase.COL_LIST_KEY);
        listName = values.getString(ListDatabase.COL_LIST_NAME);
        groupId = values.getLong(ListDatabase.COL_GROUP_ID);
        version = values.getLong(ListDatabase.COL_LIST_VERSION);
        removed = values.getInt(ListDatabase.COL_LIST_DELETED) == ListDatabase.DELETED_TRUE;
    }

    /**
     * Get the values of the list properties
     * @return  the content values object that holds the user properties
     */
    @Override
    public ContentValues getValues() {

        //Set the values to put into the table
        ContentValues listValues = new ContentValues();
        listValues.put(ListDatabase.COLUMN_LIST_KEY, listKey);
        listValues.put(ListDatabase.COLUMN_LIST_NAME, listName);
        listValues.put(ListDatabase.COLUMN_GROUP_ID, groupId);
        listValues.put(ListDatabase.COLUMN_LIST_VERSION, version);
        listValues.put(ListDatabase.COLUMN_LIST_DELETED, removed ? ListDatabase.DELETED_TRUE : ListDatabase.DELETED_FALSE);
        return listValues;

    }

    public void setItems(Context context, JSONArray items){

        ItemDatabase.deleteBulk(context,
                ItemDatabase.COLUMN_ITEM_LIST_ID +" = ? ",new String[]{getId()+"",});

        try {

            for (int i = 0; i < items.length(); i++) {

                JSONObject item = items.getJSONObject(i);

                ItemEntry itemEntry = ItemDatabase.getByAppEngineId(context, item.getString("_id"));

                if (itemEntry != null){
                    itemEntry.isChecked = (item.getInt("checked") == ItemDatabase.CHECKED);
                    ItemDatabase.put(context, itemEntry);
                    Log.e(LOG_TAG, "Overlap");
                    continue;
                }

                itemEntry = new ItemEntry(item.getString("_id"),
                        item.getString("name"), getId());
                itemEntry.isChecked = (item.getInt("checked") == ItemDatabase.CHECKED);
                itemEntry.itemPut = true;

                ItemDatabase.put(context, itemEntry);
            }

        } catch (Exception e){
            Log.d(LOG_TAG,e.toString());
        }
    }

    public ItemEntry[] getItems(Context context) {

        return ItemDatabase.query(context, ItemDatabase.COLUMN_ITEM_LIST_ID + " = ? ",
                new String[]{getId() + ""}, null);
    }
}
