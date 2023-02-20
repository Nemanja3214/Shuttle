package com.example.shuttlemobile.driver.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.UserChatActivity;
import com.example.shuttlemobile.message.Message;
import com.example.shuttlemobile.passenger.IPassengerService;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.passenger.dto.PassengerDTO;
import com.example.shuttlemobile.util.Utils;

import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerData extends DialogFragment {
    public static final String PASSENGER_ID = "passenger id";
    public static final String RIDE = "ride";
    private long passengerId;
    private PassengerDTO passenger;

    private ImageView imgProfilePicture;
    private TextView tvName;
    private TextView tvSurname;
    private TextView tvPhone;
    private TextView tvEmail;
    private TextView tvAddress;
    private Button btnChat;
    private Button btnCall;
    private RideDTO ride;

    public static PassengerData newInstance() {
        PassengerData fragment = new PassengerData();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        passengerId = requireArguments().getLong(PASSENGER_ID);
        ride = (RideDTO) requireArguments().getSerializable(RIDE);
    }

    private void fillData() {
        Call<PassengerDTO> call = IPassengerService.service.getPassenger(passengerId);
        call.enqueue(new Callback<PassengerDTO>() {
            @Override
            public void onResponse(Call<PassengerDTO> call, Response<PassengerDTO> response) {
                if(response.isSuccessful() && response.body() != null){
                    passenger = response.body();
                    setData();
                }
                else {
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<PassengerDTO> call, Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setData() {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Handler handler = new Handler();
        executorService.execute(() -> handler.post(() -> {
            tvName.setText(passenger.getName());
            tvSurname.setText(passenger.getSurname());
            tvPhone.setText(passenger.getTelephoneNumber());
            tvEmail.setText(passenger.getEmail());
            tvAddress.setText(passenger.getAddress());
            imgProfilePicture.setImageBitmap(Utils.getImageFromBase64(passenger.getProfilePicture()));
        })
        );
    }

    private void initViews(View view) {
        imgProfilePicture = view.findViewById(R.id.img_d_passenger_profile_picture);
        tvName = view.findViewById(R.id.tv_d_passenger_name);
        tvSurname = view.findViewById(R.id.tv_d_passenger_surname);
        tvPhone = view.findViewById(R.id.tv_d_passenger_phone_number);
        tvEmail = view.findViewById(R.id.tv_d_passenger_email);
        tvAddress = view.findViewById(R.id.tv_d_passenger_address);
        btnCall = view.findViewById(R.id.btn_d_passenger_call);
        btnChat = view.findViewById(R.id.btn_d_passenger_chat);

        btnChat.setOnClickListener(view1 -> openChat());
        btnCall.setOnClickListener(view12 -> call());
    }

    private void call() {
        int result = promptPermission();
        if (result == 0 && passenger.getTelephoneNumber() != null){
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:"+passenger.getTelephoneNumber()));
            startActivity(callIntent);
        }
    }

    private int promptPermission() {
        if(ContextCompat.checkSelfPermission(requireContext().getApplicationContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
        return ContextCompat.checkSelfPermission(requireContext().getApplicationContext(), Manifest.permission.CALL_PHONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_passenger_data, container, false);
        initViews(v);
        fillData();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    private void openChat() {
        Intent intent = new Intent(getActivity(), UserChatActivity.class);

        intent.putExtra(UserChatActivity.PARAM_OTHER_ID, passengerId);
        intent.putExtra(UserChatActivity.PARAM_RIDE_ID, ride.getId());
        intent.putExtra(UserChatActivity.PARAM_MSG_TYPE, Message.Type.RIDE);

        startActivity(intent);
    }
}