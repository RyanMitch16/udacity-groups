package com.codeu.teamjacob.groups.ui.lists;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.GroupDatabase;
import com.codeu.teamjacob.groups.database.GroupEntry;
import com.codeu.teamjacob.groups.database.ListDatabase;
import com.codeu.teamjacob.groups.database.ListEntry;
import com.codeu.teamjacob.groups.sync.GroupsSyncAdapter;
import com.codeu.teamjacob.groups.ui.popups.PopupActivity;

public class CreateListPopup extends PopupActivity {

    public static final String EXTRA_GROUP_ID = "EXTRA_GROUP_ID";

    EditText txtName;
    TextView btnCreate;
    TextView btnCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_create_list);

        final CreateListPopup thisActivity = this;

        //Set the dimensions
        int width = convertWidthPercentToPixels(1.0f);
        int height = convertHeightPercentToPixels(0.3f);
        getWindow().setLayout(width, height);

        //Get the views
        txtName = (EditText) findViewById(R.id.text_box);
        btnCreate = (TextView) findViewById(R.id.create_button);

        final GroupEntry group = GroupDatabase.getById(this, getIntent().getLongExtra(EXTRA_GROUP_ID,-1));
        Log.d("CC","ID : "+group.getId());

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create the new group
                ListEntry listEntry = new ListEntry("null", txtName.getText().toString(), group.getId(),0);
                ListDatabase.put(thisActivity, listEntry);

                //Make the request to retrieve the group id
                GroupsSyncAdapter.syncCreateList(thisActivity, group.getId(), listEntry.getId(), txtName.getText().toString());

                Intent resultIntent = new Intent();
                resultIntent.putExtra(ListsActivity.EXTRA_NEW_LIST, listEntry.getId());
                setResult(Activity.RESULT_OK, resultIntent);
                thisActivity.finish();
            }
        });
    }
}
