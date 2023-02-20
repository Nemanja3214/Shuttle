package com.example.shuttlemobile.driver.subactivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.SessionContext;
import com.example.shuttlemobile.common.SimpleToolbarActivity;
import com.example.shuttlemobile.common.adapter.EasyListAdapter;
import com.example.shuttlemobile.passenger.Passenger;
import com.example.shuttlemobile.passenger.subactivities.PassengerHistoryDetailsActivity;
import com.example.shuttlemobile.ride.Ride;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.user.User;

import java.util.ArrayList;
import java.util.List;

public class DriverHistoryDetailsActivity extends SimpleToolbarActivity {
    protected SessionContext session;
    protected RideDTO ride;

    public RideDTO getRide() {
        return ride;
    }

    public static final String PARAM_RIDE = "ride";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_history_details);

        initParams();

        Log.e("2", "2");

        DriverHistoryDetails frag = DriverHistoryDetails.newInstance(ride);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom)
                .setReorderingAllowed(true)
                .replace(R.id.driver_history_fragment_view, frag);
        fragmentTransaction.commit();

        initView();
    }

    private void initParams() {
        Intent intent = getIntent();
        ride = (RideDTO)intent.getSerializableExtra(PARAM_RIDE);
        Log.e("?", ride.toString());
    }

    private void initView() {
        //ListView ridePassengers = findViewById(R.id.li_d_ride_passengers);
//
//        List<User> passengers = new ArrayList<>();
//        passengers.add(new Passenger());
//        passengers.get(0).setName("Bob");
//        passengers.get(0).setLastName("Jones");
//
//        ridePassengers.setAdapter(new EasyListAdapter<User>() {
//            @Override
//            public List<User> getList() {
//                return passengers;
//            }
//
//            @Override
//            public LayoutInflater getLayoutInflater() {
//                return DriverHistoryDetailsActivity.this.getLayoutInflater();
//            }
//
//            @Override
//            public void applyToView(View view, User obj) {
//                TextView txtName = view.findViewById(R.id.txt_p_history_p_name);
//                txtName.setText(obj.getName() + " " + obj.getLastName());
//            }
//
//            @Override
//            public int getListItemLayoutId() {
//                return R.layout.list_p_history_passengers;
//            }
//        });
    }
}