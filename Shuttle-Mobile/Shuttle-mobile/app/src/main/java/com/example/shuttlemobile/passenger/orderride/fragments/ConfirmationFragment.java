package com.example.shuttlemobile.passenger.orderride.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.SessionContext;
import com.example.shuttlemobile.passenger.orderride.ICreateRideService;
import com.example.shuttlemobile.passenger.orderride.OrderActivity;
import com.example.shuttlemobile.ride.dto.CreateRideDTO;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.route.RouteDTO;
import com.example.shuttlemobile.util.RetrofitUtils;
import com.example.shuttlemobile.vehicle.IVehicleService;
import com.example.shuttlemobile.vehicle.VehicleTypeDTO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmationFragment extends Fragment {
    private Button confirm;
    private CreateRideDTO dto;
    private List<VehicleTypeDTO> vehicleTypes = null;

    private TextView txtDeparture, txtDestination, txtBabies, txtPets, txtVehicleType, txtPassengerCount, txtScheduledFor, txtPrice;

    public static ConfirmationFragment newInstance(SessionContext session) {
        ConfirmationFragment fragment = new ConfirmationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dto = ((OrderActivity)getActivity()).getCreateRideDTO();

        confirm = view.findViewById(R.id.confirm_button);
        txtDeparture = view.findViewById(R.id.txt_order_stepper_departure);
        txtDestination = view.findViewById(R.id.txt_order_stepper_destination);
        txtBabies = view.findViewById(R.id.txt_order_stepper_babies);
        txtPets = view.findViewById(R.id.txt_order_stepper_pets);
        txtVehicleType = view.findViewById(R.id.txt_order_stepper_vehicle);
        txtPassengerCount = view.findViewById(R.id.txt_order_stepper_passenger_cnt);
        txtScheduledFor = view.findViewById(R.id.txt_order_stepper_scheduled_for);
        txtPrice = view.findViewById(R.id.txt_order_stepper_cost);

        confirm.setEnabled(false);
        initConfirmButton();
        initViews();
        fetchVehicleTypes();
    }

    private void fetchVehicleTypes() {
        IVehicleService.service.getVehicleTypes().enqueue(new Callback<List<VehicleTypeDTO>>() {
            @Override
            public void onResponse(Call<List<VehicleTypeDTO>> call, Response<List<VehicleTypeDTO>> response) {
                if (response.code() == 200) {
                    vehicleTypes = response.body();
                    confirm.setEnabled(true);
                    setPrice();
                } else {
                    Toast.makeText(getActivity(), "Could not fetch vehicle data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<VehicleTypeDTO>> call, Throwable t) {

            }
        });
    }

    private void initConfirmButton() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<RideDTO> call = ICreateRideService.service.postRide(dto);
                call.enqueue(new Callback<RideDTO>() {
                    @Override
                    public void onResponse(Call<RideDTO> call, Response<RideDTO> response) {
                        if(response.code() == 200)
                            Toast.makeText(getContext(), "Ride created", Toast.LENGTH_LONG).show();
                        else{
                            Toast.makeText(getContext(), RetrofitUtils.getErrorMessage(response), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RideDTO> call, Throwable t) {
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                getActivity().finish();

            }
        });
    }

    private void initViews() {
        txtDeparture.setText(dto.getLocations().get(0).getDeparture().getAddress());
        txtDestination.setText(dto.getLocations().get(dto.getLocations().size() - 1).getDestination().getAddress());
        txtBabies.setText(dto.isBabyTransport() ? "Bringing a baby" : "No babies");
        txtPets.setText(dto.isPetTransport() ? "Bringing a pet" : "No pets");
        txtVehicleType.setText(dto.getVehicleType() + " vehicle");
        txtPassengerCount.setText(dto.getPassengers().size() + "");
        txtScheduledFor.setText(dto.getScheduledTime() == null ? "Now!" : LocalDateTime.parse(dto.getScheduledTime()).format(DateTimeFormatter.ofPattern("HH:mm")));

    }

    private void setPrice() {
        txtPrice.setText(calcPrice() + " RSD");
    }

    private Double calcPrice() {
        final Long kmInt = Math.round(dto.getDistance() / 1000);
        final Double vehicleTypeCost = vehicleTypes.stream().filter(t -> t.getName().equalsIgnoreCase(dto.getVehicleType())).findFirst().get().getPricePerKM();
        final Double price = kmInt * (120 + vehicleTypeCost);

        return price;
    }
}