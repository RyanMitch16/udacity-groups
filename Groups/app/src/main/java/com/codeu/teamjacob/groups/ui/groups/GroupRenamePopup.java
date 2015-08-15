package com.codeu.teamjacob.groups.ui.groups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.GroupDatabase;
import com.codeu.teamjacob.groups.database.GroupEntry;
import com.codeu.teamjacob.groups.database.UserDatabase;
import com.codeu.teamjacob.groups.database.UserEntry;
import com.codeu.teamjacob.groups.sync.GroupsSyncAccount;
import com.codeu.teamjacob.groups.sync.GroupsSyncAdapter;
import com.codeu.teamjacob.groups.ui.popups.PopupActivity;

public class GroupRenamePopup extends PopupActivity{

    EditText txtName;
    TextView btnCreate;
    TextView btnCancel;


    public static final String EXTRA_GROUP_ID = "EXTRA_GROUP_ID";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_create_group);

        final GroupRenamePopup thisActivity = this;

        final long groupId = getIntent().getLongExtra(EXTRA_GROUP_ID, -1);

        //Set the dimensions
        int width = convertWidthPercentToPixels(1.0f);
        int height = convertHeightPercentToPixels(0.3f);
        getWindow().setLayout(width, height);

        //Get the views
        txtName = (EditText) findViewById(R.id.text_box);
        btnCreate = (TextView) findViewById(R.id.create_button);
        //btnCancel = (TextView) findViewById(R.id);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create the new grou

                GroupEntry groupEntry = GroupDatabase.getById(thisActivity, groupId);
                groupEntry.groupName = txtName.getText().toString();
                GroupDatabase.put(thisActivity, groupEntry);

                GroupsSyncAdapter.syncGroupRename(thisActivity, groupId, txtName.getText().toString());

                thisActivity.finish();
            }
        });


    }
}
