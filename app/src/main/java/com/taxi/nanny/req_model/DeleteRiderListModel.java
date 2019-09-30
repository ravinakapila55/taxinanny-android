package com.taxi.nanny.req_model;

import java.util.List;

public class DeleteRiderListModel {


    private List<RideDetailBean> ride_detail;

    public List<RideDetailBean> getRide_detail() {
        return ride_detail;
    }

    public void setRide_detail(List<RideDetailBean> ride_detail)
    {
        this.ride_detail = ride_detail;
    }

    public static class RideDetailBean {
        /**
         * rider_id : 159
         */

        private String rider_id;

        public String getRider_id() {
            return rider_id;
        }

        public void setRider_id(String rider_id) {
            this.rider_id = rider_id;
        }
    }
}
