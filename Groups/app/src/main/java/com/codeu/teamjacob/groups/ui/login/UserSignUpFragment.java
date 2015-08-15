package com.codeu.teamjacob.groups.ui.login;

/**
 * Created by saryal on 8/10/15.
 */

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.UserDatabase;
import com.codeu.teamjacob.groups.database.UserEntry;
import com.codeu.teamjacob.groups.sync.request.GroupsRequest;
import com.codeu.teamjacob.groups.sync.request.HttpRequest;
import com.codeu.teamjacob.groups.ui.EmailAsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;

public class UserSignUpFragment extends Fragment implements LoaderManager.LoaderCallbacks<HttpURLConnection> {

    public final static String EXTRA_MESSAGE = "com.codeu.teamjacob.groups.ui.login";

    EditText txtUsername;
    EditText txtPassword;
    EditText txtPasswordRetype;
    EditText txtEmail;
     Button btnSignUp;

    String username;
    String password;
    String repassword;
    String email;
    String popupmsg;
    boolean checkedMyNumber;
    boolean checkedShareContact;

    //Get a reference to this fragment
    final UserSignUpFragment thisFragment = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popupmsg = "";
        checkedMyNumber = checkedShareContact = false;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Get the root view
        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);

        //Find the edit text views
        txtUsername = (EditText) rootView.findViewById(R.id.username_text_view);
        txtPassword = (EditText) rootView.findViewById(R.id.password_text_view);
        txtPasswordRetype = (EditText) rootView.findViewById(R.id.password_retype_text_view);
        txtEmail = (EditText) rootView.findViewById(R.id.email_text_view);

        //Find the login button
        btnSignUp = (Button) rootView.findViewById(R.id.sign_up_button);

        //Go to the sign up activity
        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Get the user data
                username = txtUsername.getText().toString();
                password = txtPassword.getText().toString();
                repassword = txtPasswordRetype.getText().toString();
                email = txtEmail.getText().toString();

                //validate user input
                if (validateAllUserInput()) {

                    // create a new user
                    Bundle args = new Bundle();
                    if (checkedShareContact) {
                        readContacts();
                        getLoaderManager().initLoader(GroupsRequest.OPCODE_USER_CREATE, args, thisFragment).forceLoad();
                    } else {
                        getLoaderManager().initLoader(GroupsRequest.OPCODE_USER_CREATE, args, thisFragment).forceLoad();
                    }
                } else {
                    Toast toast = Toast.makeText(getActivity(),
                            popupmsg, Toast.LENGTH_SHORT);
                    toast.show();
                    popupmsg = "";
                }
            }
        });

        return rootView;
    }

    @Override
    public Loader<HttpURLConnection> onCreateLoader(int id, Bundle args) {

        return GroupsRequest.userCreate(getActivity(),
                username,
                password);
    }

    @Override
    public void onLoadFinished(Loader<HttpURLConnection> loader, HttpURLConnection data) {

        try {
            //Check if the response code is a success
            int id = data.getResponseCode();
            if (id >= 200 && id < 300) {

                //Get the user key from the request
                String userKey = HttpRequest.getContentString(data);

                Log.d("Hello", "1");
                //Check if the user exists in the database

                UserEntry users[] = UserDatabase.query(getActivity(),
                        UserDatabase.COLUMN_USER_KEY + " = ?",
                        new String[]{userKey}, null);

                Log.d("Hello", "2ss");
                //Add the user to the database
                if (users.length == 0) {
                    Log.d("Hello", "3");

                    //Set the values of the user
                    UserEntry user = new UserEntry(userKey, username, null,0);
                    UserDatabase.put(getActivity(), user);
                    Log.d("NewUser", user.userKey);
                    try {
                        String receiverEmail = email;
                        new EmailAsyncTask().execute(receiverEmail,
                                username,
                                userKey);
                        // Log.e("Check point", "Get inside the gmail sender");
                    } catch (Exception e) {
                        Log.e("SendMail", e.getMessage(), e);
                    }
                }


                //Go to the login page after usr is created
                Intent intent = new Intent(thisFragment.getActivity(), LoginActivity.class);
                String[] user_info = {username, password};
                intent.putExtra(EXTRA_MESSAGE, user_info);
                startActivity(intent);

            } else {
                Toast toast = Toast.makeText(getActivity(),
                        "User with the given user name already exists.", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (IOException e) {
            Log.d("Login", e.toString());
        }

        //Clean up so the request can be made again
        getLoaderManager().destroyLoader(GroupsRequest.OPCODE_USER_CREATE);
    }

    @Override
    public void onLoaderReset(Loader<HttpURLConnection> loader) {
    }

    public boolean validateAllUserInput() {
        boolean isusernamevaild = validateUserName();
        boolean ispasswordvalid = validatePassword();
        boolean isemailvalid = validateEmail();
        if (isemailvalid && ispasswordvalid && isusernamevaild) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validateUserName() {
        if (username.length() <= 0) {
            popupmsg += "Please enter a user name.\n";
        } else if (username.length() > 15 || username.length() < 3) {
            popupmsg += "User name must be between 3 to 15 characters.\n";
            txtUsername.setText(null);
        } else {
            return true;
        }
        return false;
    }

    public boolean validatePassword() {
        if (password.equals(repassword) && password.length() >= 5) {
            return true;
        } else if (password.length() == 0) {
            popupmsg += "Please enter a password.\n";

        } else if (password.length() < 5) {
            popupmsg += "Please enter a password that is 5 character long.\n";

        } else {
            popupmsg += "Please enter the password again, password mis-match.\n";
        }
        txtPassword.setText(null);
        txtPasswordRetype.setText(null);
        return false;
    }

    public boolean validateEmail() {

        String Expn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        if (email.length() == 0) {
            return true;
        } else if (email.matches(Expn) && email.length() > 0) {
            return true;
        } else

        {
            popupmsg += "Please re-enter a valid email address.\n";
            txtEmail.setText(null);
            return false;
        }

    }

    public void readContacts() {
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        Log.e("Length", "" + cur.getCount());
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    System.out.println("name : " + name + ", ID : " + id);

                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        // I guess a code block must be added to add phoen number to db

                        Log.e("phone", " " + phone);
                    }
                    pCur.close();
                }
            }
        }
    }

}