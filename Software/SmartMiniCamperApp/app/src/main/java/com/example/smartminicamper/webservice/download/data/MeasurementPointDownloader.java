package com.example.smartminicamper.webservice.download.data;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.smartminicamper.webservice.ClientRestWebservice;
import com.example.smartminicamper.webservice.RestWebserviceSettings;
import com.example.smartminicamper.webservice.restservice.model.MeasurementPointModel;
import com.example.smartminicamper.webservice.task.bodies.DateRangeTask;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MeasurementPointDownloader {
    // this will call the GET Methods available from Rest Webserver on Raspberry Pi

    private static final String TAG = "MeasurementPointDownloa";
    private static final int DEFAULT_TIMEOUT = 15; //change this to control time till timeout

    private ProgressBar progressBar;
    // @GET("/smartminicamper/db")
    // Call<ArrayList<MeasurementPointModel>> getMeasurementPoints();
    private ArrayList<MeasurementPointModel> downloadedMeasurementPointList;

    // @GET("/smartminicamper/db_aktuelle_daten")
    // Call<MeasurementPointModel> getSingleMeasurementPoint();
    private ArrayList<MeasurementPointModel> downloadedMeasurementPoint;

    // @GET("/smartminicamper/db_zeitraum")
    // Call<ArrayList<MeasurementPointModel>> getMeasurementPointsInDateRange(@Header("StartDate") String startDate, @Header("EndDate") String endDate);
    private ArrayList<MeasurementPointModel> downloadedMeasurementPointListInRange;

    Retrofit retrofit;
    ClientRestWebservice clientRestWebservice;

    public MeasurementPointDownloader(ProgressBar progressBar) {
        this.progressBar = progressBar;

        // creating retrofit object
        retrofit = new Retrofit.Builder()
                .baseUrl(RestWebserviceSettings.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // creating client
        clientRestWebservice = retrofit.create(ClientRestWebservice.class);
    }

    public ArrayList<MeasurementPointModel> getDownloadedMeasurementPointList() {
        return downloadedMeasurementPointList;
    }

    public MeasurementPointModel getDownloadedMeasurementPoint() {
        return downloadedMeasurementPoint.get(0);
    }

    public ArrayList<MeasurementPointModel> getDownloadedMeasurementPointListInRange() {
        return downloadedMeasurementPointListInRange;
    }

    public void downloadMeasurementPointList(final DownloadProgressResponseBody.DownloadCallbacks downloadCallback){

        //Set up Progressbar
        try{
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
        }catch (Exception e){
            Log.e(TAG, "downloadMeasurementPointList: " + e.getMessage() );
        }
        try {
            // creating a call and calling  getMeasurementPoints()
            Call<ArrayList<MeasurementPointModel>> call = clientRestWebservice.getMeasurementPoints();
            // finally performing the call
            call.enqueue(new Callback<ArrayList<MeasurementPointModel>>() {
                @Override
                public void onResponse(Call<ArrayList<MeasurementPointModel>> call, Response<ArrayList<MeasurementPointModel>> response) {
                    if (response.isSuccessful()){
                        downloadedMeasurementPointList = response.body();
                        Gson gson = new Gson();
                        String content = gson.toJson(downloadedMeasurementPointList);
                        Log.d(TAG, "onResponse downloadMeasurementPointList: " + content);
                        downloadCallback.onFinish();
                    }else{
                        Log.e(TAG, "onResponse downloadMeasurementPointList: Code " + response.code() );
                        downloadCallback.onError();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<MeasurementPointModel>> call, Throwable t) {
                    Log.e(TAG, "onFailure downloadMeasurementPointList: " + t.getMessage() + " " + t.getStackTrace() );
                    downloadCallback.onError();
                }
            });
        } catch (Exception e){
            Log.e(TAG, "downloadMeasurementPointList: Error on Download " + e.getMessage() );
            downloadCallback.onError();
        }
    }

    public void downloadSingleMeasurementPoint(final DownloadProgressResponseBody.DownloadCallbacks downloadCallback){

        //Set up Progressbar
        try{
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
        }catch (Exception e){
            Log.e(TAG, "downloadSingleMeasurementPoint: " + e.getMessage() );
        }
        try {
            // creating a call and calling  getSingleMeasurementPoint()
            Call<ArrayList<MeasurementPointModel>> call = clientRestWebservice.getSingleMeasurementPoint();
            // finally performing the call
            call.enqueue(new Callback<ArrayList<MeasurementPointModel>>() {
                @Override
                public void onResponse(Call<ArrayList<MeasurementPointModel>> call, Response<ArrayList<MeasurementPointModel>> response) {
                    if (response.isSuccessful()){
                        downloadedMeasurementPoint = response.body();
                        Gson gson = new Gson();
                        String content = gson.toJson(downloadedMeasurementPoint);
                        Log.d(TAG, "onResponse downloadSingleMeasurementPoint: " + content);
                        downloadCallback.onFinish();
                    }else{
                        Log.e(TAG, "onResponse downloadSingleMeasurementPoint: Code " + response.code() );
                        downloadCallback.onError();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<MeasurementPointModel>> call, Throwable t) {
                    Log.e(TAG, "onFailure downloadSingleMeasurementPoint: " + t.getMessage() + " " + t.getStackTrace() );
                    downloadCallback.onError();
                }
            });
        } catch (Exception e){
            Log.e(TAG, "downloadSingleMeasurementPoint: " + e.getMessage() );
            downloadCallback.onError();
        }
    }

    public void downloadMeasurementPointListInRange(Date startDate, Date endDate, final DownloadProgressResponseBody.DownloadCallbacks downloadCallback){
        //Set up Progressbar
        try{
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
        }catch (Exception e){
            Log.e(TAG, "downloadMeasurementPointListInRange: "+ e.getMessage() );
        }
        try {
            String startDateAsString = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
            String endDateAsString = new SimpleDateFormat("yyyy-MM-dd").format(endDate);

            // creating a call and calling  getSingleMeasurementPoint()
            Call<ArrayList<MeasurementPointModel>> call = clientRestWebservice.getMeasurementPointsInDateRange(startDateAsString, endDateAsString);
            // finally performing the call
            call.enqueue(new Callback<ArrayList<MeasurementPointModel>>() {
                @Override
                public void onResponse(Call<ArrayList<MeasurementPointModel>> call, Response<ArrayList<MeasurementPointModel>> response) {
                    if (response.isSuccessful()){
                        downloadedMeasurementPoint = response.body();
                        Gson gson = new Gson();
                        String content = gson.toJson(downloadedMeasurementPoint);
                        Log.d(TAG, "onResponse downloadMeasurementPointListInRange: " + content);
                        downloadCallback.onFinish();
                    }else{
                        Log.e(TAG, "onResponse downloadMeasurementPointListInRange: Code " + response.code() );
                        downloadCallback.onError();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<MeasurementPointModel>> call, Throwable t) {
                    Log.e(TAG, "onFailure downloadMeasurementPointListInRange: " + t.getMessage() + " " + t.getStackTrace() );
                    downloadCallback.onError();
                }
            });
        } catch (Exception e){
            Log.e(TAG, "downloadMeasurementPointListInRange: " + e.getMessage() );
            downloadCallback.onError();
        }
    }

    // Test Method
    String testResult;

    public String getTestResult() {
        return testResult;
    }

    public void testWebserver(Date startDate, Date endDate, final DownloadProgressResponseBody.DownloadCallbacks downloadCallback){
        //Set up Progressbar
        try{
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
        }catch (Exception e){
            Log.e(TAG, "testWebserver: " + e.getMessage() );
        }
        try {
            //String startDateAsString = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
            //String endDateAsString = new SimpleDateFormat("yyyy-MM-dd").format(endDate);

            DateRangeTask dateRangeTask = new DateRangeTask(startDate, endDate);

            // creating a call and calling  getSingleMeasurementPoint()
            Call<String> call = clientRestWebservice.getWebserverTest(dateRangeTask);
            // finally performing the call
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()){
                        testResult = response.body();
                        Gson gson = new Gson();
                        String content = gson.toJson(downloadedMeasurementPoint);
                        Log.d(TAG, "onResponse testWebserver: " + content);
                        downloadCallback.onFinish();
                    }else{
                        Log.e(TAG, "onResponse testWebserver: Code " + response.code() );
                        downloadCallback.onError();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, "onFailure testWebserver: " + t.getMessage() + " " + t.getStackTrace() );
                    downloadCallback.onError();
                }
            });
        } catch (Exception e){
            Log.e(TAG, "testWebserver: " + e.getMessage() );
            downloadCallback.onError();
        }
    }



}
