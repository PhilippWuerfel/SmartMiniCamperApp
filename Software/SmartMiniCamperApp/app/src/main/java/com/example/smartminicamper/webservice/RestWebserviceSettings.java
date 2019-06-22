package com.example.smartminicamper.webservice;

public class RestWebserviceSettings {
    // all necessary settings will be listed here

    static String BASE_URL = ""; //"http://89.14.107.115:5000";

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }
}
