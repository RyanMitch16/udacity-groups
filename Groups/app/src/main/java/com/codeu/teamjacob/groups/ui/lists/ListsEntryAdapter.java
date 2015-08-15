package com.codeu.teamjacob.groups.ui.lists;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.ListEntry;
import com.codeu.teamjacob.groups.ui.EntryAdapter;
import com.codeu.teamjacob.groups.ui.LetterCircleView;

public class ListsEntryAdapter extends EntryAdapter<ListEntry> {

    public interface Callback{
        void onEditPressed(ListEntry listEntry);
    }

    public class ViewHolder{

        public final TextView listNameView;
        public final ImageButton btnEdit;
        public final LetterCircleView circleView;

        public ViewHolder(View view) {
            listNameView = (TextView) view.findViewById(R.id.list_name_text_view);
            btnEdit = (ImageButton) view.findViewById(R.id.list_edit_button);
            btnEdit.setFocusable(false);
            circleView = (LetterCircleView) view.findViewById(R.id.letterCircleview);
        }
    }

    ListsActivity activityContext;

    public ListsEntryAdapter(ListsActivity context, @LayoutRes int resource){
        super(context, resource);
        activityContext = context;
    }

    @Override
    public View newView(Context context, final int position, ViewGroup parent) {
        View view =  LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        return view;
    }

    @Override
    public View bindView(final Context context, final int position, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.listNameView.setText(getItem(position).listName);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) (context)).onEditPressed(getItem(position));
            }
        });
        holder.circleView.setLetter(getItem(position).listName.charAt(0));

        return view;
    }

}