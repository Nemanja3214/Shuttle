package com.example.shuttlemobile.review;

import com.example.shuttlemobile.user.dto.UserEmailDTO;

import java.io.Serializable;

public class ReviewDTO implements Serializable {
    private Long id;
    private Long rating;
    private String comment;
    private UserEmailDTO passenger;

    public ReviewDTO(Long id, Long rating, String comment, UserEmailDTO passenger) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.passenger = passenger;
    }

    public ReviewDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UserEmailDTO getPassenger() {
        return passenger;
    }

    public void setPassenger(UserEmailDTO passenger) {
        this.passenger = passenger;
    }

    @Override
    public String toString() {
        return "RatingDTO{" +
                "id=" + id +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", passenger=" + passenger +
                '}';
    }
}
