package com.example.habittracker;

import android.app.Dialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.ViewHolder> {

    private ArrayList<Habit> habitList;
    private Context context;
    private DbHelper database;

    public HabitAdapter(ArrayList<Habit> habitList, Context context) {
        this.habitList = habitList;
        this.context = context;
        this.database = new DbHelper(context, "database.db", null, 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.habit_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit habit = habitList.get(position);

        holder.tv_description.setText(habit.getDescription());
        holder.tv_count.setText(habit.getCount() + " Per Day");

        String text = habit.getTitle();
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        holder.tv_title.setText(content);

        holder.fab_edit.setOnClickListener(v -> showEditDialog(habit, position));
        holder.fab_delete.setOnClickListener(v -> deleteHabit(habit, position));
    }

    private void showEditDialog(Habit habit, int position) {
        Dialog editDialog = new Dialog(context);
        editDialog.setContentView(R.layout.edit_habit_form);
        editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.dialog_background));
        editDialog.setCancelable(false);

        // Initialize dialog views
        EditText et_title = editDialog.findViewById(R.id.et_title_edit);
        EditText et_count = editDialog.findViewById(R.id.et_count_edit);
        TextInputEditText tiet_description = editDialog.findViewById(R.id.tiet_description_edit);
        Spinner spinner_weekdays = editDialog.findViewById(R.id.spinner_weekdays_edit);
        Button btn_update = editDialog.findViewById(R.id.btn_dialog_update);
        Button btn_cancel = editDialog.findViewById(R.id.btn_dialog_cancel_edit);

        TextInputLayout til_title = editDialog.findViewById(R.id.til_title_edit);
        TextInputLayout til_count = editDialog.findViewById(R.id.til_count_edit);

        // Set current values
        et_title.setText(habit.getTitle());
        et_count.setText(String.valueOf(habit.getCount()));
        tiet_description.setText(habit.getDescription());

        // Set up spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context, R.array.days_of_week, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_weekdays.setAdapter(adapter);

        // Set selected day
        String day = habit.getFrequency();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(day)) {
                spinner_weekdays.setSelection(i);
                break;
            }
        }

        // Set up cancel button
        btn_cancel.setOnClickListener(v -> editDialog.dismiss());

        // Set up update button
        btn_update.setOnClickListener(v -> {
            if (et_title.getText().toString().trim().isEmpty()) {
                til_title.setError("Title is required");
                return;
            }

            if (et_count.getText().toString().trim().isEmpty()) {
                til_count.setError("Count is required");
                return;
            }

            habit.setTitle(et_title.getText().toString().trim());
            habit.setDescription(tiet_description.getText().toString().trim());
            habit.setCount(Integer.parseInt(et_count.getText().toString().trim()));
            habit.setFrequency(spinner_weekdays.getSelectedItem().toString());

            if (database.updateHabit(habit)) {
                notifyItemChanged(position);
                Toast.makeText(context, "Habit updated!", Toast.LENGTH_SHORT).show();
                editDialog.dismiss();
            } else {
                Toast.makeText(context, "Failed to update habit", Toast.LENGTH_SHORT).show();
            }
        });

        editDialog.show();
    }

    private void deleteHabit(Habit habit, int position) {
        if (database.deleteHabit(habit.getId())) {
            habitList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, habitList.size());
            Toast.makeText(context, "Habit deleted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to delete habit", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_description, tv_count;
        FloatingActionButton fab_edit, fab_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_count = itemView.findViewById(R.id.tv_count);
            fab_edit = itemView.findViewById(R.id.fab_edit);
            fab_delete = itemView.findViewById(R.id.fab_delete);
        }
    }

    public void updateData(ArrayList<Habit> newHabits) {
        this.habitList = newHabits;
        notifyDataSetChanged();
    }
}