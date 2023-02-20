package com.example.shuttlemobile.review;

public class ReviewPairDTO {
    private ReviewDTO vehicleReview;
    private ReviewDTO driverReview;

    public ReviewPairDTO() {
    }

    public ReviewDTO getVehicleReview() {
        return vehicleReview;
    }

    public void setVehicleReview(ReviewDTO vehicleReview) {
        this.vehicleReview = vehicleReview;
    }

    public ReviewDTO getDriverReview() {
        return driverReview;
    }

    public void setDriverReview(ReviewDTO driverReview) {
        this.driverReview = driverReview;
    }

    @Override
    public String toString() {
        return "ReviewPairDTO{" +
                "vehicleReview=" + vehicleReview +
                ", driverReview=" + driverReview +
                '}';
    }
}
