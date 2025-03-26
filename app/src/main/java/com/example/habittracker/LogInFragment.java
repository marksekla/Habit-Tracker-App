package com.example.habittracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

public class LogInFragment extends Fragment {

    private TextInputEditText et_password, et_username;
    private Button btn_login;
    private DbHelper database;
    private Intent intent;

    public LogInFragment() {
        // Required empty public constructor
    }

    public static LogInFragment newInstance(String param1, String param2) {
        return new LogInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Initialize views
        et_password = getView().findViewById(R.id.et_password);
        et_username = getView().findViewById(R.id.et_username);
        btn_login = getView().findViewById(R.id.btn_login);
        intent = new Intent(getContext(), landingPage.class);

        database = new DbHelper(getContext(), "database.db", null, 1);
        database.getReadableDatabase();

        // Handle login
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordsValid() && isUsernameValid() && isValidUser()) {
                    String username = et_username.getText().toString();
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean isPasswordsValid() {
        if (et_password.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Password is empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isUsernameValid() {
        if (et_username.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Username is empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isValidUser() {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();

        Cursor cursor = database.getUserByUsernameAndPassword(username, password);

        if (cursor.getCount() == 0) {
            Toast.makeText(getActivity(), "Username or password is incorrect.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}