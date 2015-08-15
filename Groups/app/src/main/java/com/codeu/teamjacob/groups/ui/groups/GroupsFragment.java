package com.codeu.teamjacob.groups.ui.groups;


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
import com.codeu.teamjacob.groups.database.GroupEntry;
import com.codeu.teamjacob.groups.database.UserDatabase;
import com.codeu.teamjacob.groups.database.UserEntry;
import com.codeu.teamjacob.groups.sync.GroupsPeriodicSyncService;
import com.codeu.teamjacob.groups.sync.GroupsSyncAccount;
import com.codeu.teamjacob.groups.ui.EntryLoader;

public class GroupsFragment extends Fragment implements LoaderManager.LoaderCallbacks<GroupEntry[]>{

    /**
     * The callback when items are clicked on
     */
    public interface Callback{
        void onItemClick(GroupEntry entry);
        void onItemLongClick(GroupEntry entry);
    }

    //The group entry loader id
    public static final int GROUPS_LOADER = 0;

    ListView groupList;
    GroupEntryAdapter groupListAdapter;

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

        final GroupsFragment thisFragment = this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        //Get the root view and the list view
        final View rootView = inflater.inflate(R.layout.fragment_groups, container, false);
        groupList = (ListView) rootView.findViewById(R.id.group_list_view);

        //Create the group adapter
        groupListAdapter = new GroupEntryAdapter(getActivity(),R.layout.fragment_groups);
        groupList.setAdapter(groupListAdapter);

        //Set the callback of a click to the activity
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((Callback) getActivity()).onItemClick((GroupEntry) parent.getItemAtPosition(position));
            }
        });

        //Set the callback of a log click to the activity
        groupList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ((Callback) getActivity()).onItemLongClick((GroupEntry) parent.getItemAtPosition(position));
                return true;
            }
        });

        return rootView;
    }

    /**
     * Reload the data in the adapter
     */
    public void reloadData(){
        getLoaderManager().restartLoader(GROUPS_LOADER, null, this);
    }

    /**
     * Initiate the loader to load the entries
     * @param savedInstanceState    the saved bundle
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(GROUPS_LOADER, null, this);
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
    public Loader<GroupEntry[]> onCreateLoader(int id, Bundle args) {

        return new EntryLoader<GroupEntry[]>(getActivity()) {
            @Override
            public GroupEntry[] loadInBackground() {
                //Get the user entry
                UserEntry user = UserDatabase.getByKey(getActivity(),  GroupsSyncAccount.getUserKey(getActivity()));
                //Get the groups the user has access to
                GroupEntry[] groups = user.getGroups(getActivity());
                return groups;
            }
        };
    }

    /**
     * Set the adapter to the data returned from the loader
     * @param loader        the entry loader
     * @param groupEntries  the entry data from the loader
     */
    @Override
    public void onLoadFinished(Loader<GroupEntry[]> loader, GroupEntry[] groupEntries) {
        groupListAdapter.set(groupEntries);
        groupListAdapter.notifyDataSetChanged();
        Log.d("Entries",groupEntries.length+"");
    }

    /**
     * Reset the adapter when the loader resets
     * @param loader    the entry loader
     */
    @Override
    public void onLoaderReset(Loader<GroupEntry[]> loader) {
        groupListAdapter.set(null);
    }
}
