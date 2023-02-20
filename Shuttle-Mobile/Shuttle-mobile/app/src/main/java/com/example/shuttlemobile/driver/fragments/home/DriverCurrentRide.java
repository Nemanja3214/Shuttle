package com.example.shuttlemobile.driver.fragments.home;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.driver.fragments.DriverHome;
import com.example.shuttlemobile.driver.fragments.PassengerData;
import com.example.shuttlemobile.driver.services.CurrentRideTimeService;
import com.example.shuttlemobile.driver.services.DriverRideService;
import com.example.shuttlemobile.ride.IRideService;
import com.example.shuttlemobile.ride.dto.PanicDTO;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.route.LocationDTO;
import com.example.shuttlemobile.route.RouteDTO;
import com.mapbox.geojson.Point;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverCurrentRide extends Fragment {
    private Button btnPassengers;
    private TextView tvDeparture;
    private TextView tvDestination;
    private TextView tvTime;
    private TextView tvPrice;
    private TextView tvPassengerCount;
    private Button btnFinish;
    private Button btnPanic;

    private RideDTO currentRide = null;

    private Point A = null;
    private Point B = null;

    private DriverHome parent = null;
    private BroadcastReceiver rideReceiver;
    private BroadcastReceiver timeReceiver;

    public static DriverCurrentRide newInstance() {
        DriverCurrentRide fragment = new DriverCurrentRide();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initParent();
        initTimeReceiver();
        initRideReceiver();
        initViewElements(view);

        this.currentRide = null;
    }


    @Override
    public void onStop() {
        super.onStop();
        unregisterReceivers();
    }

    @Override
    public void onStart() {
        super.onStart();

        registerReceivers();
    }

    private void initParent() {
        parent = (DriverHome)DriverCurrentRide.this.getParentFragment();
        if (parent == null) {
            throw new IllegalStateException("Parent is null (should be DriverHome)");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_current_ride, container, false);
    }

    private void initRideReceiver() {
        rideReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RideDTO rideDTO = (RideDTO)intent.getSerializableExtra(DriverRideService.INTENT_RIDE_KEY);
                onGetRide(rideDTO);
            }
        };
    }

    private void registerReceivers(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DriverRideService.BROADCAST_CHANNEL);
        getActivity().registerReceiver(rideReceiver, intentFilter);

        intentFilter = new IntentFilter();
        intentFilter.addAction(CurrentRideTimeService.RESULT);
        getActivity().registerReceiver(timeReceiver, intentFilter);
    }

    private void unregisterReceivers(){
        getActivity().unregisterReceiver(rideReceiver);
        getActivity().unregisterReceiver(timeReceiver);
    }


    private void initTimeReceiver() {
        timeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(CurrentRideTimeService.NEW_TIME_MESSAGE);
                tvTime.setText(s);
            }
        };
    }

    private void onGetRide(RideDTO dto) {
        if (dto == null) {
            currentRide = null;
            A = null;
            B = null;
            return;
        }

        // If the currently cached ride is different from this one, redraw the route and focus on it.

        boolean isNewRide = false;
        if (currentRide == null || !currentRide.getId().equals(dto.getId())) {
            isNewRide = true;
        }

        // Update ride.

        currentRide = dto;
        final LocationDTO A_loc = currentRide.getLocations().get(0).getDeparture();
        final LocationDTO B_loc = currentRide.getLocations().get(currentRide.getLocations().size() - 1).getDestination();

        A = Point.fromLngLat(A_loc.getLongitude(), A_loc.getLatitude());
        B = Point.fromLngLat(B_loc.getLongitude(), B_loc.getLatitude());

        if (isNewRide) {
            startTimer();
            fillData();

            // Draw route.

            parent.drawRoute(A, B, "#FF0000");
            parent.fitViewport(A, B, 3000);
        }
    }

    private void initViewElements(View view) {

        btnPassengers = view.findViewById(R.id.btn_current_ride_passengers);
        tvDeparture = view.findViewById(R.id.txt_current_ride_departure);
        tvDestination = view.findViewById(R.id.txt_current_ride_destination);
        tvTime = view.findViewById(R.id.txt_current_ride_duration);
        tvPrice = view.findViewById(R.id.txt_current_ride_price);
        tvPassengerCount = view.findViewById(R.id.txt_current_ride_passengers);
        btnFinish = view.findViewById(R.id.btn_d_finish);
        btnPanic = view.findViewById(R.id.btn_d_panic);
    }

    private void showPassengerData(int position) {
        Bundle bundle = new Bundle();
        long passengerId =  currentRide.getPassengers().get(position).getId();
        bundle.putLong(PassengerData.PASSENGER_ID, passengerId);
        bundle.putSerializable(PassengerData.RIDE, currentRide);

        DialogFragment dialog = new PassengerData();
        dialog.setArguments(bundle);
        dialog.show(getChildFragmentManager(), "Passenger data");
    }

    private void fillData() {
        fillTextViews();

        fillLocations();
        btnFinish.setOnClickListener(view -> finishRide());

        initPanicButton();
        initPreviewButton();
    }

    private void initPreviewButton() {
        btnPassengers.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View v = inflater.inflate(R.layout.alert_ride_passenger_list, null);

            builder.setView(v)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setItems(
                            currentRide.getPassengers()
                                    .stream()
                                    .map(o -> o.getEmail())
                                    .collect(Collectors.toList())
                                    .toArray(new String[0]), (dialogInterface, i) -> showPassengerData(i)
                    );
            builder.show();
        });
    }

    @SuppressLint("DefaultLocale")
    private void fillTextViews() {
        tvPassengerCount.setText(Integer.toString(currentRide.getPassengers().size()));
        tvPrice.setText(String.format("%.2f", currentRide.getTotalCost()));
    }

    private void fillLocations() {
        List<RouteDTO> locations = currentRide.getLocations();
        tvDeparture.setText(locations.get(0).getDeparture().getAddress());
        tvDestination.setText(locations.get(locations.size() - 1).getDestination().getAddress());
    }

    private void finishRide(){
        Call<RideDTO> call = IRideService.service.endRide(currentRide.getId());
        call.enqueue(new Callback<RideDTO>() {
            @Override
            public void onResponse(Call<RideDTO> call, Response<RideDTO> response) {
            }

            @Override
            public void onFailure(Call<RideDTO> call, Throwable t) {
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
    }

    private void stopTimer() {
        Intent myService = new Intent(requireContext(), CurrentRideTimeService.class);
        requireContext().stopService(myService);
    }

    private void startTimer() {
        if (currentRide != null && currentRide.getStartTime() != null) {
            Intent intent = new Intent(getActivity(), CurrentRideTimeService.class);
            intent.putExtra(CurrentRideTimeService.TIME_START, currentRide.getStartTime());
            requireActivity().startService(intent);
        }
    }

    private void initPanicButton() {
        btnPanic.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View v = inflater.inflate(R.layout.alert_reject_reason, null);
            EditText txtReason = v.findViewById(R.id.txt_reject_reason);

            builder.setView(v)
                    .setPositiveButton(R.string.panic, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sendPanic(txtReason.getText().toString());
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            AlertDialog dialog = builder.show();

            // If the specified reason is empty, disable the button.

            txtReason.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() == 0) {
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    } else {
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable editable) {}
            });

            // Disable it right from the start (onTextChanged() isn't called on its own).

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        });
    }

    private void sendPanic(String reason) {
        if (currentRide == null) {
            return;
        }

        IRideService.service.panicRide(currentRide.getId(), new PanicDTO(reason)).enqueue(new Callback<RideDTO>() {
            @Override
            public void onResponse(Call<RideDTO> call, Response<RideDTO> response) {
            }

            @Override
            public void onFailure(Call<RideDTO> call, Throwable t) {
                Log.e("REST Error", t.toString());
            }
        });
    }
}