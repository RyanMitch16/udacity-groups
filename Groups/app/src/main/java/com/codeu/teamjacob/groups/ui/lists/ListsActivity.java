package com.codeu.teamjacob.groups.ui.lists;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.GroupDatabase;
import com.codeu.teamjacob.groups.database.GroupEntry;
import com.codeu.teamjacob.groups.database.ListEntry;
import com.codeu.teamjacob.groups.sync.GroupsPeriodicSyncService;
import com.codeu.teamjacob.groups.ui.Utility;
import com.codeu.teamjacob.groups.ui.items.ItemsActivity;
import com.codeu.teamjacob.groups.ui.items.ItemsFragment;

public class ListsActivity extends AppCompatActivity implements ListsFragment.Callback, ListsEntryAdapter.Callback{

    //The log tag of the class
    public static final String LOG_TAG = ListsActivity.class.getSimpleName();

    public static final String EXTRA_GROUP_ID = "EXTRA_GROUP_ID";
    public static final String EXTRA_NEW_LIST = "EXTRA_NEW_LIST";

    public static final int REQUEST_NEW_LIST = 0;

    private SharedPreferences mPrefs;

    public static long groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);


        Intent launchIntent = getIntent();

        if (launchIntent.hasExtra(EXTRA_GROUP_ID)){
            groupId = launchIntent.getLongExtra(EXTRA_GROUP_ID, -1);
            Utility.setGroupId(this, groupId);
        } else {
            groupId = Utility.getGroupId(this);
        }

        GroupEntry group = GroupDatabase.getById(this, groupId);
        getSupportActionBar().setTitle(group.groupName);
        getSupportActionBar().setElevation(0f);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putLong(ListsFragment.ARG_GROUP_ID, groupId);

            ListsFragment fragment = new ListsFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_list_container, fragment)
                    .commit();
        }


        Log.d("GROUPID", groupId + "");
    }

    @Override
    public void onItemSelected(ListEntry listEntry) {
        Intent intent = new Intent(this, ItemsActivity.class);
        intent.putExtra(ItemsActivity.EXTRA_LIST_ID, listEntry.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongSelected(ListEntry listEntry) {
        Intent intent = new Intent(this, EditListPopup.class);
        intent.putExtra(EditListPopup.EXTRA_LIST_ID, listEntry.getId());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NEW_LIST){
            //sopenList(data.getLongExtra(EXTRA_NEW_LIST, -1));
        }
    }

    @Override
    public void onEditPressed(ListEntry listEntry) {
        onItemLongSelected(listEntry);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_list) {
            Intent newGroup = new Intent(this, CreateListPopup.class);
            GroupEntry group = GroupDatabase.getById(this, groupId);
            newGroup.putExtra(CreateListPopup.EXTRA_GROUP_ID,group.getId());
            startActivityForResult(newGroup, REQUEST_NEW_LIST);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, GroupsPeriodicSyncService.class);
        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopService(new Intent(this, GroupsPeriodicSyncService.class));
    }


}
