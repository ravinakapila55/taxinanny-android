package com.taxi.nanny.req_model;

import java.util.List;

public class RecurringDays
{

    public List<RideRecurringDays> getRideRecurringDaysList()
    {
        return RideRecurringDaysList;
    }

    public void setRideRecurringDaysList(List<RideRecurringDays> rideRecurringDaysList)
    {
        RideRecurringDaysList = rideRecurringDaysList;
    }

    private List<RideRecurringDays> RideRecurringDaysList;

    public static class RideRecurringDays
    {
        private String day;
        public String getDay()
        {
            return day;
        }
        public void setDay(String day)
        {
            this.day = day;
        }
    }

}
