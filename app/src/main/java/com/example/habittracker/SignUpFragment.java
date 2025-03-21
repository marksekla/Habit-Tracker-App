package com.example.habittracker;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {

    private CheckBox checkBox;
    private EditText et_password, et_confrimPassword, et_username;
    private Drawable unlocked_drawable, locked_drawable;
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        unlocked_drawable = ContextCompat.getDrawable(getActivity(), R.drawable.baseline_lock_open_24);
        locked_drawable = ContextCompat.getDrawable(getActivity(), R.drawable.baseline_lock_24);
        et_password = (EditText)getView().findViewById(R.id.et_password);
        et_confrimPassword = (EditText)getView().findViewById(R.id.et_confrimPassword);
        et_username = (EditText) getView().findViewById(R.id.et_username);
        btn_login = (Button)getView().findViewById(R.id.btn_login);
        checkBox = (CheckBox)getView().findViewById(R.id.checkBox);
        intent = new Intent(getContext(), landingPage.class);

        database = new DbHelper(getContext(), "database", null, 1);
        database.getReadableDatabase();

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    et_password.setCompoundDrawablesRelativeWithIntrinsicBounds(unlocked_drawable, null, null, null);
                    et_password.setInputType(InputType.TYPE_CLASS_TEXT);
                    et_confrimPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(unlocked_drawable, null, null, null);
                    et_confrimPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    et_password.setCompoundDrawablesRelativeWithIntrinsicBounds(locked_drawable, null, null, null);
                    et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    et_confrimPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(locked_drawable, null, null, null);
                    et_confrimPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPasswordsValid() && isUsernameValid()){
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
            }
        });
    }

    private boolean isPasswordsValid() {
        // if passwords are not the same
        if(!et_password.getText().toString().equals(et_confrimPassword.getText().toString())){
            Toast toast = Toast.makeText(getActivity(), "Passwords do not match.", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        // if the passwords are empty
        if(et_password.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(getActivity(), "Passwords are empty.", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    private boolean isUsernameValid() {
        if(et_username.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(getActivity(), "Username is empty.", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        String username = et_username.getText().toString();
        Cursor cursor = database.getUserByUsername(username);

        // if the username already exists in the database return false
        if(cursor.getCount() != 0){
            Toast toast = Toast.makeText(getActivity(), "Username is already taken.", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }
}