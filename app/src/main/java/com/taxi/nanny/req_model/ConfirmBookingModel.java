package com.taxi.nanny.req_model;

import java.util.List;

public class ConfirmBookingModel {


    private List<RideDetailBean> ride_detail;

    public List<RideDetailBean> getRide_detail() {
        return ride_detail;
    }

    public void setRide_detail(List<RideDetailBean> ride_detail) {
        this.ride_detail = ride_detail;
    }

    public static class RideDetailBean {
        /**
         * rider_id : 12
         * rider_pick_location_id : 279
         * rider_drop_location_id : 280
         * priority_pick : 1
         * priority_drop : 2
         */

        private String rider_id;
        private String rider_pick_location_id;
        private String rider_drop_location_id;
        private String priority_pick;
        private String priority_drop;

        public String getRider_id() {
            return rider_id;
        }

        public void setRider_id(String rider_id) {
            this.rider_id = rider_id;
        }

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

        public String getPriority_pick() {
            return priority_pick;
        }

        public void setPriority_pick(String priority_pick) {
            this.priority_pick = priority_pick;
        }

        public String getPriority_drop() {
            return priority_drop;
        }

        public void setPriority_drop(String priority_drop) {
            this.priority_drop = priority_drop;
        }
    }
}
