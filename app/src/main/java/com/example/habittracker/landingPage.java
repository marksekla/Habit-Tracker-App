package com.example.habittracker;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class landingPage extends AppCompatActivity {

    TextView tv_message;
    EditText et_title, et_count;
    TextInputEditText tiet_description;
    Spinner spinner_weekdays;

    Intent logInIntent;
    FloatingActionButton fab_add;
    Dialog dialog;
    Button btn_dialog_add, btn_dialog_cancel;
    String title, description, selectedDay, type;
    Integer count, userId;
    DbHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_landing_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dialog = new Dialog(landingPage.this);
        dialog.setContentView(R.layout.add_habit_form);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.setCancelable(false);
        logInIntent = getIntent();
        tv_message = findViewById(R.id.tv_test);
        fab_add = findViewById(R.id.fab_add);
        et_title = dialog.findViewById(R.id.et_title);
        et_count = dialog.findViewById(R.id.et_count);
        tiet_description = dialog.findViewById(R.id.tiet_description);
        spinner_weekdays = dialog.findViewById(R.id.spinner_weekdays);
        btn_dialog_add = dialog.findViewById(R.id.btn_dialog_add);
        btn_dialog_cancel = dialog.findViewById(R.id.btn_dialog_cancel);

        database = new DbHelper(getApplicationContext(), "database.db", null, 1);
        database.getWritableDatabase();

        tv_message.setText("Hello " + logInIntent.getStringExtra("username") + "!");

        // sets weekdays to dropdown list
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.days_of_week, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_weekdays.setAdapter(adapter);

        spinner_weekdays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDay = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is selected
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        // close dialog when cancel is pressed
        btn_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_dialog_add.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                // return if the user did not enter a title
                if(et_title.getText().toString().isEmpty()){
                    Toast.makeText(landingPage.this, "Title is empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                // return if a user did not enter a count
                if(et_count.getText().toString().isEmpty()){
                    Toast.makeText(landingPage.this, "Count is empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                title = et_title.getText().toString();
                description = tiet_description.getText().toString();
                count = Integer.parseInt(et_count.getText().toString());
                selectedDay = spinner_weekdays.getSelectedItem().toString();
                type = "none"; //types can be added later if needed

                // get userID from database
                Cursor cursor = database.getUserByUsername(logInIntent.getStringExtra("username"));
                if (cursor != null && cursor.moveToFirst()) {
                    userId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                }

                // create new habit and add it to the database
                Habit habit = new Habit(userId, title, description, type, selectedDay, count);
                database.addHabit(habit);
                Log.d("habits", database.getHabitsByUserId(userId).toString());

                Toast.makeText(landingPage.this, "Habit added!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
}