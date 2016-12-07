package de.htwberlin.f2.FacilityManagementIssueTracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f2.FacilityManagementIssueTracker.adapters.TaskAdapter;

public class ListTasks extends Activity {

    private ListView listView;
    private ArrayAdapter<Task> listViewAdapter;
    private List<Task> taskList = new ArrayList<Task>();
    private Boolean taskWasAdded = false;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {
            Task resultTask = (Task) data.getSerializableExtra("ResultTask");

            if (taskWasAdded) {
                taskList.add(resultTask);
                taskWasAdded = false;
            } else {
                for (int i = 0; i < taskList.size(); i++) {
                    if (taskList.get(i).getId() == resultTask.getId()) {
                        taskList.set(i, resultTask);
                        break;
                    }
                }
            }
            listViewAdapter.notifyDataSetChanged();
        }
    }

    public void Initialize(JSONArray data) {
        taskList = PopulateTaskList(data);
        listViewAdapter = new TaskAdapter(this, R.id.list, taskList);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long id) {

                Intent createTask = new Intent(ListTasks.this,
                        CaptureTask.class);
                createTask.putExtra("EditTask", (Task) listView.getAdapter().getItem(position));
                startActivityForResult(createTask, 0);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tasks);

        new HubAccess.GetHubData(this).execute();

        Button addButton = (Button) findViewById(R.id.addButtonList);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskWasAdded = true;
                Intent createTask = new Intent(ListTasks.this,
                        CaptureTask.class);

                Task addTask = new Task();

                createTask.putExtra("EditTask", addTask);
                startActivityForResult(createTask, 0);

            }
        });
    }

    private List<Task> PopulateTaskList(JSONArray taskData) {
        JSONObject oneFootprintObject = null;
        ObjectMapper mapper = new ObjectMapper();
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        mapper.setDateFormat(df);
        List<Task> taskList = new ArrayList<Task>();

        for (int i = 0; i < taskData.length(); i++) {
            try {
                oneFootprintObject = taskData.getJSONObject(i);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                Task task = mapper.readValue(oneFootprintObject.toString(),
                        Task.class);
                taskList.add(task);
            } catch (JsonParseException e) {
                // TODO Auto-generated catch bl
                e.printStackTrace();
            } catch (JsonMappingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return taskList;
    }


}
