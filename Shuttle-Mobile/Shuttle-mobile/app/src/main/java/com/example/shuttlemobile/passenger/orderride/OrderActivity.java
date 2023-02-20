package com.example.shuttlemobile.passenger.orderride;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.SessionContext;
import com.example.shuttlemobile.passenger.orderride.fragments.ConfirmationFragment;
import com.example.shuttlemobile.passenger.orderride.fragments.InviteFragment;
import com.example.shuttlemobile.passenger.orderride.fragments.RidePropertiesFragment;
import com.example.shuttlemobile.passenger.orderride.fragments.ScheduleRide;
import com.example.shuttlemobile.ride.dto.CreateRideDTO;
import com.example.shuttlemobile.route.RouteDTO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {
    public static final String KEY_ROUTE = "route";
    public static final String KEY_DIST = "dist";

    private int step = 0;
    private int MAX_STEP = 3;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private final String STACK_FRAGMENTS = "UserActivityFragment";
    private Map<Integer, Fragment> fragments = new HashMap<>();
    private Fragment currentFragment;
    private SessionContext session;
    private RouteDTO route;
    private Double distance;
    private SeekBar seekbarProgress;

    public RouteDTO getRoute() {
        return route;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.route = (RouteDTO) getIntent().getSerializableExtra(KEY_ROUTE);
        this.distance = getIntent().getDoubleExtra(KEY_DIST, 0.0);

        //Log.e("?", this.distance.toString());

        setContentView(R.layout.activity_order);
        initViewElements(getWindow().getDecorView().getRootView());

        initializeFragmentMap();
        changeStep();
    }


    private void previousStep() {
        step--;
        if (step < -1) step = -1;
        getSupportFragmentManager().popBackStack(STACK_FRAGMENTS, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        changeStep();
    }

    private void nextStep() {
        step++;
        if (step > 3) step = MAX_STEP;
        changeStep();
    }

    private void changeStep() {
        Fragment f = fragments.get(this.step);
        if (f != null) {
            setVisibleFragment(f);
        }

        seekbarProgress.setProgress(step);
        seekbarProgress.setMax(fragments.size() - 1);
        seekbarProgress.setProgress(step);

        btnPrevious.setVisibility(step == 0 ? View.INVISIBLE : View.VISIBLE);
        btnNext.setVisibility(step == fragments.size() - 1 ? View.INVISIBLE : View.VISIBLE);
    }

    private int getFragmentFrameId() {
        return R.id.order_fragment_view;
    }

    private void initializeFragmentMap() {
        fragments.put(0, RidePropertiesFragment.newInstance(session));
        fragments.put(1, InviteFragment.newInstance(session));
        fragments.put(2, ScheduleRide.newInstance(session));
        fragments.put(3, ConfirmationFragment.newInstance(session));
    }

    private Fragment getDefaultFragment() {
        return fragments.get(0);
    }

    private void initViewElements(View view) {
        btnNext = view.findViewById(R.id.next_button);
        btnPrevious = view.findViewById(R.id.previous_button);
        seekbarProgress = view.findViewById(R.id.seekbar_progress);

        btnNext.setOnClickListener(view1 -> {
            nextStep();
        });

        btnPrevious.setOnClickListener(view12 -> {
            previousStep();
        });

        seekbarProgress.setOnTouchListener((View.OnTouchListener) (v, event) -> true);
    }

    protected final void setVisibleFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .replace(getFragmentFrameId(), fragment);
        fragmentTransaction.addToBackStack(STACK_FRAGMENTS);
        fragmentTransaction.commit();
        currentFragment = fragment;
    }

    @Override
    public void onBackPressed() {
        previousStep();
        if(step == -1){
            getSupportFragmentManager().popBackStack();
            super.onBackPressed();
        }

    }

    public CreateRideDTO getCreateRideDTO() {
        RidePropertiesFragment properties = (RidePropertiesFragment) fragments.get(0);
        InviteFragment invite = (InviteFragment) fragments.get(1);
        ScheduleRide schedule = (ScheduleRide) fragments.get(2);
        return generateDTO(properties, schedule, invite);
    }

    private CreateRideDTO generateDTO(RidePropertiesFragment properties, ScheduleRide schedule, InviteFragment invite) {
        CreateRideDTO dto = new CreateRideDTO();

        dto.setLocations(Arrays.asList(this.route));
        dto.setBabyTransport(properties.isBabyChecked());
        dto.setPetTransport(properties.isPetChecked());
        dto.setVehicleType(properties.getVehicleType());
        dto.setScheduledTime(schedule.getFutureTime());
        dto.setPassengers(invite.getPassengers());
        dto.setDistance(distance);

        return dto;
    }

}