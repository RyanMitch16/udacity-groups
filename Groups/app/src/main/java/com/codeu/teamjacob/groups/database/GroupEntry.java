package com.codeu.teamjacob.groups.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codeu.teamjacob.groups.database.base.Entry;
import com.codeu.teamjacob.groups.ui.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GroupEntry extends Entry {

    //Set the log tag
    public static String LOG_TAG = GroupEntry.class.getSimpleName();

    //The group properties
    public String groupKey;
    public String groupName;

    //The json object has user keys paired up the permission levels
    public JSONArray users;
    public JSONArray pendingUsers;

    public long version;

    public Bitmap photo;
    public long photoVersion;

    public boolean removed;

    //Default constructor
    public GroupEntry(){ }

    /**
     * Create a new group entry object to be manipulated
     * @param groupKey  the key of the group from app engine
     * @param groupName the name of the group
     * @param users     the grocery lists in the
     */
    public GroupEntry(String groupKey, String groupName, @Nullable String users, @Nullable String pendingUsers, long version){
        super();
        try {
            this.groupKey = groupKey;
            this.groupName = groupName;
            if (users == null) {
                this.users = new JSONArray();
            } else {
                this.users = new JSONArray(users);
            }

            if (pendingUsers == null) {
                this.pendingUsers = new JSONArray();
            } else {
                this.pendingUsers = new JSONArray(pendingUsers);
            }

            this.version = version;
            this.removed = false;

            photo = null;
            photoVersion = 0;


        } catch (JSONException e){
            Log.e(LOG_TAG, e.toString());
        }
    }

    /**
     * Set the group properties from the given cursor
     * @param values    the cursor to take the values from
     */
    @Override
    public void setValues(Cursor values){
        try{
            groupKey = values.getString(GroupDatabase.COL_GROUP_KEY);
            groupName = values.getString(GroupDatabase.COL_GROUP_NAME);
            users = new JSONArray(values.getString(GroupDatabase.COL_GROUP_USERS));
            pendingUsers = new JSONArray(values.getString(GroupDatabase.COL_GROUP_PENDING_USERS));
            version = values.getLong(GroupDatabase.COL_GROUP_VERSION);
            removed = values.getInt(GroupDatabase.COL_GROUP_REMOVED) == GroupDatabase.GROUP_REMOVED_TRUE;
            if (values.getString(GroupDatabase.COL_GROUP_PHOTO).equals("")){
                photo = null;
            } else {
                photo = Utility.stringToBitMap(values.getString(GroupDatabase.COL_GROUP_PHOTO));
            }
            photoVersion = values.getLong(GroupDatabase.COL_GROUP_PHOTO_VERSION);
        }
        catch (JSONException e){
            Log.e(LOG_TAG,e.toString());
        }
    }

    /**
     * Get the values of the group properties
     * @return  the content values object that holds the user properties
     */
    @Override
    public ContentValues getValues() {

        //Set the values to put into the table
        ContentValues groupValues = new ContentValues();
        groupValues.put(GroupDatabase.COLUMN_GROUP_KEY, groupKey);
        groupValues.put(GroupDatabase.COLUMN_GROUP_NAME, groupName);
        groupValues.put(GroupDatabase.COLUMN_GROUP_USERS, users.toString());
        groupValues.put(GroupDatabase.COLUMN_GROUP_PENDING_USERS, pendingUsers.toString());
        groupValues.put(GroupDatabase.COLUMN_GROUP_VERSION, version);
        groupValues.put(GroupDatabase.COLUMN_GROUP_REMOVED, removed ?
                GroupDatabase.GROUP_REMOVED_TRUE : GroupDatabase.GROUP_REMOVED_FALSE);
        groupValues.put(GroupDatabase.COLUMN_GROUP_PHOTO, (photo == null) ? "" : Utility.bitMapToString(photo));
        groupValues.put(GroupDatabase.COLUMN_GROUP_PHOTO_VERSION, photoVersion);
        return groupValues;

    }

    public ListEntry[] getLists(Context context, boolean onlyWithKeys) {

        if (onlyWithKeys){
            return ListDatabase.query(context, ListDatabase.COLUMN_GROUP_ID+" = ? AND "
                            +ListDatabase.COLUMN_LIST_KEY+" != ? ",
                    new String[]{getId()+"","null"}, null);
        }

        ArrayList<ListEntry> listsToReturn = new ArrayList<ListEntry>();
        ListEntry[] lists = ListDatabase.query(context, ListDatabase.COLUMN_GROUP_ID+" = ? ",
                new String[]{getId()+""}, null);

        for (int i=0;i<lists.length;i++){
            if (lists[i].removed == false){
                listsToReturn.add(lists[i]);
            }
        }

        ListEntry[] listEntries = new ListEntry[listsToReturn.size()];
        for (int i=0;i<listEntries.length;i++){
            listEntries[i] = listsToReturn.get(i);
        }

        return listEntries;
    }

    public void addUser(String userName) {

        try {
            for (int i = 0; i < users.length(); i++) {
                if (users.getString(i).equals(userName)){
                    return;}
            }
            users.put(userName);
        } catch (JSONException e){
            Log.d(LOG_TAG,e.toString());
        }
    }

    public void addPendingUser(String userName) {
        try {
            for (int i = 0; i < pendingUsers.length(); i++) {
                if (pendingUsers.getString(i).equals(userName)){
                    return;}
            }
            pendingUsers.put(userName);
        } catch (JSONException e){
            Log.d(LOG_TAG,e.toString());
        }
    }

    public void removeUser(String userName) {

        try {
            JSONArray newUsersArray = new JSONArray();

            for (int i = 0; i < users.length(); i++) {
                if (!users.getString(i).equals(userName)){
                    newUsersArray.put(users.getString(i));}
            }

            users = newUsersArray;

        } catch (JSONException e){
            Log.d(LOG_TAG,e.toString());
        }
    }

    public void removePendingUser(String userName) {
        try {
            JSONArray newUsersArray = new JSONArray();

            for (int i = 0; i < pendingUsers.length(); i++) {
                if (!pendingUsers.getString(i).equals(userName)){
                    newUsersArray.put(pendingUsers.getString(i));}
            }

            pendingUsers = newUsersArray;

        } catch (JSONException e){
            Log.d(LOG_TAG,e.toString());
        }
    }

    public UserEntry[] getUsers(Context context){

        //Set the selection arguments to selection all available lists
        String selection = UserDatabase.COL_USERNAME + " = ? ";
        String[] selectionArgs = new String[Math.max(users.length(), 1)];
        selectionArgs[0] = "-1";

        try {
            for (int i = 0; i < users.length(); i++) {

                if (i != 0) {
                    selection += "OR " + UserDatabase.COL_USERNAME + " = ? ";
                }
                selectionArgs[i] = users.getString(i);

            }

            return UserDatabase.query(context, selection, selectionArgs, null);
        } catch (JSONException e){
            Log.d(LOG_TAG,e.toString());
        }
        return new UserEntry[0];
    }

    public UserEntry[] getPendingUsers(Context context) {

        //Set the selection arguments to selection all available lists
        String selection = UserDatabase.COL_USERNAME + " = ? ";
        String[] selectionArgs = new String[Math.max(pendingUsers.length(), 1)];
        selectionArgs[0] = "-1";

        try {
            for (int i = 0; i < pendingUsers.length(); i++) {

                if (i != 0) {
                    selection += "OR " + UserDatabase.COL_USERNAME + " = ? ";
                }
                selectionArgs[i] = pendingUsers.getString(i);

            }

            return UserDatabase.query(context, selection, selectionArgs, null);
        } catch (JSONException e){
            Log.d(LOG_TAG,e.toString());
        }
        return new UserEntry[0];
    }

    public boolean isInPendingUsers(Context context, String userName) {
        try {
            for (int i = 0; i < pendingUsers.length(); i++) {
                if (pendingUsers.getString(i).equals(userName)){
                    return true;}
            }
        } catch (JSONException e){
            Log.d(LOG_TAG,e.toString());
        }
        return false;
    }
}