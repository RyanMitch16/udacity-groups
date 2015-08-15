package com.codeu.teamjacob.groups.ui.items;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.GroupDatabase;
import com.codeu.teamjacob.groups.database.GroupEntry;
import com.codeu.teamjacob.groups.database.ListDatabase;
import com.codeu.teamjacob.groups.database.ListEntry;
import com.codeu.teamjacob.groups.sync.GroupsPeriodicSyncService;
import com.codeu.teamjacob.groups.ui.FloatingButtonView;
import com.codeu.teamjacob.groups.ui.groups.CreateGroupPopup;
import com.codeu.teamjacob.groups.ui.lists.CreateListPopup;
import com.codeu.teamjacob.groups.ui.lists.ListsFragment;

public class ItemsActivity extends AppCompatActivity {

    public static final String EXTRA_LIST_ID = "EXTRA_LIST_ID";

    public long listId;

    public FloatingButtonView newItemButton;


    // Creates fake items
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);



        final ItemsActivity thisActivity = this;

        listId = getIntent().getLongExtra(EXTRA_LIST_ID, -1);
        ListEntry list = ListDatabase.getById(this, listId);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(list.listName);
        ab.setElevation(0f);

        newItemButton = (FloatingButtonView) findViewById(R.id.new_item_button);
        newItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newGroup = new Intent(thisActivity, CreateItemPopup.class);
                ListEntry listEntry = ListDatabase.getById(thisActivity, listId);
                newGroup.putExtra(CreateItemPopup.EXTRA_LIST_ID,listEntry.getId());
                startActivityForResult(newGroup, 0);
            }
        });

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putLong(ItemsFragment.EXTRA_LIST_ID, listId);

            ItemsFragment fragment = new ItemsFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_item_container, fragment)
                    .commit();
        }

    }

    @Override
    public void onBackPressed()
    {

        ItemsFragment fragment = (ItemsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_item_container);
        if (fragment.onBackPressed()){
            return;
        }
        super.onBackPressed();
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