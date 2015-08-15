package com.codeu.teamjacob.groups.ui.lists;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.ListDatabase;
import com.codeu.teamjacob.groups.database.ListEntry;
import com.codeu.teamjacob.groups.sync.GroupsSyncAdapter;
import com.codeu.teamjacob.groups.ui.groups.AddGroupUserPopup;
import com.codeu.teamjacob.groups.ui.groups.GroupRenamePopup;
import com.codeu.teamjacob.groups.ui.popups.PopupActivity;

public class EditListPopup extends PopupActivity{


    public static final String EXTRA_LIST_ID = "EXTRA_LIST_ID";

    long listId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup_edit_list);

        int width = convertWidthPercentToPixels(0.8f);
        int height = convertHeightPercentToPixels(0.6f*0.6f);

        final EditListPopup thisActivity = this;

        listId = getIntent().getLongExtra(EXTRA_LIST_ID,-1);

        getWindow().setLayout(width, height);

        TextView listName = (TextView) findViewById(R.id.popup_list_name);
        listName.setText(ListDatabase.getById(this, listId).listName);

        LinearLayout deleteList = (LinearLayout) findViewById(R.id.leave_list);
        deleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ListEntry listEntry = ListDatabase.getById(thisActivity, listId);
                listEntry.removed = true;
                ListDatabase.put(thisActivity, listEntry);

                GroupsSyncAdapter.syncListDelete(thisActivity, listId);

                finish();
            }
        });

        LinearLayout renameList = (LinearLayout) findViewById(R.id.rename_list);
        renameList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(thisActivity, ListRenamePopup.class);
                intent.putExtra(ListRenamePopup.EXTRA_LIST_ID, listId);
                startActivity(intent);
                finish();
            }
        });


    }
}
