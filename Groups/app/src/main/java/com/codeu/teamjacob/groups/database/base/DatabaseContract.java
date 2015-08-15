package com.codeu.teamjacob.groups.database.base;

import android.net.Uri;

public class DatabaseContract {

    //The content authority of the content provider
    public static final String CONTENT_AUTHORITY = "com.codeu.teamjacob.groups";

    //The base uri to access the content
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible content uri paths
    public static final String PATH_USER = "user";
    public static final String PATH_GROUP = "group";
    public static final String PATH_LIST = "list";
    public static final String PATH_ITEM= "item";

    //Default constructor
    public DatabaseContract() { }
}
