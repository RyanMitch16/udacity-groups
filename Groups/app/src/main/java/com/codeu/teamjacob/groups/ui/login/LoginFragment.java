package com.codeu.teamjacob.groups.ui.login;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codeu.teamjacob.groups.sync.GroupsSyncAccount;
import com.codeu.teamjacob.groups.ui.groups.GroupsActivity;
import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.UserDatabase;
import com.codeu.teamjacob.groups.database.UserEntry;
import com.codeu.teamjacob.groups.sync.request.GroupsRequest;
import com.codeu.teamjacob.groups.sync.request.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;


public class LoginFragment extends Fragment implements LoaderManager.LoaderCallbacks<HttpURLConnection>{

    //The
    EditText txtUsername;
    EditText txtPassword;

    //The login button
    Button btnLogin;
    Button btnSignUp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        //Get a reference to this fragment
        final LoginFragment thisFragment = this;

        //Get the root view
        final View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        //Find the edit text views
        txtUsername = (EditText) rootView.findViewById(R.id.txt_username);
        txtPassword = (EditText) rootView.findViewById(R.id.txt_password);

        //Find the login button
        btnLogin = (Button) rootView.findViewById(R.id.btn_login);
        btnSignUp = (Button) rootView.findViewById(R.id.btn_signup);

        //Send the login request
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                getLoaderManager().initLoader(GroupsRequest.OPCODE_USER_LOGIN, args, thisFragment).forceLoad();
            }
        });

        //Go to the sign up activity
        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisFragment.getActivity(), SignUpActivity.class);
                startActivity(intent);

            }
        });

        return rootView;
    }

    @Override
    public Loader<HttpURLConnection> onCreateLoader(int id, Bundle args) {

        return GroupsRequest.userLogin(getActivity(),
                txtUsername.getText().toString(),
                txtPassword.getText().toString());
    }

    @Override
    public void onLoadFinished(Loader<HttpURLConnection> loader, HttpURLConnection data) {

        try {
            //Check if the response code is a success
            int id = data.getResponseCode();
            if (id >= 200 && id <300){

                //Get the user key from the request
                String userKey = HttpRequest.getContentString(data);

                UserEntry user = UserDatabase.getByKey(getActivity(), userKey);

                //Add the user to the database
                if (user == null){

                    //Set the values of the user
                    user = new UserEntry(userKey,txtUsername.getText().toString(), null,0);
                    UserDatabase.put(getActivity(), user);

                    GroupsSyncAccount.setUserKey(getActivity(), userKey);

                    Log.d("XX",  GroupsSyncAccount.getUserKey(getActivity()));
                } else {
                    GroupsSyncAccount.setUserKey(getActivity(), userKey);
                    Log.d("XX",user.username);
                }

                //Go to the main activity (this may need to be moved into the activity class)
                Intent intent = new Intent(getActivity(), GroupsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else{
                Toast toast = Toast.makeText(getActivity(),
                        "Unable to login. The username or password is incorrect.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }catch (IOException e){
            Log.d("Login",e.toString());
        }

        //Clean up so the request can be made again
        getLoaderManager().destroyLoader(GroupsRequest.OPCODE_USER_LOGIN);
    }

    @Override
    public void onLoaderReset(Loader<HttpURLConnection> loader) {

    }
}
