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

public class SignUpFragment extends Fragment {

    private TextInputEditText et_password, et_confrimPassword, et_username;
    private Button btn_login;
    private DbHelper database;
    private Intent intent;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance(String param1, String param2) {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Initialize views
        et_password = getView().findViewById(R.id.et_password);
        et_confrimPassword = getView().findViewById(R.id.et_confrimPassword);
        et_username = getView().findViewById(R.id.et_username);
        btn_login = getView().findViewById(R.id.btn_login);
        intent = new Intent(getContext(), landingPage.class);

        database = new DbHelper(getContext(), "database.db", null, 1);
        database.getReadableDatabase();

        // Sign up logic
        btn_login.setOnClickListener(v -> {
            if (isPasswordsValid() && isUsernameValid()) {
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();

                try {
                    database.addUser(username, password);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    private boolean isPasswordsValid() {
        if (!et_password.getText().toString().equals(et_confrimPassword.getText().toString())) {
            Toast.makeText(getActivity(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (et_password.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Passwords are empty.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isUsernameValid() {
        if (et_username.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Username is empty.", Toast.LENGTH_SHORT).show();
            return false;
        }

        String username = et_username.getText().toString();
        Cursor cursor = database.getUserByUsername(username);

        if (cursor.getCount() != 0) {
            Toast.makeText(getActivity(), "Username is already taken.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}