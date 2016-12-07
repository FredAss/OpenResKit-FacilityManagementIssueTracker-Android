package de.htwberlin.f2.FacilityManagementIssueTracker.adapters;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import de.htwberlin.f2.FacilityManagementIssueTracker.R;
import de.htwberlin.f2.FacilityManagementIssueTracker.Task;

public class TaskAdapter extends ArrayAdapter<Task> {

    private List<Task> tasks;

    public TaskAdapter(Context context, int textViewResourceId,
                       List<Task> tasks) {
        super(context, textViewResourceId, tasks);
        this.tasks = tasks;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.tasks_item_layout, null);
        }

        Task t = tasks.get(position);

        if (t != null) {
            if(t.getImage() != null){
                byte[] decodedByte = Base64.decode(t.getImage(), 0);
                ((ImageView)v.findViewById(R.id.imageView)).setImageBitmap(BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length));
            }
            TextView location = (TextView) v.findViewById(R.id.task_location);
            TextView description = (TextView) v.findViewById(R.id.task_description);

            if (location != null) {
                location.setText(t.getLocation());
            }
            if (description != null) {
                description.setText(t.getDescription());
            }

            if (t.getIsTaskFixed()) {
                v.setBackgroundColor(Color.rgb(46, 96, 51));
                location.setBackgroundColor(Color.rgb(46, 96, 51));
            } else {
                if(t.getDueDate() != null){
                    if (new Date().after(t.getDueDate())) {
                        v.setBackgroundColor(Color.rgb(141, 39, 46));
                        location.setBackgroundColor(Color.rgb(141, 39, 46));
                        return v;
                    }
                }
                v.setBackgroundColor(Color.BLACK);
                location.setBackgroundColor(Color.BLACK);
            }
        }


        return v;

    }


}

		
