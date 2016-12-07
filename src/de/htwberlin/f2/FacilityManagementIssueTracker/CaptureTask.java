package de.htwberlin.f2.FacilityManagementIssueTracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

import de.htwberlin.f2.FacilityManagementIssueTracker.HubAccess.WriteData;

public class CaptureTask extends Activity {

    private Task model;
    private EditText location;
    private EditText description;
    private EditText reportDate;
    private EditText dueDate;
    private ImageButton camera;
    private Button addButton;
    private CheckBox isFixed;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1337 && resultCode == RESULT_OK) {
            Bitmap bm = (Bitmap) data.getExtras().get("data");
            ((ImageButton) findViewById(R.id.imageButton)).setImageBitmap(bm);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            model.setImage(Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_task);

        model = (Task) getIntent().getSerializableExtra("EditTask");

        location = (EditText) findViewById(R.id.location);
        location.setText(model.getLocation());
        description = (EditText) findViewById(R.id.description);
        description.setText(model.getDescription());

        reportDate = (EditText) findViewById(R.id.reportDate);
        reportDate.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(model.getReportDate()));

        dueDate = (EditText) findViewById(R.id.dueDate);
        dueDate.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(model.getDueDate()));

        isFixed = (CheckBox)findViewById(R.id.isFixed);
        isFixed.setChecked(model.getIsTaskFixed());

        camera = (ImageButton) findViewById(R.id.imageButton);

        if (model.getImage() != null) {
            byte[] decodedByte = Base64.decode(model.getImage(), 0);
            camera.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length));
        }

        camera.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent takePicture = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 1337);
            }
        });

        addButton = (Button) findViewById(R.id.addButton);

        if (model.getId() > 0) {
            addButton.setText("Fertig");
        }

        addButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                model.setDescription(description.getText().toString());
                model.setLocation(location
                        .getText().toString());

                PushTaskToHub();
            }
        });

        isFixed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                model.setIsTaskFixed(isFixed.isChecked());
            }
        });

    }

    private void PushTaskToHub() {
        new WriteData(this).execute(model);
    }

    public void finishTask(Task resultTask) {
        Intent result = new Intent(CaptureTask.this, ListTasks.class);
        result.putExtra("ResultTask", resultTask);
        setResult(1, result);

        finish();
    }

}
