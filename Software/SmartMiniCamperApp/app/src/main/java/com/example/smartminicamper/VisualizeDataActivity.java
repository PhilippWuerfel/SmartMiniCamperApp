package com.example.smartminicamper;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartminicamper.webservice.download.data.DownloadProgressResponseBody;
import com.example.smartminicamper.webservice.download.data.MeasurementPointDownloader;
import com.example.smartminicamper.webservice.restservice.model.MeasurementPointModel;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VisualizeDataActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "VisualizeDate";

    private GraphView dataGraph;
    private LineGraphSeries<DataPoint> series;

    private Button calendarButton;

    TextView startDateText;
    TextView endDateText;

    private ProgressBar progressBar;

    // @GET("/smartminicamper/db")
    // Call<ArrayList<MeasurementPointModel>> getMeasurementPoints();
    private ArrayList<MeasurementPointModel> measurementPoints;

    final MeasurementPointDownloader measurementPointDownloader = new MeasurementPointDownloader(progressBar);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize_data);


        addSpinner();
        // Initializing of the app elements
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataGraph = findViewById(R.id.graph1);
        series = new LineGraphSeries<DataPoint>();

        //calendarButton = findViewById(R.id.calendar_button);

        startDateText = (TextView) findViewById(R.id.textViewStartDate);
        endDateText = (TextView) findViewById(R.id.textViewEndDate);

        startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendarActivity();
            }
        });

        endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendarActivity();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //createGraphFromJson(item);
        return super.onOptionsItemSelected(item);
    }

    public void openCalendarActivity(){
        Intent intent = new Intent(VisualizeDataActivity.this, CalendarDateSelectionActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        String startDate = "No Date Selected";
        String endDate = "No Date Selected";

        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                startDate = data.getStringExtra("startDate");
                endDate = data.getStringExtra("endDate");
                //Toast.makeText(VisualizeDataActivity.this, "Starting Date: " + startDate, Toast.LENGTH_SHORT).show();
            }
        }
        startDateText.setText(startDate);
        endDateText.setText(endDate);
    }

    private void createGraphFromMeasurementPointList(String selectedItem){
        Date startDate = new Date();
        Date endDate = new Date();
        MeasurementPointModel measurementPoint;

        double y;

        try{
            dataGraph.removeAllSeries();
            series = new LineGraphSeries<DataPoint>();

            for(int i=0; i<measurementPoints.size();i++){
                measurementPoint = measurementPoints.get(i);
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(measurementPoint.getDate());

                switch(selectedItem){
                    case "Battery":
                        y = measurementPoint.getBatteryVoltage();
                        break;
                    case "Incoming Current":
                        y = measurementPoint.getPowerCharge();
                        break;
                    case "Outgoing Current":
                        y = measurementPoint.getPowerUsage();
                        break;
                    default:
                        y = measurementPoint.getBatteryVoltage();
                        break;
                }
                series.appendData(new DataPoint(date,y),true,601);
                // set the start and enddate to scale the x-axis
                if(i==0){
                    startDate = date;
                }
                else if(i==measurementPoints.size()-1){
                    endDate = date;
                }
            }
        }
        catch(Exception e){
            Log.e(TAG, "createGraphFromMeasurementPointList: (adding data): " + e.getMessage());
        }
        try {
            dataGraph.addSeries(series);

            DateFormat dateFormat = new SimpleDateFormat("EEE");
            dataGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(VisualizeDataActivity.this, dateFormat));
            dataGraph.getGridLabelRenderer().setNumHorizontalLabels(3); // because of the space

            // as we use dates as labes for the x-axis we just need humanRounding at the y-axis
            // which also leeds to nicer diagrams
            dataGraph.getGridLabelRenderer().setHumanRounding(false, true);

            // set manual x bounds to have nice steps
            dataGraph.getViewport().setMinX(startDate.getTime());
            dataGraph.getViewport().setMaxX(endDate.getTime());
            dataGraph.getViewport().setXAxisBoundsManual(true);
        }
        catch (Exception e){
            Log.e(TAG, "createGraphFromJson(creating graph): " + e.getMessage());
        }
        //return jsonData;
    }

    private String createGraphFromJson(String selectedItem){
        JSONObject jObj = new JSONObject();
        String jsonData = "";
        Date startDate = new Date();
        Date endDate = new Date();

        double y;

        try{
            jsonData = loadJSONFromAsset(this);

            dataGraph.removeAllSeries();
            series = new LineGraphSeries<DataPoint>();

            JSONArray arrayAllDatapoints = new JSONArray(jsonData);

            for(int i=0; i<arrayAllDatapoints.length();i++){
                JSONObject singleDatapointObject = arrayAllDatapoints.getJSONObject(i);
                String dateString = singleDatapointObject.getString("date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);

                switch(selectedItem){
                    case "Battery":
                        String currentString = singleDatapointObject.getString("currentBat");
                        y = Double.parseDouble(currentString);
                        break;
                    case "Incoming Current":
                        currentString = singleDatapointObject.getString("currentIn");
                        y = Double.parseDouble(currentString);
                        break;
                    case "Outgoing Current":
                        currentString = singleDatapointObject.getString("currentOut");
                        y = Double.parseDouble(currentString);
                        break;
                    default:
                        currentString = singleDatapointObject.getString("currentBat");
                        y = Double.parseDouble(currentString);
                        break;
                }
                series.appendData(new DataPoint(date,y),true,601);
                // set the start and enddate to scale the x-axis
                if(i==0){
                    startDate = date;
                }
                else if(i==arrayAllDatapoints.length()-1){
                    endDate = date;
                }
            }
        }
        catch(Exception e){
            Log.e(TAG, "createGraphFromJson(adding data): " + e.getMessage());
        }
        try {
            dataGraph.addSeries(series);

            DateFormat dateFormat = new SimpleDateFormat("EEE");
            dataGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(VisualizeDataActivity.this, dateFormat));
            dataGraph.getGridLabelRenderer().setNumHorizontalLabels(3); // because of the space

            // as we use dates as labes for the x-axis we just need humanRounding at the y-axis
            // which also leeds to nicer diagrams
            dataGraph.getGridLabelRenderer().setHumanRounding(false, true);

            // set manual x bounds to have nice steps
            dataGraph.getViewport().setMinX(startDate.getTime());
            dataGraph.getViewport().setMaxX(endDate.getTime());
            dataGraph.getViewport().setXAxisBoundsManual(true);
        }
        catch (Exception e){
            Log.e(TAG, "createGraphFromJson(creating graph): " + e.getMessage());
        }
        return jsonData;
    }

    // METHOD TO LOAD LOCAL DUMMY DATA
    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("testdatajson.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex) {
            Log.e(TAG, "loadJSONFromAsset: " + ex.getStackTrace());
            return null;
        }
        return json;
    }

    // ADDS A SPINNER TO SHOW DIFFERENT DIAGRAM OPTIONS
    public void addSpinner(){
        Spinner mySpinner = (Spinner) findViewById(R.id.spinner1); // DEN SPINNER NOCH IN EINE EIGENE KLASSE HAUEN
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(VisualizeDataActivity.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.dropDownDiagramOptions));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
        mySpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedOption = parent.getItemAtPosition(position).toString();
        //createGraphFromJson(selectedOption);
        //createGraphFromMeasurementPointList(selectedOption);
        synchronizeMeasurementPoints(selectedOption);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void synchronizeMeasurementPoints(final String selectedOption){
        // testing @GET("/smartminicamper/db")
        measurementPointDownloader.downloadMeasurementPointList(new DownloadProgressResponseBody.DownloadCallbacks() {
            @Override
            public void onProgressUpdate(long bytesRead, long contentLength, boolean done) {
                Log.d(TAG, "onProgressUpdate: " + "BytesRead=" + bytesRead + " ContentLength=" + contentLength + " Done=" + done);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(VisualizeDataActivity.this,"Error downloading measurement points from server" ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                measurementPoints = measurementPointDownloader.getDownloadedMeasurementPointList();
                createGraphFromMeasurementPointList(selectedOption);
                //loadBar.setProgress((int)Math.round(measurementPoints.get(1).getBatteryVoltage()));
            }
        });
    }



}
