package com.example.wayne.homesecurity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class ViewChartActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private BarChart barChart;
    public final String[] month = {"Jan","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    private String monthNum[] = {"01","02","03","04","05","06","07","08","09","10","11","12"};
    private String year;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    private List<Upload> uploadFirstVer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chart);

        mDatabase = FirebaseDatabase.getInstance().getReference(HomeSecurityMainActivity.DATABASE_PATH_UPLOADS);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                progressDialog.dismiss();
                uploadFirstVer = new ArrayList<Upload>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    uploadFirstVer.add(upload);
                }

                Spinner spinner = (Spinner) findViewById(R.id.spinner);
                ArrayList<String> years = new ArrayList<String>();
                int thisYear = Calendar.getInstance().get(Calendar.YEAR);
                year = thisYear+"";
                for (int i = thisYear; i >= 1990; i--) {
                    years.add(Integer.toString(i));
                }
                spinner.setOnItemSelectedListener(ViewChartActivity.this);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewChartActivity.this,
                        android.R.layout.simple_spinner_item, years);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private void setBarChart() {
        barChart = (BarChart) findViewById(R.id.chart);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateXY(2000, 2000);

        ArrayList<BarEntry> entries = new ArrayList<>();
        int count = 0;
        int count2 = 0;
        for(int i=1; i<month.length; i++) {
            count2 = 0;
            for(Upload u: uploadFirstVer) {
                if(u.getName().substring(4,6).equals(monthNum[i-1])&&u.getName().substring(0,4).equals(year)) {
                    count2++;
                }
            }
            count++;
            entries.add(new BarEntry(count, count2));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData data = new BarData(dataSet);
        data.setValueTextSize(20f);
        data.setValueTextColor(Color.BLACK);
        data.setValueFormatter(new PCValueFormatter());

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(12);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(month));

        Legend l = barChart.getLegend();
        l.setEnabled(false);

        if(!entries.isEmpty())
            barChart.setData(data);

        barChart.invalidate();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        year = spinner.getSelectedItem().toString();
        setBarChart();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

class PCValueFormatter implements IValueFormatter {

    private DecimalFormat mFormat;

    public PCValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0");
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

        if(value > 0) {
            return mFormat.format(value);
        } else {
            return "";
        }
    }
}
