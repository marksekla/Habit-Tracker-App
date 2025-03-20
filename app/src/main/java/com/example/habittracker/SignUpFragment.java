package com.example.habittracker;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {

    private CheckBox checkBox;
    private EditText et_password, et_confrimPassword;
    private Drawable unlocked_drawable, locked_drawable;

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
        checkBox = (CheckBox)getView().findViewById(R.id.checkBox);
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
    }
}