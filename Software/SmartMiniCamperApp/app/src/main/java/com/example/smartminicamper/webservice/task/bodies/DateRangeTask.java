package com.example.smartminicamper.webservice.task.bodies;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateRangeTask {
    private String dateBegin;
    private String dateEnd;

    public DateRangeTask(Date startDate, Date endDate) {
        this.dateBegin = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
        this.dateEnd = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
    }
}
