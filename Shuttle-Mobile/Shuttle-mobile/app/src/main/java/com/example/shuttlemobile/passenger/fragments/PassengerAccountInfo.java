package com.example.shuttlemobile.passenger.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.GenericUserFragment;
import com.example.shuttlemobile.driver.services.DriverMessageService;
import com.example.shuttlemobile.driver.services.DriverRideService;
import com.example.shuttlemobile.passenger.IPassengerService;
import com.example.shuttlemobile.passenger.PassengerActivity;
import com.example.shuttlemobile.passenger.dto.PassengerDTO;
import com.example.shuttlemobile.passenger.services.PassengerMessageService;
import com.example.shuttlemobile.passenger.services.PassengerRideService;
import com.example.shuttlemobile.unregistered.LoginActivity;
import com.example.shuttlemobile.user.IUserService;
import com.example.shuttlemobile.user.services.UserMessageService;
import com.example.shuttlemobile.util.SettingsUtil;
import com.example.shuttlemobile.util.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment for viewing and updating account info for the passenger of this session.
 * Only certain fields are editable, while others must be changed in the web application.
 */
public class PassengerAccountInfo extends GenericUserFragment {
    private EditText editName;
    private EditText editEmail;
    private EditText editSurname;
    private EditText editAddress;
    private EditText editPhone;
    private ImageButton editPfp;
    private Button btnSubmit;

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private String currentImage;
    private PassengerDTO passenger;

    public static PassengerAccountInfo newInstance() {
        PassengerAccountInfo fragment = new PassengerAccountInfo();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_account_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        editName = getActivity().findViewById(R.id.txt_p_info_name);
        editSurname = getActivity().findViewById(R.id.txt_p_info_surname);
        editAddress = getActivity().findViewById(R.id.txt_p_info_address);
        editPhone = getActivity().findViewById(R.id.txt_p_info_phone);
        editEmail= getActivity().findViewById(R.id.txt_p_info_email);
        editPfp = getActivity().findViewById(R.id.img_p_info_pfp);
        btnSubmit = getActivity().findViewById(R.id.btn_p_info_info_submit);

        editPfp.setOnClickListener(view -> {
            if (checkAndRequestPermissions(getActivity())) {
                chooseImage(getContext());
            }
        });

        fillData();

        // TODO: If all the input fields are the same as the current user data, disable the button.
        // You have to use listeners for each edit text.
        boolean canSubmit = true;
        btnSubmit.setActivated(canSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(valid()){

                    updatePassenger();

                }
            }
        });

        Button btnLogout = getActivity().findViewById(R.id.btn_passenger_logout);
        btnLogout.setOnClickListener(view -> logout());
    }

    private void logout() {
        IUserService.service.setInactive(SettingsUtil.getUserJWT().getId()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                getActivity().stopService(new Intent(getActivity().getApplicationContext(), PassengerRideService.class));
                getActivity().stopService(new Intent(getActivity().getApplicationContext(), PassengerMessageService.class));
                getActivity().stopService(new Intent(getActivity().getApplicationContext(), UserMessageService.class));

                SettingsUtil.clearUser();
                Intent toLogin = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(toLogin);
                getActivity().finish();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), "Could not log out.", Toast.LENGTH_SHORT);
            }
        });
    }

    private boolean valid() {
        if(editName.getText().toString().equals("")){
            Toast.makeText(requireContext(), "Field name is required", Toast.LENGTH_LONG).show();
            return false;
        }
        if(editAddress.getText().toString().equals("")){
            Toast.makeText(requireContext(), "Field surname is required", Toast.LENGTH_LONG).show();
            return false;
        }
        if(editPhone.getText().toString().equals("")){
            Toast.makeText(requireContext(), "Field address is required", Toast.LENGTH_LONG).show();
            return false;
        }
        if(editSurname.getText().toString().equals("")){
            Toast.makeText(requireContext(), "Field phone number is required", Toast.LENGTH_LONG).show();
            return false;
        }
        if(editEmail.getText().toString().equals("")){
            Toast.makeText(requireContext(), "Field email is required", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void fillData(){
        long passengerId = SettingsUtil.getUserJWT().getId();
        Call<PassengerDTO> call = IPassengerService.service.getPassenger(passengerId);
        call.enqueue(new Callback<PassengerDTO>() {
            @Override
            public void onResponse(Call<PassengerDTO> call, Response<PassengerDTO> response) {
                if(response.isSuccessful()){
                    passenger = response.body();
                    editPfp.setImageBitmap(Utils.getImageFromBase64(passenger.getProfilePicture()));
                    editName.setText(passenger.getName());
                    editSurname.setText(passenger.getSurname());
                    editAddress.setText(passenger.getAddress());
                    editPhone.setText(passenger.getTelephoneNumber());
                    editEmail.setText(passenger.getEmail());
                }
            }

            @Override
            public void onFailure(Call<PassengerDTO> call, Throwable t) {

            }
        });
    }

    private void updatePassenger() {
        PassengerDTO passengerCopy = constructPassenger();
        Call<PassengerDTO> call = IPassengerService.service.updatePassenger(SettingsUtil.getUserJWT().getId(), passengerCopy);
        call.enqueue(new Callback<PassengerDTO>() {
            @Override
            public void onResponse(Call<PassengerDTO> call, Response<PassengerDTO> response) {
                if (response.isSuccessful()) {

                    passenger = response.body();
                    SettingsUtil.put(SettingsUtil.KEY_ACCESS_TOKEN,passenger.getJwt());
                    Toast.makeText(getContext(), "Changes posted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PassengerDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to update", Toast.LENGTH_LONG).show();
            }
        });
    }

    private PassengerDTO constructPassenger() {
        PassengerDTO passengerDTO = new PassengerDTO();
        passengerDTO.setAddress(editAddress.getText().toString());
        passengerDTO.setName(editName.getText().toString());
        passengerDTO.setSurname(editSurname.getText().toString());
        passengerDTO.setTelephoneNumber(editPhone.getText().toString());
        passengerDTO.setProfilePicture(currentImage);
        passengerDTO.setEmail(editEmail.getText().toString());
        return passengerDTO;
    }

    private void chooseImage(Context context) {

        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit"}; // create a menuOption Array

        // create a dialog for showing the optionsMenu

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // set the items in builder

        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (optionsMenu[i].equals("Take Photo")) {

                    // Open the camera and get the photo

                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                } else if (optionsMenu[i].equals("Choose from Gallery")) {

                    // choose from  external storage

                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);

                } else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }

            }
        });
        builder.show();
    }

    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(),
                                "Shuttle Requires Access to Camara.", Toast.LENGTH_SHORT)
                        .show();

            } else if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(),
                        "Shuttle Requires Access to Your Storage.",
                        Toast.LENGTH_SHORT).show();

            } else {
                chooseImage(getContext());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        editPfp.setImageBitmap(selectedImage);
                        currentImage = Utils.getBase64Bitmap(selectedImage);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = requireActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                Bitmap pfp = BitmapFactory.decodeFile(picturePath);
                                editPfp.setImageBitmap(pfp);
                                currentImage = Utils.getBase64Bitmap(pfp);
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }
}