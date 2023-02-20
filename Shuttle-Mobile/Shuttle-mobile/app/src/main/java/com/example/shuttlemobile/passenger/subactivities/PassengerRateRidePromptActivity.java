package com.example.shuttlemobile.passenger.subactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.review.IReviewService;
import com.example.shuttlemobile.ride.IRideService;
import com.example.shuttlemobile.review.ReviewDTO;
import com.example.shuttlemobile.review.ReviewSendDTO;
import com.example.shuttlemobile.ride.dto.RideDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRateRidePromptActivity extends AppCompatActivity {
    private Button btnClose;
    private Button btnSend;
    private EditText txtCommentDriver;
    private EditText txtCommentVehicle;
    private RatingBar ratingDriver;
    private RatingBar ratingVehicle;

    public static final String KEY_RIDE = "ride";
    private RideDTO ride;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_rate_ride_prompt);
        initViewElements();

        Bundle b = getIntent().getExtras();
        ride = (RideDTO)b.getSerializable(KEY_RIDE);
    }

    private void initViewElements() {
        btnClose = findViewById(R.id.btn_p_rate_prompt_close);
        btnSend = findViewById(R.id.btn_p_rate_prompt_send);
        txtCommentDriver = findViewById(R.id.txt_p_rate_prompt_driver);
        txtCommentVehicle = findViewById(R.id.txt_p_rate_prompt_vehicle);
        ratingDriver = findViewById(R.id.rating_p_rate_prompt_driver);
        ratingVehicle = findViewById(R.id.rating_p_rate_prompt_vehicle);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReview();
            }
        });
    }

    private void sendReview() {
        sendReviewVehicle(); // Calls sendReviewDriver() on success.
    }
    
    private void sendReviewVehicle() {
        final ReviewSendDTO reviewVehicle = new ReviewSendDTO(
                (long)ratingVehicle.getRating(),
                txtCommentVehicle.getText().toString()
        );
        IReviewService.service.leaveReviewVehicle(ride.getId(), reviewVehicle).enqueue(new Callback<ReviewDTO>() {
            @Override
            public void onResponse(Call<ReviewDTO> call, Response<ReviewDTO> response) {
                sendReviewDriver();
            }

            @Override
            public void onFailure(Call<ReviewDTO> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Could not leave vehicle review!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void sendReviewDriver() {
        final ReviewSendDTO reviewDriver = new ReviewSendDTO(
                (long)ratingDriver.getRating(),
                txtCommentDriver.getText().toString()
        );
        IReviewService.service.leaveReviewDriver(ride.getId(), reviewDriver).enqueue(new Callback<ReviewDTO>() {
            @Override
            public void onResponse(Call<ReviewDTO> call, Response<ReviewDTO> response) {
                PassengerRateRidePromptActivity.this.finish();
            }

            @Override
            public void onFailure(Call<ReviewDTO> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Could not leave driver review!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}