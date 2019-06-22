package com.example.smartminicamper.webservice;

import com.example.smartminicamper.webservice.restservice.model.MeasurementPointModel;
import com.example.smartminicamper.webservice.task.bodies.DateRangeTask;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ClientRestWebservice {

    @GET("/smartminicamper/db")
    Call<ArrayList<MeasurementPointModel>> getMeasurementPoints();

    @GET("/smartminicamper/db_aktuelle_daten")
    Call<ArrayList<MeasurementPointModel>> getSingleMeasurementPoint(); // currently on failure because Webservice returns as Array

    /*
    @GET("/smartminicamper/db_aktuelle_daten")
    Call<MeasurementPointModel> getSingleMeasurementPoint(); // currently on failure because Webservice returns as Array
    */

    // smartminicamper/db_zeitraum
    @GET("/smartminicamper/db_zeitraum")
    Call<ArrayList<MeasurementPointModel>> getMeasurementPointsInDateRange(@Header("StartDate") String startDate, @Header("EndDate") String endDate);

    // smartminicamper/db_zeitraum
    //@GET("/test")
    //@HTTP(method = "GET", path = "/test", hasBody = true)
    @POST("/test")
    Call<String> getWebserverTest(@Body DateRangeTask dateRangeTask);
    //Call<String> getWebserverTest(@Header("StartDate") String startDate, @Header("EndDate") String endDate);



}
