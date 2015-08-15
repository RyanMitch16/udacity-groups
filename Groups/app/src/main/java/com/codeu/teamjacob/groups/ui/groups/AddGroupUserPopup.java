package com.codeu.teamjacob.groups.ui.groups;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.sync.GroupsSyncAdapter;
import com.codeu.teamjacob.groups.sync.request.GroupsRequest;
import com.codeu.teamjacob.groups.sync.request.HttpRequest;
import com.codeu.teamjacob.groups.ui.popups.PopupActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class AddGroupUserPopup extends PopupActivity {

    public long groupId;
    public static final String EXTA_GROUP_ID = "EXTA_GROUP_ID";

    public static final String ARG_USERNAME = "ARG_USERNAME";

    ArrayList<String> userNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_add_user);

        //Set the dimensions
        int width = convertWidthPercentToPixels(0.9f);
        int height = convertHeightPercentToPixels(0.9f);
        getWindow().setLayout(width, height);

        groupId = getIntent().getLongExtra(EXTA_GROUP_ID, -1);

        userNames = new ArrayList<String>();


        final AddGroupUserPopup thisActivity = this;

        final EditText addUserEditText = (EditText) findViewById(R.id.add_user_edit_text);
        addUserEditText.setImeActionLabel("Next", KeyEvent.KEYCODE_ENTER);

        final HorizontalScrollView addedUsersScrollView =
                (HorizontalScrollView) findViewById(R.id.added_users_scroll_view);

        final LinearLayout addedUsersView =
                (LinearLayout) findViewById(R.id.added_users_view);


        addUserEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (s.charAt(s.length() - 1) == '\n') {

                        if (s.length() == 1) {
                            addUserEditText.setText("");
                            return;
                        }

                        final LinearLayout user = (LinearLayout) LayoutInflater.from(thisActivity).inflate(
                                R.layout.item_user_added, null, false);


                        String userNameStr = addUserEditText.getText().toString().replace("\n", "");

                        addedUsersView.addView(user);
                        TextView userName = (TextView) user.findViewById(R.id.user_name);
                        userName.setText(userNameStr);
                        userNames.add(userNameStr);

                        ImageButton removeUser = (ImageButton) user.findViewById(R.id.remove_user);
                        removeUser.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addedUsersView.removeView(user);
                                userNames.remove(addUserEditText.getText().toString());
                            }
                        });

                        addUserEditText.setText("");

                        addedUsersScrollView.postDelayed(new Runnable() {
                            public void run() {
                                addedUsersScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                            }
                        }, 100L);
                    } else if (s.charAt(s.length() - 1) == ' ') {
                        addUserEditText.setText(addUserEditText.getText().toString().replace(" ", ""));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*addUserEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    final LinearLayout user = (LinearLayout) LayoutInflater.from(thisActivity).inflate(
                            R.layout.item_user_added, null, false);

                    addedUsersView.addView(user);
                    TextView userName = (TextView) user.findViewById(R.id.user_name);
                    userName.setText(addUserEditText.getText());
                    userNames.add(addUserEditText.getText().toString());

                    ImageButton removeUser = (ImageButton) user.findViewById(R.id.remove_user);
                    removeUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addedUsersView.removeView(user);
                            userNames.remove(addUserEditText.getText().toString());
                        }
                    });

                    addUserEditText.setText("");

                    addedUsersScrollView.postDelayed(new Runnable() {
                        public void run() {
                            addedUsersScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                        }
                    }, 100L);

                    return true;
                }
                return false;
            }
        });*/

        ImageButton btnCreate = (ImageButton) findViewById(R.id.confirm_btn);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GroupsSyncAdapter.syncGroupAddUsers(thisActivity, groupId, userNames.toArray(new String[userNames.size()]));
                finish();
            }
        });

    }

}