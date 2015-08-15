package com.codeu.teamjacob.groups.ui.groups;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.GroupDatabase;
import com.codeu.teamjacob.groups.database.GroupEntry;
import com.codeu.teamjacob.groups.database.UserDatabase;
import com.codeu.teamjacob.groups.database.UserEntry;
import com.codeu.teamjacob.groups.sync.GroupsPeriodicSyncService;
import com.codeu.teamjacob.groups.sync.GroupsSyncAccount;
import com.codeu.teamjacob.groups.sync.GroupsSyncAdapter;
import com.codeu.teamjacob.groups.ui.EntryAdapter;
import com.codeu.teamjacob.groups.ui.Utility;

public class GroupEntryAdapter extends EntryAdapter<GroupEntry> {

    /**
     * Holds references to all the views in the group items
     */
    public class ViewHolder{

        public final TextView listNameView;
        public final ImageButton imageButtonEdit;
        public final Button acceptButton;
        public final Button denyButton;
        public final ImageView groupPhoto;
        public int viewType;

        public ViewHolder(View view, int viewType) {
            listNameView = (TextView) view.findViewById(R.id.group_name_text_view);
            imageButtonEdit = (ImageButton) view.findViewById(R.id.group_edit_button);
            if (imageButtonEdit != null){
                imageButtonEdit.setFocusable(false);}
            acceptButton = (Button) view.findViewById(R.id.accept_btn);
            denyButton = (Button) view.findViewById(R.id.deny_btn);
            groupPhoto = (ImageView) view.findViewById(R.id.group_photo);
            this.viewType = viewType;
        }
    }

    public interface Callback{

        void onEditPressed(GroupEntry groupEntry);
    }

    public static final int VIEW_TYPE_NORMAL = 0;
    public static final int VIEW_TYPE_CONFIRM = 1;

    public String userKey;
    public String userName;

    public GroupEntryAdapter(Activity context, @LayoutRes int resource){
        super(context, resource);

        userKey = GroupsSyncAccount.getUserKey(context);
        userName = UserDatabase.getByKey(context, userKey).username;
    }

    @Override
    public View newView(final Context context, final int position, ViewGroup parent) {

        int layoutId = -1;
        int viewType = getItemViewType(position);

        switch (viewType){
            case VIEW_TYPE_NORMAL:
                layoutId = R.layout.item_group;
                break;
            case VIEW_TYPE_CONFIRM:
                layoutId = R.layout.item_group_confirm;
                break;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder holder = new ViewHolder(view, viewType);
        view.setTag(holder);

        return view;
    }

    @Override
    public View bindView(final Context context, final int position, View view) {

        ViewHolder holder = (ViewHolder) view.getTag();

        int viewType = getItemViewType(position);
        if (holder.viewType != viewType){

            //Recreate the view
            view = newView(context,position,(ViewGroup) view.getParent());
            holder = (ViewHolder) view.getTag();
        }

        holder.listNameView.setText(getItem(position).groupName);

        if (viewType == VIEW_TYPE_NORMAL) {
            holder.imageButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Callback) (context)).onEditPressed(getItem(position));
                }
            });
        }

        if (viewType == VIEW_TYPE_CONFIRM){
            holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupEntry groupEntry = GroupDatabase.getById(getContext(), getItem(position).getId());

                    UserEntry userEntry = UserDatabase.getByKey(getContext(),userKey);

                    groupEntry.removePendingUser(userEntry.username);
                    groupEntry.addUser(userEntry.username);

                    GroupDatabase.put(getContext(), groupEntry);

                    GroupsSyncAdapter.syncGroupConfirm(getContext(), userKey, groupEntry.getId(), true);

                    Intent newIntent = new Intent(GroupsPeriodicSyncService.BROADCAST_ACTION);
                    getContext().sendBroadcast(newIntent);
                }
            });

            holder.denyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    GroupEntry groupEntry = GroupDatabase.getById(getContext(), getItem(position).getId());

                    UserEntry userEntry = UserDatabase.getByKey(getContext(), userKey);

                    userEntry.removeGroup(groupEntry.getId());
                    UserDatabase.put(getContext(), userEntry);

                    Log.d("Groups", userEntry.groupIds.toString());

                    GroupsSyncAdapter.syncGroupConfirm(getContext(), userKey, groupEntry.getId(), false);

                    Intent newIntent = new Intent(GroupsPeriodicSyncService.BROADCAST_ACTION);
                    getContext().sendBroadcast(newIntent);

                }
            });
        }

        if (GroupDatabase.getById(getContext(), getItem(position).getId()).photo != null) {
            holder.groupPhoto.setImageBitmap(getItem(position).photo);
        }

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return GroupDatabase.getById(context,
                getItem(position).getId()).isInPendingUsers(context,userName)
                ? VIEW_TYPE_CONFIRM : VIEW_TYPE_NORMAL;
    }
}

