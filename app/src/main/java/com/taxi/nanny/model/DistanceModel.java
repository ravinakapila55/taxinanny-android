package com.taxi.nanny.model;

import java.util.List;

public class DistanceModel
{
    private List<RideDistanceBean> ride_detail;

    public List<RideDistanceBean> getRide_detail()
    {
        return ride_detail;
    }

    public void setRide_detail(List<RideDistanceBean> ride_detail)
    {
        this.ride_detail = ride_detail;
    }

    public static class RideDistanceBean
    {
        private String rider_distance;

        public String getRider_distance()
        {
            return rider_distance;
        }

        public void setRider_distance(String rider_distance)
        {
            this.rider_distance = rider_distance;
        }

    }



}
