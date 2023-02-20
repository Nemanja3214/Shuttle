package com.example.shuttlemobile.review;

import java.io.Serializable;

public class ReviewSendDTO implements Serializable {
    private Long rating;
    private String comment;

    public ReviewSendDTO(Long rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public ReviewSendDTO() {
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "RatingSendDTO{" +
                "rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }
}
