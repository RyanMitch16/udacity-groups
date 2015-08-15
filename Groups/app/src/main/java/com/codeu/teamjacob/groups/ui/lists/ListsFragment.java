package com.codeu.teamjacob.groups.ui.lists;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.GroupDatabase;
import com.codeu.teamjacob.groups.database.GroupEntry;
import com.codeu.teamjacob.groups.database.ListEntry;
import com.codeu.teamjacob.groups.database.UserDatabase;
import com.codeu.teamjacob.groups.database.UserEntry;
import com.codeu.teamjacob.groups.sync.GroupsPeriodicSyncService;
import com.codeu.teamjacob.groups.sync.GroupsSyncAccount;
import com.codeu.teamjacob.groups.ui.EntryLoader;

public class ListsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ListEntry[]>{

    /**
     * The callback to the activity when a list item is selected
     */
    public interface Callback{
        void onItemSelected(ListEntry listEntry);
        void onItemLongSelected(ListEntry listEntry);
    }

    //The list view and adapter that hold all the list entries
    public ListView listEntryView;
    public ListsEntryAdapter listsEntryAdapter;

    //The group the list resides in
    public GroupEntry groupEntry;
    public long groupId;

    public static final String ARG_GROUP_ID = "ARG_GROUP_ID";

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        //Get a reference to this fragment
        final ListsFragment thisFragment = this;

        groupId = getArguments().getLong(ARG_GROUP_ID);
        groupEntry = GroupDatabase.getById(getActivity(),groupId );
        Log.d("DD", "ID : " + groupEntry.getId());

        //Get the root view and the list view
        final View rootView = inflater.inflate(R.layout.fragment_lists, container, false);
        listEntryView = (ListView) rootView.findViewById(R.id.list_list_view);

        //Create the group adapter
        listsEntryAdapter = new ListsEntryAdapter((ListsActivity) getActivity(),R.layout.fragment_groups);
        listEntryView.setAdapter(listsEntryAdapter);
        listEntryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ListEntry listEntry = (ListEntry) parent.getItemAtPosition(position);

                ((Callback) getActivity()).onItemSelected(listEntry);
            }
        });

        listEntryView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                ListEntry listEntry = (ListEntry) parent.getItemAtPosition(position);

                ((Callback) getActivity()).onItemLongSelected(listEntry);
                return true;
            }
        });


        return rootView;

    }

    /**
     * Reload the data in the adapter
     */
    public void reloadData(){
        getLoaderManager().restartLoader(0, null, this);
    }

    /**
     * Initiate the loader to load the entries
     * @param savedInstanceState    the saved bundle
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Reloads the apater when the activity resumes
     */
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
     * @param id    the id of the loader
     * @param args  the extra loader arguments
     * @return      the loader to load the entries
     */
    @Override
    public Loader<ListEntry[]> onCreateLoader(int id, Bundle args) {

        return new EntryLoader<ListEntry[]>(getActivity()) {
            @Override
            public ListEntry[] loadInBackground() {
                //Get the user entry
                ListEntry[] lists = GroupDatabase.getById(getActivity(), groupId).getLists(getActivity(),false);
                return lists;
            }
        };
    }

    /**
     * Set the adapter to the data returned from the loader
     * @param loader        the entry loader
     * @param groupEntries  the entry data from the loader
     */
    @Override
    public void onLoadFinished(Loader<ListEntry[]> loader, ListEntry[] groupEntries) {
        listsEntryAdapter.set(groupEntries);
        listsEntryAdapter.notifyDataSetChanged();
    }

    /**
     * Reset the adapter when the loader resets
     * @param loader    the entry loader
     */
    @Override
    public void onLoaderReset(Loader<ListEntry[]> loader) {
        listsEntryAdapter.set(null);
    }

}