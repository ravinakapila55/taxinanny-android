package com.taxi.nanny.req_model;

import java.util.List;

public class EstimateFTModel
{
    private List<RideDetailBean> ride_detail;

    public List<RideDetailBean> getRide_detail() {
        return ride_detail;
    }

    public void setRide_detail(List<RideDetailBean> ride_detail) {
        this.ride_detail = ride_detail;
    }

    public static class RideDetailBean
    {
        /**
         * rider_pick_location_id : 159
         * rider_drop_location_id : 160
         */

        private String rider_pick_location_id;
        private String rider_drop_location_id;

        public String getRider_pick_location_id() {
            return rider_pick_location_id;
        }

        public void setRider_pick_location_id(String rider_pick_location_id) {
            this.rider_pick_location_id = rider_pick_location_id;
        }

        public String getRider_drop_location_id() {
            return rider_drop_location_id;
        }

        public void setRider_drop_location_id(String rider_drop_location_id) {
            this.rider_drop_location_id = rider_drop_location_id;
        }
    }
}
