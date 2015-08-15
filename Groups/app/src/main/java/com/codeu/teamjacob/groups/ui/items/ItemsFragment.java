package com.codeu.teamjacob.groups.ui.items;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.GroupEntry;
import com.codeu.teamjacob.groups.database.ItemEntry;
import com.codeu.teamjacob.groups.database.ListDatabase;
import com.codeu.teamjacob.groups.database.ListEntry;
import com.codeu.teamjacob.groups.sync.GroupsPeriodicSyncService;
import com.codeu.teamjacob.groups.sync.GroupsSyncAdapter;
import com.codeu.teamjacob.groups.ui.EntryLoader;

public class ItemsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ItemEntry[]> {

    /**
     * The callback when items are clicked on
     */
    public interface Callback {
        void onItemClick(GroupEntry entry);

        void onItemLongClick(GroupEntry entry);
    }

    public static final String EXTRA_LIST_ID = "EXTRA_LIST_ID";

    //The group entry loader id
    public static final int GROUPS_LOADER = 3;

    ListView groupList;
    ItemEntryAdapter itemEntryAdapter;

    public ListEntry listEntry;

    public boolean editMode;

    Menu menuOptions;

    IntentFilter filter1 = new IntentFilter(GroupsPeriodicSyncService.BROADCAST_ACTION);

    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            reloadData();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        editMode = false;

        //Get the root view and the list view
        final View rootView = inflater.inflate(R.layout.fragment_item, container, false);


        listEntry = ListDatabase.getById(getActivity(), getArguments().getLong(EXTRA_LIST_ID));

        groupList = (ListView) rootView.findViewById(R.id.item_list_view);

        //Create the group adapter
        itemEntryAdapter = new ItemEntryAdapter(getActivity(), R.layout.fragment_item);
        groupList.setAdapter(itemEntryAdapter);

        //Set the callback of a click to the activity
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //((Callback) getActivity()).onItemClick((GroupEntry) parent.getItemAtPosition(position));
            }
        });

        //Set the callback of a log click to the activity
        groupList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                menuOptions.findItem(R.id.action_edit_mode).setVisible(false);
                menuOptions.findItem(R.id.action_trash_items).setVisible(true);
                menuOptions.findItem(R.id.action_select_all_items).setVisible(true);

                itemEntryAdapter.toggleEditMode();
                editMode = true;
                //((Callback) getActivity()).onItemLongClick((GroupEntry) parent.getItemAtPosition(position));
                return false;
            }
        });

        return rootView;
    }

    /**
     * Reload the data in the adapter
     */
    public void reloadData() {
        getLoaderManager().restartLoader(GROUPS_LOADER, null, this);
    }

    /**
     * Initiate the loader to load the entries
     *
     * @param savedInstanceState the saved bundle
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(GROUPS_LOADER, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(myReceiver, filter1);
        reloadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(myReceiver);
    }


    /**
     * Create the loader to load the entries into the adapter
     *
     * @param id   the id of the loader
     * @param args the extra loader arguments
     * @return the loader to load the entries
     */
    @Override
    public Loader<ItemEntry[]> onCreateLoader(int id, Bundle args) {

        return new EntryLoader<ItemEntry[]>(getActivity()) {
            @Override
            public ItemEntry[] loadInBackground() {

                //Get the groups the user has access to
                ItemEntry[] groups = listEntry.getItems(getActivity());
                Log.d("Items","Id: "+listEntry.getId()+" , "+groups.length+"");
                return groups;
            }
        };
    }


    public boolean onBackPressed(){
        itemEntryAdapter.toggleEditMode();
        if (editMode){
            editMode = false;
            menuOptions.findItem(R.id.action_select_all_items).setVisible(false);
            menuOptions.findItem(R.id.action_trash_items).setVisible(false);
            menuOptions.findItem(R.id.action_edit_mode).setVisible(true);
            return true;
        }
        return false;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.item, menu);
        menuOptions = menu;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.action_trash_items).setVisible(false);
        menu.findItem(R.id.action_select_all_items).setVisible(false);
        menu.findItem(R.id.action_edit_mode).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_mode:

                menuOptions.findItem(R.id.action_edit_mode).setVisible(false);
                menuOptions.findItem(R.id.action_trash_items).setVisible(true);
                menuOptions.findItem(R.id.action_select_all_items).setVisible(true);

                itemEntryAdapter.toggleEditMode();
                editMode = true;
                return true;
            case R.id.action_trash_items:
                String[] itemsToDeleteIds = itemEntryAdapter.delete();
                if (itemsToDeleteIds.length > 0) {
                    GroupsSyncAdapter.syncItemsDelete(getActivity(), listEntry.getId(), itemsToDeleteIds);
                }

            return true;
            case R.id.action_select_all_items:
                itemEntryAdapter.selectAll(true);
                return true;
            default:
                return false;
        }
    }

    /**
     * Set the adapter to the data returned from the loader
     *
     * @param loader       the entry loader
     * @param groupEntries the entry data from the loader
     */
    @Override
    public void onLoadFinished(Loader<ItemEntry[]> loader, ItemEntry[] groupEntries) {
        itemEntryAdapter.set(groupEntries);
        itemEntryAdapter.notifyDataSetChanged();
    }

    /**
     * Reset the adapter when the loader resets
     *
     * @param loader the entry loader
     */
    @Override
    public void onLoaderReset(Loader<ItemEntry[]> loader) {
        itemEntryAdapter.set(null);
    }

}