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

public class CreateGroupPopup extends PopupActivity {

    EditText txtName;
    TextView btnCreate;
    TextView btnCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_create_group);

        final CreateGroupPopup thisActivity = this;

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

                //Create the new group
                GroupEntry group = new GroupEntry("",txtName.getText().toString(),null,null,0);
                GroupDatabase.put(thisActivity, group);

                GroupsSyncAdapter.syncCreateGroup(thisActivity, group.getId(),
                        txtName.getText().toString());

                //Add the list to the list the user can access
                UserEntry user = UserDatabase.getByKey(
                        thisActivity,GroupsSyncAccount.getUserKey(thisActivity));
                user.addGroup(group.getId());
                UserDatabase.put(thisActivity, user);

                //Make the request to retrieve the group id
                //GrocerySyncAdapter.sendListCreate(thisActivity, txtName.getText().toString(), list.getId());

                Intent resultIntent = new Intent();
                resultIntent.putExtra(GroupsActivity.EXTRA_NEW_GROUP, group.getId());
                setResult(Activity.RESULT_OK, resultIntent);
                thisActivity.finish();
            }
        });


    }
}