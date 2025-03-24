package com.example.habittracker;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.ViewHolder>{

    private ArrayList<Habit> habitList;

    public HabitAdapter(ArrayList<Habit> habitList){
        this.habitList = habitList;
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

        String text= habit.getTitle();
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        holder.tv_title.setText(content);
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
        }
    }
}
