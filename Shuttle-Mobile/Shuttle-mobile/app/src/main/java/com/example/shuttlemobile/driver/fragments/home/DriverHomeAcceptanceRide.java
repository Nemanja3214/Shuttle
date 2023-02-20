package com.example.shuttlemobile.driver.fragments.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.driver.fragments.DriverHome;
import com.example.shuttlemobile.driver.fragments.PassengerData;
import com.example.shuttlemobile.driver.services.DriverRideService;
import com.example.shuttlemobile.ride.IRideService;
import com.example.shuttlemobile.ride.Ride;
import com.example.shuttlemobile.ride.dto.RejectionDTOMinimal;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.route.LocationDTO;
import com.example.shuttlemobile.util.NotificationUtil;
import com.example.shuttlemobile.util.SettingsUtil;
import com.mapbox.geojson.Point;

import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHomeAcceptanceRide extends Fragment {
    private TextView txtDeparture;
    private TextView txtDestination;
    private TextView txtDistance;
    private TextView txtTime;
    private TextView txtPrice;
    private TextView txtPassengerCount;
    private Button btnReject;
    private Button btnBegin;
    private Button btnAccept;
    private Button btnViewPassengers;

    private DriverHome parent = null;

    private BroadcastReceiver rideReceiver;

    private Point A = null;
    private Point B = null;
    private RideDTO ride = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_home_acceptance_ride, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initParent();
        initViewElements(view);
        initRideReceiver();
    }

    private void initParent() {
        parent = (DriverHome)DriverHomeAcceptanceRide.this.getParentFragment();
        if (parent == null) {
            throw new IllegalStateException("Parent is null (should be DriverHome)");
        }
    }

    private void initRideReceiver() {
        rideReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RideDTO rideDTO = (RideDTO)intent.getSerializableExtra(DriverRideService.INTENT_RIDE_KEY);
                onGetRide(rideDTO);
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DriverRideService.BROADCAST_CHANNEL);
        getActivity().registerReceiver(rideReceiver, intentFilter);
    }

    public static DriverHomeAcceptanceRide newInstance() {
        return new DriverHomeAcceptanceRide();
    }

    private void initViewElements(View view) {
        txtDeparture = view.findViewById(R.id.txt_acceptance_ride_departure);
        txtDestination = view.findViewById(R.id.txt_acceptance_ride_destination);
        txtDistance = view.findViewById(R.id.txt_acceptance_ride_distance);
        txtTime = view.findViewById(R.id.txt_acceptance_ride_duration);
        txtPrice = view.findViewById(R.id.txt_acceptance_ride_price);
        txtPassengerCount = view.findViewById(R.id.txt_acceptance_ride_passengers);
        btnReject = view.findViewById(R.id.btn_acceptance_ride_reject);
        btnBegin = view.findViewById(R.id.btn_acceptance_ride_begin);
        btnAccept = view.findViewById(R.id.btn_acceptance_ride_accept);
        btnViewPassengers = view.findViewById(R.id.btn_acceptance_ride_passengers);

        initRejectButton();
        initAcceptButton();
        initBeginButton();
        initViewPassengersButton();

        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        if (this.ride == null) {
            return;
        }

        if (Ride.State.valueOf(ride.getStatus()) == Ride.State.ACCEPTED) {
            this.btnBegin.setVisibility(View.VISIBLE);
            this.btnAccept.setVisibility(View.INVISIBLE);
            this.btnReject.setVisibility(View.INVISIBLE);
        } else {
            this.btnBegin.setVisibility(View.INVISIBLE);
            this.btnAccept.setVisibility(View.VISIBLE);
            this.btnReject.setVisibility(View.VISIBLE);
        }
    }

    private void initRejectButton() {
        btnReject.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View v = inflater.inflate(R.layout.alert_reject_reason, null);
            EditText txtReason = v.findViewById(R.id.txt_reject_reason);

            builder.setView(v)
                    .setPositiveButton(R.string.reject, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            rejectRide(txtReason.getText().toString());
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

    private void initBeginButton() {
        this.btnBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginRide();
            }
        });
    }

    private void initAcceptButton() {
        this.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptRide();
            }
        });
    }

    private void initViewPassengersButton() {
        btnViewPassengers.setOnClickListener(view -> {
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
                    ride.getPassengers().stream().map(o -> o.getEmail()).collect(Collectors.toList()).toArray(new String[0]),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showPassengerData(i);
                        }
                    }
                );
            AlertDialog dialog = builder.show();
        });
    }

    private void acceptRide() {
        if (ride == null) {
            return;
        }

        IRideService.service.acceptRide(ride.getId()).enqueue(new Callback<RideDTO>() {
            @Override
            public void onResponse(Call<RideDTO> call, Response<RideDTO> response) {
            }

            @Override
            public void onFailure(Call<RideDTO> call, Throwable t) {
                Log.e("REST Error", t.toString());
            }
        });
    }

    private void beginRide() {
        if (ride == null) {
            return;
        }

        IRideService.service.startRide(ride.getId()).enqueue(new Callback<RideDTO>() {
            @Override
            public void onResponse(Call<RideDTO> call, Response<RideDTO> response) {
            }

            @Override
            public void onFailure(Call<RideDTO> call, Throwable t) {
                Log.e("REST Error", t.toString());
            }
        });
    }

    private void rejectRide(String reason) {
        if (ride == null) {
            return;
        }

        IRideService.service.rejectRide(ride.getId(), new RejectionDTOMinimal(reason)).enqueue(new Callback<RideDTO>() {
            @Override
            public void onResponse(Call<RideDTO> call, Response<RideDTO> response) {
                clearRide();
            }

            @Override
            public void onFailure(Call<RideDTO> call, Throwable t) {
                Log.e("REST Error", t.toString());
            }
        });
    }

    private void clearRide() {
        this.ride = null;
        this.A = null;
        this.B = null;
    }

    private void onGetRide(RideDTO dto) {
        // If the DTO is null, then there is no ride.

        if (dto == null) {
            ride = null;
            A = null;
            B = null;
            return;
        }

        // If this is the first time you're getting this ride, send a notification.

        boolean shouldSendNotification = false;
        if (!SettingsUtil.get(SettingsUtil.KEY_CURRENT_RIDE_ID, -1L).equals(dto.getId())) {
            shouldSendNotification = true;
        }

        // If the currently cached ride is different from this one, redraw the route and focus on it.

        boolean shouldRedrawRoute = false;
        if (ride == null || !ride.getId().equals(dto.getId())) {
            shouldRedrawRoute = true;
        }

        // Update ride.

        ride = dto;
        final LocationDTO A_loc = ride.getLocations().get(0).getDeparture();
        final LocationDTO B_loc = ride.getLocations().get(ride.getLocations().size() - 1).getDestination();

        A = Point.fromLngLat(A_loc.getLongitude(), A_loc.getLatitude());
        B = Point.fromLngLat(B_loc.getLongitude(), B_loc.getLatitude());

        // Update text views.

        txtDeparture.setText(A_loc.getAddress());
        txtDestination.setText(B_loc.getAddress());
        txtDistance.setText("???");
        txtTime.setText(dto.getEstimatedTimeInMinutes() + "min");
        txtPrice.setText(dto.getTotalCost() + " RSD");
        txtPassengerCount.setText(dto.getPassengers().size() + "");
        updateButtonVisibility();

        // Update the things from above if neccessary.

        if (shouldRedrawRoute) {
            parent.drawRoute(A, B, "#0000FF");
            parent.fitViewport(A, B, 3000);
        }

        if (shouldSendNotification) {
            NotificationUtil.sendNotification(
                    getActivity(),
                    NotificationUtil.DRIVER_NOTIFICATION_CHANNEL_ID,
                    "New ride",
                    "You have a new ride!",
                    R.drawable.car_green,
                    1100110110
            );

            SettingsUtil.put(SettingsUtil.KEY_CURRENT_RIDE_ID, ride.getId());
        }
    }

    private void showPassengerData(int position) {
        // Copied from DriverCurrentRide

        Bundle bundle = new Bundle();
        long passengerId = ride.getPassengers().get(position).getId();
        bundle.putLong(PassengerData.PASSENGER_ID, passengerId);
        bundle.putSerializable(PassengerData.RIDE, ride);

        DialogFragment dialog = new PassengerData();
        dialog.setArguments(bundle);
        dialog.show(getChildFragmentManager(), "Passenger data");
    }
}