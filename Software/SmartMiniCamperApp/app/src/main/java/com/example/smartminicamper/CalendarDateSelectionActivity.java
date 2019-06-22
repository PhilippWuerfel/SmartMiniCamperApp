package com.example.smartminicamper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.timessquare.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CalendarDateSelectionActivity extends AppCompatActivity {
    CalendarPickerView datePicker;
    Button okButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_date_selection);


        // ASIGN ELEMENTS
        okButton = (Button) findViewById(R.id.ok_button);

        // CUSTOMIZED WIDGETS
        Date today = new Date();
        java.util.Calendar lastMonth = java.util.Calendar.getInstance();
        lastMonth.add(java.util.Calendar.DAY_OF_MONTH,1);
        Date todayPlusOne = lastMonth.getTime();
        lastMonth.add(java.util.Calendar.MONTH,-1);
        Date oneMonthAgo = lastMonth.getTime();
        //CalendarPickerView datePicker = findViewById(R.id.calendar);
        datePicker = findViewById(R.id.calendar);
        datePicker.init(lastMonth.getTime(), todayPlusOne).inMode(CalendarPickerView.SelectionMode.RANGE).withSelectedDate(today);

        //end test

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Date> selectedDates = datePicker.getSelectedDates();
                String startDateString = selectedDates.get(0).toString();
                int size = selectedDates.size();
                String endDateString = selectedDates.get(selectedDates.size()-1).toString();


                Date startDate = selectedDates.get(0);
                Date endDate = selectedDates.get(selectedDates.size()-1);

                startDateString = startDateString.substring(0,10) + startDateString.substring(startDateString.length()-5,startDateString.length());
                endDateString = endDateString.substring(0,10) + endDateString.substring(endDateString.length()-5,endDateString.length());

                Intent resultIntent = new Intent();
                resultIntent.putExtra("startDate", startDateString);
                resultIntent.putExtra("endDate", endDateString);

                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}

