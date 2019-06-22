package com.example.smartminicamper;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartminicamper.webservice.download.data.DownloadProgressResponseBody;
import com.example.smartminicamper.webservice.download.data.MeasurementPointDownloader;
import com.example.smartminicamper.webservice.restservice.model.MeasurementPointModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class LandingPageActivity extends AppCompatActivity implements GetDataFromRestWebservice.OnDownloadComplete {

    private static final String TAG = "LandingPageActivity";

    private Button changeButton;
    private ProgressBar loadBar;
    private double batteryValue = 0.0;
    private TextView batteryPercentage;
    private TextView testTextView;
    private Spinner mySpinner;

    private TextView txtPowerChargeValue;
    private TextView txtPowerUsageValue;

    // Dummy Button for testing WebServerConnection
    private Button btnWebServCon;
    GetDataFromRestWebservice getDataFromRestWebservice = new GetDataFromRestWebservice(this);

    private ProgressBar progressBar;

    // @GET("/smartminicamper/db")
    // Call<ArrayList<MeasurementPointModel>> getMeasurementPoints();
    private ArrayList<MeasurementPointModel> measurementPoints;

    // @GET("/smartminicamper/db_aktuelle_daten")
    // Call<MeasurementPointModel> getSingleMeasurementPoint();
    private MeasurementPointModel measurementPoint;

    // smartminicamper/db_zeitraum
    //@GET("/smartminicamper/db_zeitraum")
    private ArrayList<MeasurementPointModel> measurementPointListInRange;

    // smartminicamper/db_zeitraum
    // @GET("/test")
    private String testResult;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        // ADDING A SPINNER
        Spinner mySpinner = (Spinner) findViewById(R.id.spinner1); // DEN SPINNER NOCH IN EINE EIGENE KLASSE HAUEN
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(LandingPageActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.dropDownTimespanes));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        // ADDING A TOOLBAR
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initializing app elements
        changeButton = (Button) findViewById(R.id.button);
        loadBar = (ProgressBar) findViewById(R.id.progressBar);
        loadBar.setProgress((int)Math.round(batteryValue)); // tbd value currently not connected to any data, just hardcoded
        progressBar = new ProgressBar(this);

        txtPowerChargeValue = (TextView) findViewById(R.id.txtPowerChargeValue);
        txtPowerUsageValue = (TextView) findViewById(R.id.txtPowerUsageValue);

        getNewData();

        getJsonStuff();

        // Dummy Button for testing WebServerConnection
        btnWebServCon = (Button)findViewById(R.id.btnDummyWebTest);


        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVisualizeDataGraph();

            }
        });

        btnWebServCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MeasurementPointDownloader measurementPointDownloader = new MeasurementPointDownloader(progressBar);

                /*
                // testing @GET("/smartminicamper/db")
                measurementPointDownloader.downloadMeasurementPointList(new DownloadProgressResponseBody.DownloadCallbacks() {
                    @Override
                    public void onProgressUpdate(long bytesRead, long contentLength, boolean done) {
                        Log.d(TAG, "onProgressUpdate: " + "BytesRead=" + bytesRead + " ContentLength=" + contentLength + " Done=" + done);
                    }

                    @Override
                    public void onError() {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LandingPageActivity.this,"Error downloading measurement points from server" ,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        measurementPoints = measurementPointDownloader.getDownloadedMeasurementPointList();
                        //loadBar.setProgress((int)Math.round(measurementPoints.get(1).getBatteryVoltage()));
                    }
                });
                */

                // testing @GET("/smartminicamper/db_aktuelle_daten")
                measurementPointDownloader.downloadSingleMeasurementPoint(new DownloadProgressResponseBody.DownloadCallbacks() {
                    @Override
                    public void onProgressUpdate(long bytesRead, long contentLength, boolean done) {
                        Log.d(TAG, "onProgressUpdate: " + "BytesRead=" + bytesRead + " ContentLength=" + contentLength + " Done=" + done);
                    }

                    @Override
                    public void onError() {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LandingPageActivity.this,"Error downloading measurement point from server" ,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        measurementPoint = measurementPointDownloader.getDownloadedMeasurementPoint();
                        loadBar.setProgress((int)Math.round(measurementPoint.getBatteryVoltage()));

                        txtPowerChargeValue.setText(String.valueOf(measurementPoint.getPowerCharge()));
                        txtPowerUsageValue.setText(String.valueOf(measurementPoint.getPowerUsage()));
                        Log.d(TAG, "onFinish: downloaded: " + measurementPoint.toString());
                    }
                });
                /*

                // testing @GET("/smartminicamper/db_zeitraum")
                try{
                    String startDateAsString = "2019-06-20";
                    Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateAsString);
                    String endDateAsString = "2019-06-20";
                    Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateAsString);

                    measurementPointDownloader.downloadMeasurementPointListInRange(startDate, endDate, new DownloadProgressResponseBody.DownloadCallbacks() {
                        @Override
                        public void onProgressUpdate(long bytesRead, long contentLength, boolean done) {
                            Log.d(TAG, "onProgressUpdate: " + "BytesRead=" + bytesRead + " ContentLength=" + contentLength + " Done=" + done);
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LandingPageActivity.this,"Error downloading measurement point from server" ,Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFinish() {
                            measurementPointListInRange = measurementPointDownloader.getDownloadedMeasurementPointListInRange();
                            //Log.d(TAG, "onFinish: downloaded: " + measurementPointListInRange.toString());
                        }
                    });
                }catch(Exception e){
                    Log.e(TAG, "onClick: " + e.getMessage() );
                }
                */
                /*

                // testing @GET("/test")
                try{
                    String startDateAsString = "2019-06-20";
                    Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateAsString);
                    String endDateAsString = "2019-06-20";
                    Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateAsString);

                    measurementPointDownloader.testWebserver(startDate, endDate, new DownloadProgressResponseBody.DownloadCallbacks() {
                        @Override
                        public void onProgressUpdate(long bytesRead, long contentLength, boolean done) {
                            Log.d(TAG, "onProgressUpdate: " + "BytesRead=" + bytesRead + " ContentLength=" + contentLength + " Done=" + done);
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LandingPageActivity.this,"Error downloading measurement point from server" ,Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFinish() {
                            testResult = measurementPointDownloader.getTestResult();
                            Log.d(TAG, "onFinish: downloaded: " + testResult);
                        }
                    });
                }catch(Exception e){
                    Log.e(TAG, "onClick: " + e.getMessage() );
                }
                */

                //getDataFromRestWebservice.execute(RestWebserviceSettings.getBaseUrl()+"/test");

            }
        });


    }

    private String getJsonStuff(){
        // testTextView = (TextView) findViewById(R.id.textView2);
        JSONObject jObj = new JSONObject();
        String jsonData = "lol";
        String jsonParsed = "not parsed";

        jsonData = loadJSONFromAsset(this);
        try{
            JSONArray array = new JSONArray(jsonData);
            for(int i=0; i<array.length();i++){
                jsonParsed = array.getJSONArray(0).toString();
    /*            jObj = new JSONObject(jsonData);
                jsonData = jObj.getString("current");*/
            }
        }
        catch(Exception e){
            Log.e(TAG, "getJsonStuff: " + e.getMessage());
        }


        // testTextView.setText(jsonParsed + ":\n" + new Date().toString());

        return jsonData;
    }

    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("testdatajson.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException e) {
            Log.e(TAG, "loadJSONFromAsset: " + e.getStackTrace());
            return null;
        }
        return json;

    }

    private void getNewData(){
        batteryPercentage = (TextView) findViewById(R.id.txtBatteryPercentage);
        //WAS WENN KEINE DATEN VERFÜGBAR SIND?????
        batteryPercentage.setText(batteryValue + " %");

    }

    public void openVisualizeDataGraph(){
        Intent intent = new Intent(this, VisualizeDataActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDownloadComplete(JSONArray data, DownloadStatus status) {
        if(status==DownloadStatus.OK){
            Log.d(TAG, "onDownloadComplete: downloaded data is " + data.toString());
            //Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_SHORT).show();

            // JSON TEST

            try {
                //JSONObject jsonObject = data.getJSONObject(0);
                Toast.makeText(getApplicationContext(),data.get(0).toString(),Toast.LENGTH_LONG).show();
            }catch (Exception e){
                Log.d(TAG, "onDownloadComplete: Problems with JSON " + e.getStackTrace());
            }


        }else{
            // download failed
            Log.e(TAG, "onDownloadComplete failed with status " + status );
            Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_SHORT).show();
        }
        getDataFromRestWebservice = new GetDataFromRestWebservice(this);
    }
}