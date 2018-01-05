package com.example.wayne.homesecurity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class ViewReportActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    private List<Upload> uploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);

        TableView tableView = (TableView) findViewById(R.id.tableView);
        tableView.setColumnCount(4);

        mDatabase = FirebaseDatabase.getInstance().getReference(HomeSecurityMainActivity.DATABASE_PATH_UPLOADS);

        progressDialog = new ProgressDialog(this);
        uploads = new ArrayList<>();
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                progressDialog.dismiss();
                uploads.clear();

                int counter = 0;
                int number = 0;
                String startTime = "";
                String endTime = "";
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //if(!uploads.isEmpty()) {
                        for (Upload u : uploads) {
                            if(counter==0) {
                                startTime = postSnapshot.getKey().substring(9, 15);
                                Toast.makeText(ViewReportActivity.this, postSnapshot.getKey().substring(9, 15), Toast.LENGTH_SHORT).show();
                                endTime = postSnapshot.getKey().substring(9, 15);
                            } else if(counter>0) {
                                endTime = postSnapshot.getKey().substring(9, 15);
                                //Toast.makeText(ViewReportActivity.this, postSnapshot.getKey().substring(9, 15), Toast.LENGTH_SHORT).show();
                            }
                            if (u.getDate().equals(postSnapshot.getKey().substring(0, 8))) {
                                counter++;
                            }
                        }
                    //}

                    if(counter==0) {
                        number++;
                        Upload upload = new Upload(number+"", postSnapshot.getKey(), postSnapshot.getValue().toString(), postSnapshot.getKey().substring(0, 8), startTime, endTime);
                        //Toast.makeText(ViewReportActivity.this, postSnapshot.getKey().substring(0, 8), Toast.LENGTH_SHORT).show();
                        uploads.add(upload);
                    }
                }
                //Collections.reverse(uploads);

                TableView<Upload> table = findViewById(R.id.tableView);
                table.setHeaderAdapter(new SimpleTableHeaderAdapter(getApplicationContext(), "No.", "Date", "Start Time", "End Time"));
                table.setDataAdapter(new ReportTableDataAdapter(getApplicationContext(), uploads));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }
}

class ReportTableDataAdapter extends TableDataAdapter<Upload> {

    public ReportTableDataAdapter(Context context, List<Upload> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Upload upload = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 0:
                renderedView = renderNo();
                break;
            case 1:
                renderedView = renderDate(upload);
                break;
            case 2:
                renderedView = renderStartTime(upload);
                break;
            case 3:
                renderedView = renderEndTime(upload);
                break;
        }

        return renderedView;
    }

    public View renderNo() {

        final TextView textView = new TextView(getContext());
        textView.setText("1");
        textView.setPadding(20, 40, 20, 40);
        textView.setTextSize(12);

        return textView;
    }

    public View renderDate(Upload upload) {

        final TextView textView = new TextView(getContext());

        String date = upload.getDate();
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date convertedDate = new Date();
        try {
            convertedDate = (Date)formatter.parse(date);
            date = sdf.format(convertedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        textView.setText(date);
        textView.setPadding(20, 40, 20, 40);
        textView.setTextSize(12);

        return textView;
    }

    public View renderStartTime(Upload upload) {

        final TextView textView = new TextView(getContext());

        String date = upload.getStartTime();
        DateFormat formatter = new SimpleDateFormat("HHmmSS");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:SS");
        Date convertedDate = new Date();
        try {
            convertedDate = (Date)formatter.parse(date);
            date = sdf.format(convertedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        textView.setText(date);
        textView.setPadding(20, 40, 20, 40);
        textView.setTextSize(12);

        return textView;
    }

    public View renderEndTime(Upload upload) {

        final TextView textView = new TextView(getContext());

        String date = upload.getEndTime();
        DateFormat formatter = new SimpleDateFormat("HHmmSS");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:SS");
        Date convertedDate = new Date();
        try {
            convertedDate = (Date)formatter.parse(date);
            date = sdf.format(convertedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        textView.setText(date);
        textView.setPadding(20, 40, 20, 40);
        textView.setTextSize(12);

        return textView;
    }

}
