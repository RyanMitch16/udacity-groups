package com.codeu.teamjacob.groups.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codeu.teamjacob.groups.database.base.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class UserEntry extends Entry {

    //Set the log tag
    public static String LOG_TAG = UserEntry.class.getSimpleName();

    //The name of the array of grocery list ids the user has access to
    public static final String GROUPS_JSON_ARRAY = "grocery_lists";

    //The user properties
    public String userKey;
    public String username;
    public JSONArray groupIds;
    public long version;

    //Default constructor
    public UserEntry(){ }

    /**
     * Create a new user entry object to be manipulated
     * @param userKey       the key of the user
     * @param username      the username of the user
     * @param groceryLists  the grocery lists in the
     */
    public UserEntry(String userKey, String username, @Nullable String groceryLists, long version){
        super();
        try {
            this.userKey = userKey;
            this.username = username;
            if (groceryLists == null) {
                //JSONObject groups = new JSONObject("{ " + GROCERY_LISTS_ARRAY + " : [] }");
                groupIds = new JSONArray();
            } else {
                groupIds = new JSONArray(groceryLists);
            }
            this.version = version;
        } catch (JSONException e){
            Log.e(LOG_TAG, e.toString());
        }
    }

    /**
     * Set the user properties from the given ursor
     * @param values    the cursor to take the values from
     */
    @Override
    public void setValues(Cursor values){
        try{
            userKey = values.getString(UserDatabase.COL_USER_KEY);
            username = values.getString(UserDatabase.COL_USERNAME);
            groupIds = new JSONArray(values.getString(UserDatabase.COL_GROUP_IDS));
            version = values.getLong(UserDatabase.COL_VERSION);
        }
        catch (JSONException e){
            Log.e(LOG_TAG,e.toString());
        }
    }

    /**
     * Get the values of the user properties
     * @return  the content values object that holds the user properties
     */
    @Override
    public ContentValues getValues() {
            //Set the values to put into the table
            ContentValues userValues = new ContentValues();
            userValues.put(UserDatabase.COLUMN_USER_KEY, userKey);
            userValues.put(UserDatabase.COLUMN_USERNAME, username);

            //Convert the array of group ids to a json string
            userValues.put(UserDatabase.COLUMN_GROUP_IDS, groupIds.toString());

            userValues.put(UserDatabase.COLUMN_VERSION, version);

            return userValues;

    }


    public void addGroup(long id){
        try {
            for (int i = 0; i < groupIds.length(); i++) {
                if (groupIds.getLong(i) == id){
                    return;}
            }
            groupIds.put(id);
        } catch (JSONException e){
            Log.d(LOG_TAG,e.toString());
        }
    }

    public void removeGroup(long id){
        try {
            JSONArray newUsersArray = new JSONArray();

            for (int i = 0; i < groupIds.length(); i++) {
                if (groupIds.getLong(i) != id){
                    newUsersArray.put(groupIds.getString(i));}
            }

            groupIds = newUsersArray;

        } catch (JSONException e){
            Log.d(LOG_TAG,e.toString());
        }
    }

    public GroupEntry[] getGroups(Context context){
        try {
            int count = groupIds.length();

            String selection = GroupDatabase.COLUMN_ID + " = ? ";
            String[] selectionArgs = new String[Math.max(count, 1)];
            selectionArgs[0] = "-1";

            for (int i=0; i<count; i++) {
                String groupId = groupIds.getString(i);

                //Append to the selection query
                if (i != 0){
                    selection += "OR " + GroupDatabase.COLUMN_ID + " = ? ";}

                //Set the selection value
                selectionArgs[i] = groupId;

            }

            ArrayList<GroupEntry> groupsToReturn = new ArrayList<GroupEntry>();
            GroupEntry[] groups = GroupDatabase.query(context,selection,selectionArgs,null);
            for (int i=0;i<groups.length;i++){
                if (groups[i].removed == false){
                    groupsToReturn.add(groups[i]);
                }
            }

            GroupEntry[] groupEntries = new GroupEntry[groupsToReturn.size()];
            for (int i=0;i<groupEntries.length;i++){
                groupEntries[i] = groupsToReturn.get(i);
            }

            return groupEntries;
        }
        catch(JSONException e){
            Log.e(LOG_TAG, e.toString());
        }
        return new GroupEntry[0];
    }
}
