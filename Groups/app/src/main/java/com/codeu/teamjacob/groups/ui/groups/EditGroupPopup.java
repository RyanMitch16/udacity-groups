package com.codeu.teamjacob.groups.ui.groups;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.GroupDatabase;
import com.codeu.teamjacob.groups.database.GroupEntry;
import com.codeu.teamjacob.groups.sync.GroupsSyncAccount;
import com.codeu.teamjacob.groups.sync.GroupsSyncAdapter;
import com.codeu.teamjacob.groups.ui.Utility;
import com.codeu.teamjacob.groups.ui.popups.PopupActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class EditGroupPopup extends PopupActivity {

    public static final String EXTRA_GROUP_ID = "EXTRA_GROUP_ID";

    private static final int REQUEST_CODE = 1;

    long groupId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_edit_group);

        int width = convertWidthPercentToPixels(0.8f);
        int height = convertHeightPercentToPixels(0.6f);

        final EditGroupPopup thisActivity = this;

        getWindow().setLayout(width, height);

        Intent launchIntent = getIntent();

        if (launchIntent.hasExtra(EXTRA_GROUP_ID)){
            groupId = launchIntent.getLongExtra(EXTRA_GROUP_ID, -1);
            Utility.setGroupId(this, groupId);
        } else {
            groupId = Utility.getGroupId(this);
        }

        final GroupEntry groupEntry = GroupDatabase.getById(this, groupId);

        //Get the views
        TextView groupName = (TextView) findViewById(R.id.popup_group_name);
        groupName.setText(groupEntry.groupName);

        LinearLayout addUsers = (LinearLayout) findViewById(R.id.add_user);
        addUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, AddGroupUserPopup.class);
                intent.putExtra(AddGroupUserPopup.EXTA_GROUP_ID, groupId);
                startActivity(intent);
                finish();

            }
        });

        LinearLayout renameGroup = (LinearLayout) findViewById(R.id.rename_group);
        renameGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(thisActivity, GroupRenamePopup.class);
                intent.putExtra(GroupRenamePopup.EXTRA_GROUP_ID, groupId);
                startActivity(intent);
                finish();

            }
        });

        LinearLayout changeIcon = (LinearLayout) findViewById(R.id.change_icon);
        changeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, REQUEST_CODE);
            }
        });

        LinearLayout leaveGroup = (LinearLayout) findViewById(R.id.leave_group);
        leaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GroupsSyncAdapter.syncGroupLeave(thisActivity,
                        GroupsSyncAccount.getUserKey(thisActivity), groupId);

                groupEntry.removed = true;
                GroupDatabase.put(thisActivity, groupEntry);

                finish();

            }
        });
        //btnCreate = (TextView) findViewById(R.id.create_button);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;
        try {
            InputStream stream = getContentResolver().openInputStream(
                    data.getData());
            bitmap = BitmapFactory.decodeStream(stream);
            stream.close();

            GroupEntry groupEntry = GroupDatabase.getById(this, groupId);

            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 128, 128, true);

            groupEntry.photo = scaled;
            GroupDatabase.put(this, groupEntry);

            GroupsSyncAdapter.syncGroupSetImage(this, groupId);

            Log.d("BEFORE",Utility.bitMapToString(groupEntry.photo));


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        finish();

    }
}