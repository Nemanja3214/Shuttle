package com.example.shuttlemobile.passenger.subactivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.SessionContext;
import com.example.shuttlemobile.common.SimpleToolbarActivity;
import com.example.shuttlemobile.common.adapter.EasyListAdapter;
import com.example.shuttlemobile.driver.subactivities.DriverHistoryDetails;
import com.example.shuttlemobile.passenger.Passenger;
import com.example.shuttlemobile.passenger.fragments.PassengerHistory;
import com.example.shuttlemobile.passenger.fragments.PassengerHistoryDetails;
import com.example.shuttlemobile.ride.Ride;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.user.User;

import java.util.ArrayList;
import java.util.List;

public class PassengerHistoryDetailsActivity extends SimpleToolbarActivity {
    protected RideDTO ride;

    public static final String PARAM_RIDE = "ride";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_history_details);

        initParams();

        PassengerHistoryDetails frag = PassengerHistoryDetails.newInstance(ride);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom)
                .setReorderingAllowed(true)
                .add(R.id.p_history_fragment_view, frag);
        fragmentTransaction.commit();
        initView();
    }


    private void initParams() {
        Intent intent = getIntent();
        ride = (RideDTO)intent.getSerializableExtra(PARAM_RIDE);

        if (ride == null) {
            throw new NullPointerException("Missing intent parameter " + PARAM_RIDE);
        }
    }

    private void initView() {
    }
}