package com.example.smartminicamper;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

enum DownloadStatus { IDLE, PROCESSING, NOT_INITIALISED, FAILED_OR_EMPTY, OK}

public class GetDataFromRestWebservice extends AsyncTask<String, Void, JSONArray> {

    // Downloading JSON-Data from REST Webservice with BuffedReader and AsyncTask
    private static final String TAG = "GetDataFromRestWebserv";

    private DownloadStatus downloadStatus;
    private final OnDownloadComplete callback;

    interface OnDownloadComplete {
        void onDownloadComplete(JSONArray data, DownloadStatus status);
    }


    public GetDataFromRestWebservice(OnDownloadComplete callback) {
        this.downloadStatus = DownloadStatus.IDLE;
        this.callback = callback;
    }

/*
    void runInSameThread(String s){
        // the doInBackground function of the Async-caller will be used (--> runInSameThread)
        Log.d(TAG, "runInSameThread starts");
//        onPostExecute(doInBackground(s));
        if (callback != null){
            String result = doInBackground(s);
            callback.onDownloadComplete(result, downloadStatus);
        }
        Log.d(TAG, "runInSameThread ends");
    }
*/

    @Override
    protected JSONArray doInBackground(String... strings) {
        // invoked on the background thread immediately after onPreExecute() finishes executing

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        if (strings == null){
            this.downloadStatus = DownloadStatus.NOT_INITIALISED;
            return null;
        }

        try{
            downloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(strings[0]);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int response = connection.getResponseCode();
            Log.d(TAG, "doInBackground: The response code was " + response);

            StringBuilder result = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            // while reader.readLine is not returning null
            while(null != (line = reader.readLine())) {
                result.append(line).append("\n");
            }

            downloadStatus = DownloadStatus.OK;

            // parsing JSON
            JSONArray jsonData = new JSONArray(result.toString());

            return  jsonData;
            //return result.toString();

        }catch (MalformedURLException e){
            Log.e(TAG, "doInBackground: Invalid URL " + e.getMessage() );
        }catch (IOException e){
            Log.e(TAG, "doInBackground: IO Exception reading data: " + e.getMessage() );
        }catch (SecurityException e) {
            Log.e(TAG, "doInBackground: Security Exception. Needs permission?" + e.getMessage() );
        }catch (JSONException jsone){
            jsone.printStackTrace();
            Log.e(TAG, "onDownloadComplete: Error processing Json data " + jsone.getMessage());
            downloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        }finally {
            if(connection != null){
                connection.disconnect();
            }
            if(reader != null){
                try{
                    reader.close();
                }catch (IOException e){
                    Log.e(TAG, "doInBackground: Error closing stream " + e.getMessage() );
                }
            }
        }
        downloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        // invoked on the UI thread after the background computation finishes.
        // The result of the background computation is passed to this step as a parameter

        Log.d(TAG, "onPostExecute: parameter = " + jsonArray);
        if (callback != null){
            callback.onDownloadComplete(jsonArray, downloadStatus);
        }
    }
}