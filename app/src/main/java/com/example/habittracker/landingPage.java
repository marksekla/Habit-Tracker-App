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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class landingPage extends AppCompatActivity {

    TextView tv_message, tv_no_habits;
    EditText et_title, et_count;
    TextInputEditText tiet_description;
    Spinner spinner_weekdays;
    TabLayout tabLayout;
    TextInputLayout til_title, til_count, til_description;

    Intent logInIntent;
    FloatingActionButton fab_add;
    Dialog dialog;
    Button btn_dialog_add, btn_dialog_cancel;
    RecyclerView recyclerView;
    TextView btnLogout;  // Add logout button reference
    String title, description, selectedDay, type;
    Integer count, userId;
    DbHelper database;
    HabitAdapter habitAdapter;
    String currentDay = "All"; // Default to showing all habits

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
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.setCancelable(false);

        // Initialize views
        logInIntent = getIntent();
        tv_message = findViewById(R.id.tv_test);
        tv_no_habits = findViewById(R.id.tv_no_habits);
        fab_add = findViewById(R.id.fab_add);

        // Dialog views with updated references
        et_title = dialog.findViewById(R.id.et_title);
        et_count = dialog.findViewById(R.id.et_count);
        tiet_description = dialog.findViewById(R.id.tiet_description);
        spinner_weekdays = dialog.findViewById(R.id.spinner_weekdays);
        btn_dialog_add = dialog.findViewById(R.id.btn_dialog_add);
        btn_dialog_cancel = dialog.findViewById(R.id.btn_dialog_cancel);

        // New TextInputLayout references
        til_title = dialog.findViewById(R.id.til_title);
        til_count = dialog.findViewById(R.id.til_count);
        til_description = dialog.findViewById(R.id.til_description);

        recyclerView = findViewById(R.id.recyclerView);
        tabLayout = findViewById(R.id.tabLayout3);
        btnLogout = findViewById(R.id.btn_logout);  // Reference to logout button

        database = new DbHelper(getApplicationContext(), "database.db", null, 1);
        database.getWritableDatabase();

        // Get userID from database
        Cursor cursor = database.getUserByUsername(logInIntent.getStringExtra("username"));
        if (cursor != null && cursor.moveToFirst()) {
            userId = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id")));
        }

        // Set LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set Adapter - initially show day indicators as we start in "All" tab
        habitAdapter = new HabitAdapter(database.getHabitsByUserId(userId), this, true);
        recyclerView.setAdapter(habitAdapter);

        tv_message.setText("Hello " + logInIntent.getStringExtra("username") + "!");

        // Set weekdays to dropdown list
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

        // Tab layout filter setup
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String day = tab.getText().toString();
                currentDay = day;

                // Show day indicators only in "All" tab
                boolean showDayIndicator = "All".equals(day);
                updateHabitsList(showDayIndicator);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Show dialog on FAB click
        fab_add.setOnClickListener(v -> {
            resetDialogFields();
            dialog.show();
        });

        // Cancel dialog
        btn_dialog_cancel.setOnClickListener(v -> dialog.dismiss());

        // Add habit button click
        btn_dialog_add.setOnClickListener(v -> {
            if (validateInputs()) {
                createAndSaveHabit();
                Toast.makeText(landingPage.this, "Habit added!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                updateHabitsList("All".equals(currentDay));
            }
        });

        // Set up logout functionality
        btnLogout.setOnClickListener(v -> performLogout());

        // Initial habit display
        updateHabitsList(true); // Initially show day indicators
    }

    private void performLogout() {
        // Clear any session data or other logout logic here

        // Redirect to the MainActivity which hosts both login and signup tabs
        Intent intent = new Intent(landingPage.this, MainActivity.class);

        // Add an extra to indicate we want to show the Login tab (index 1) instead of the default Signup (index 0)
        intent.putExtra("tab_index", 1);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Close the current activity to prevent returning to it
    }

    private void resetDialogFields() {
        et_title.setText("");
        et_count.setText("");
        tiet_description.setText("");
        spinner_weekdays.setSelection(0);

        // Clear any error states
        til_title.setError(null);
        til_count.setError(null);
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (et_title.getText().toString().trim().isEmpty()) {
            til_title.setError("Title is required");
            isValid = false;
        } else {
            til_title.setError(null);
        }

        if (et_count.getText().toString().trim().isEmpty()) {
            til_count.setError("Count is required");
            isValid = false;
        } else {
            til_count.setError(null);
        }

        return isValid;
    }

    private void createAndSaveHabit() {
        title = et_title.getText().toString().trim();
        description = tiet_description.getText().toString().trim();
        count = Integer.parseInt(et_count.getText().toString().trim());
        selectedDay = spinner_weekdays.getSelectedItem().toString();
        type = "none"; // Optional for future use

        Habit habit = new Habit(userId, title, description, type, selectedDay, count);
        database.addHabit(habit);
    }

    private void updateHabitsList(boolean showDayIndicator) {
        ArrayList<Habit> habits;

        if (currentDay.equals("All")) {
            habits = database.getHabitsByUserId(userId);
        } else {
            habits = database.getHabitsByUserIdAndDay(userId, currentDay);
        }

        if (habits.isEmpty()) {
            tv_no_habits.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tv_no_habits.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            habitAdapter.updateData(habits, showDayIndicator);
        }
    }

    // Overloaded method for backward compatibility
    private void updateHabitsList() {
        updateHabitsList("All".equals(currentDay));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHabitsList("All".equals(currentDay));
    }
}