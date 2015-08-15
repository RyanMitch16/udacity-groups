package com.codeu.teamjacob.groups.ui.items;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.ItemDatabase;
import com.codeu.teamjacob.groups.database.ItemEntry;
import com.codeu.teamjacob.groups.database.ListDatabase;
import com.codeu.teamjacob.groups.database.ListEntry;
import com.codeu.teamjacob.groups.database.UserDatabase;
import com.codeu.teamjacob.groups.sync.GroupsSyncAccount;
import com.codeu.teamjacob.groups.sync.GroupsSyncAdapter;
import com.codeu.teamjacob.groups.ui.lists.ListsActivity;
import com.codeu.teamjacob.groups.ui.popups.PopupActivity;

public class CreateItemPopup extends PopupActivity {

    public static final String EXTRA_LIST_ID = "EXTRA_GROUP_ID";

    EditText txtName;
    TextView btnCreate;
    TextView btnCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_create_item);

        final CreateItemPopup thisActivity = this;

        //Set the dimensions
        int width = convertWidthPercentToPixels(1.0f);
        int height = convertHeightPercentToPixels(0.3f);
        getWindow().setLayout(width, height);

        //Get the views
        txtName = (EditText) findViewById(R.id.text_box);
        btnCreate = (TextView) findViewById(R.id.create_button);
        //btnCancel = (TextView) findViewById(R.id);

        final ListEntry list = ListDatabase.getById(this, getIntent().getLongExtra(EXTRA_LIST_ID, -1));

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create the new group
                ItemEntry itemEntry = new ItemEntry("",txtName.getText().toString(),list.getId());
                ItemDatabase.put(thisActivity, itemEntry);
                itemEntry.itemAppEngineId = UserDatabase.getByKey(thisActivity,
                        GroupsSyncAccount.getUserKey(thisActivity)).username+itemEntry.getId();
                ItemDatabase.put(thisActivity, itemEntry);

                Log.d("XX", list.getId() + "");

                //Make the request to retrieve the group id
                //GrocerySyncAdapter.sendListCreate(thisActivity, txtName.getText().toString(), list.getId());

                GroupsSyncAdapter.syncAddItem(thisActivity,list.getId(),itemEntry.getId());

                Intent resultIntent = new Intent();
                resultIntent.putExtra(ListsActivity.EXTRA_NEW_LIST, itemEntry.getId());
                setResult(Activity.RESULT_OK, resultIntent);
                thisActivity.finish();
            }
        });
    }
}