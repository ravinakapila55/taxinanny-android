package com.taxi.nanny.req_model;

import java.util.List;

public class ConfirmBookingMain

{

    /**
     * amount : 806.12
     * lattitude : 30.7046
     * booking_end_date : 2019-07-31
     * parent_id : 73
     * booking_type : 1
     * currency : USD
     * RideRecurringDaysList : [{"day":"Tuesday"},{"day":"Thursday"}]
     * ride_detail : [{"priority_drop":"1","priority_pick":"1","rider_drop_location_id":"113","rider_id":"385","rider_pick_location_id":"108"}]
     * token : tok_1ExWoTC4hWTqQmtYOOY2JAAu
     * longitude : 76.7179
     */

    private String amount;
    private String lattitude;
    private String booking_end_date;
    private String parent_id;
    private String booking_type;
    private String sign_in;
    private String sign_up;

    public String getSign_up()
    {
        return sign_up;
    }

    public void setSign_up(String sign_up) {
        this.sign_up = sign_up;
    }

    private String currency;

    public String getTime_of_ride() {
        return time_of_ride;
    }

    public void setTime_of_ride(String time_of_ride) {
        this.time_of_ride = time_of_ride;
    }

    private String time_of_ride;
    private String booking_date;

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public String getSign_in() {
        return sign_in;
    }

    public void setSign_in(String sign_in) {
        this.sign_in = sign_in;
    }


    private String token;
    private String card_id;

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    private String longitude;
    private List<RideRecurringDaysListBean> RideRecurringDaysList;
    private List<RideDetailBean> ride_detail;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getBooking_end_date() {
        return booking_end_date;
    }

    public void setBooking_end_date(String booking_end_date) {
        this.booking_end_date = booking_end_date;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getBooking_type() {
        return booking_type;
    }

    public void setBooking_type(String booking_type) {
        this.booking_type = booking_type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public List<RideRecurringDaysListBean> getRideRecurringDaysList() {
        return RideRecurringDaysList;
    }

    public void setRideRecurringDaysList(List<RideRecurringDaysListBean> RideRecurringDaysList) {
        this.RideRecurringDaysList = RideRecurringDaysList;
    }

    public List<RideDetailBean> getRide_detail() {
        return ride_detail;
    }

    public void setRide_detail(List<RideDetailBean> ride_detail) {
        this.ride_detail = ride_detail;
    }

    public static class RideRecurringDaysListBean {
        /**
         * day : Tuesday
         */

        private String day;

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }
    }

    public static class RideDetailBean {
        /**
         * priority_drop : 1
         * priority_pick : 1
         * rider_drop_location_id : 113
         * rider_id : 385
         * rider_pick_location_id : 108
         */

        private String priority_drop;
        private String priority_pick;
        private String rider_drop_location_id;
        private String rider_id;
        private String rider_pick_location_id;
        private String pick_up_instruction;
        private String drop_off_instruction;
        private String kids_sign_in;
        private String kids_sign_up;

        public String getPick_up_instruction() {
            return pick_up_instruction;
        }

        public void setPick_up_instruction(String pick_up_instruction) {
            this.pick_up_instruction = pick_up_instruction;
        }

        public String getDrop_off_instruction() {
            return drop_off_instruction;
        }

        public void setDrop_off_instruction(String drop_off_instruction) {
            this.drop_off_instruction = drop_off_instruction;
        }

        public String getKids_sign_in() {
            return kids_sign_in;
        }

        public void setKids_sign_in(String kids_sign_in) {
            this.kids_sign_in = kids_sign_in;
        }

        public String getKids_sign_up() {
            return kids_sign_up;
        }

        public void setKids_sign_up(String kids_sign_up) {
            this.kids_sign_up = kids_sign_up;
        }

        public String getPriority_drop() {
            return priority_drop;
        }

        public void setPriority_drop(String priority_drop) {
            this.priority_drop = priority_drop;
        }

        public String getPriority_pick() {
            return priority_pick;
        }

        public void setPriority_pick(String priority_pick) {
            this.priority_pick = priority_pick;
        }

        public String getRider_drop_location_id() {
            return rider_drop_location_id;
        }

        public void setRider_drop_location_id(String rider_drop_location_id) {
            this.rider_drop_location_id = rider_drop_location_id;
        }

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
    }
}
