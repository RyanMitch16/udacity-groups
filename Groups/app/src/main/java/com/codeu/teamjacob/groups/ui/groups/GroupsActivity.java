package com.codeu.teamjacob.groups.ui.groups;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.GroupEntry;
import com.codeu.teamjacob.groups.database.UserDatabase;
import com.codeu.teamjacob.groups.sync.GroupsPeriodicSyncService;
import com.codeu.teamjacob.groups.sync.GroupsSyncAccount;
import com.codeu.teamjacob.groups.ui.Utility;
import com.codeu.teamjacob.groups.ui.lists.ListsActivity;
import com.codeu.teamjacob.groups.ui.login.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class GroupsActivity extends AppCompatActivity implements GroupsFragment.Callback, GroupEntryAdapter.Callback {

    //The log tag of the class
    public static final String LOG_TAG = GroupsActivity.class.getSimpleName();

    public static final String EXTRA_NEW_GROUP = "EXTRA_NEW_GROUP";

    public static final int REQUEST_NEW_GROUP = 0;


    private static final int REQUEST_CODE = 1;

    private Bitmap bitmap;

    private ImageView imageView;


    String userKey;

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    ListView mListView;
    ListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final GroupsActivity thisActivity = this;

        //Get the user key
        userKey = GroupsSyncAccount.getUserKey(this);

        //Check if the user is not logged in
        if (userKey.equals("")) {

            //Redirect to the login page
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }

        setContentView(R.layout.activity_groups);

        getSupportActionBar().setElevation(0f);


        imageView = (ImageView) findViewById(R.id.profile_img);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state.*/
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getSupportActionBar().setTitle("Closed");
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state.*/
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle("Opened");
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mListView = (ListView) findViewById(R.id.drawer_list);

        mListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_item, R.id.drawer_item_text_view, new String[]{"Change Icon", "Log out"}));


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, REQUEST_CODE);
                } else if (position == 1) {

                    GroupsSyncAccount.removeAccount(thisActivity);
                    Intent intent = new Intent(thisActivity, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        TextView usernameTextView = (TextView) findViewById(R.id.username_text_view);
        usernameTextView.setText(UserDatabase.getByKey(this, userKey).username);
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (!userKey.equals("")) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onEditPressed(GroupEntry groupEntry) {
        onItemLongClick(groupEntry);
    }

    @Override
    public void onItemClick(GroupEntry entry) {
        Intent intent = new Intent(this, ListsActivity.class);
        intent.putExtra(ListsActivity.EXTRA_GROUP_ID, entry.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(GroupEntry entry) {
        Intent intent = new Intent(this, EditGroupPopup.class);
        intent.putExtra(EditGroupPopup.EXTRA_GROUP_ID, entry.getId());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NEW_GROUP) {

            GroupsFragment groupsFragment = ((GroupsFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.groups_fragment));
            groupsFragment.reloadData();
        } else if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
            try {
                // We need to recyle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }
                InputStream stream = getContentResolver().openInputStream(
                        data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                stream.close();

                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 128, 128, true);

                imageView.setImageBitmap(scaled);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.groups, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_group) {
            Intent newGroup = new Intent(this, CreateGroupPopup.class);
            startActivityForResult(newGroup, REQUEST_NEW_GROUP);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
