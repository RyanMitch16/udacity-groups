package com.codeu.teamjacob.groups.ui.lists;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.GroupDatabase;
import com.codeu.teamjacob.groups.database.GroupEntry;
import com.codeu.teamjacob.groups.database.ListDatabase;
import com.codeu.teamjacob.groups.database.ListEntry;
import com.codeu.teamjacob.groups.sync.GroupsSyncAdapter;
import com.codeu.teamjacob.groups.ui.popups.PopupActivity;

public class ListRenamePopup extends PopupActivity {

    public static final String EXTRA_LIST_ID = "EXTRA_LIST_ID";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_create_group);

        final ListRenamePopup thisActivity = this;

        final long listId = getIntent().getLongExtra(EXTRA_LIST_ID, -1);

        //Set the dimensions
        int width = convertWidthPercentToPixels(1.0f);
        int height = convertHeightPercentToPixels(0.3f);
        getWindow().setLayout(width, height);

        //Get the views
        final EditText txtName = (EditText) findViewById(R.id.text_box);
        TextView btnCreate = (TextView) findViewById(R.id.create_button);
        //btnCancel = (TextView) findViewById(R.id);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create the new grou

                ListEntry listEntry = ListDatabase.getById(thisActivity, listId);
                listEntry.listName = txtName.getText().toString();
                ListDatabase.put(thisActivity, listEntry);

                GroupsSyncAdapter.syncListRename(thisActivity, listId, txtName.getText().toString());

                thisActivity.finish();
            }
        });


    }
}
