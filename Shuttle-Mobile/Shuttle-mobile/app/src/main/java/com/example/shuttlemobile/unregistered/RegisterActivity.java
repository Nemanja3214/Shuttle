package com.example.shuttlemobile.unregistered;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.MyValidator;
import com.example.shuttlemobile.common.MyValidatorException;
import com.example.shuttlemobile.common.SimpleToolbarActivity;
import com.example.shuttlemobile.common.UserDTONoPassword;
import com.example.shuttlemobile.passenger.IPassengerService;
import com.example.shuttlemobile.passenger.dto.PassengerDTO;
import com.google.android.material.appbar.AppBarLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends SimpleToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Register();
    }

    private void Register() {
        Button registerButton = findViewById(R.id.btn_un_reg_register);
        EditText emailText = findViewById(R.id.txt_un_reg_email);
        EditText passwordText = findViewById(R.id.txt_un_reg_password);
        EditText nameText = findViewById(R.id.txt_un_reg_name);
        EditText surnameText = findViewById(R.id.txt_un_reg_surname);
        EditText addressText = findViewById(R.id.txt_un_reg_address);
        EditText phoneText = findViewById(R.id.txt_un_reg_phone);
        EditText confirmText = findViewById(R.id.txt_un_reg_cfrm_password);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    PassengerDTO dto = new PassengerDTO();
                    dto.setName(nameText.getText().toString());
                    dto.setSurname(surnameText.getText().toString());
                    dto.setEmail(emailText.getText().toString());
                    dto.setAddress(addressText.getText().toString());
                    dto.setPassword(passwordText.getText().toString());
                    dto.setTelephoneNumber(phoneText.getText().toString());
                    MyValidator.validateRequired(dto.getName(), "name");
                    MyValidator.validateRequired(dto.getSurname(), "surname");
                    MyValidator.validateRequired(dto.getEmail(), "email");
                    MyValidator.validateRequired(dto.getAddress(), "address");
                    MyValidator.validateRequired(dto.getPassword(), "password");

                    MyValidator.validateMatchingPassword(dto.getPassword(),confirmText.getText().toString());

                    MyValidator.validateLength(dto.getName(), "name", 100);
                    MyValidator.validateLength(dto.getSurname(), "surname", 100);
                    MyValidator.validateLength(dto.getTelephoneNumber(), "telephoneNumber", 18);
                    MyValidator.validateLength(dto.getEmail(), "email", 100);
                    MyValidator.validateLength(dto.getAddress(), "address", 100);
                    MyValidator.validatePattern(dto.getPassword(), "password", "^(?=.*\\d)(?=.*[A-Z])(?!.*[^a-zA-Z0-9@#$^+=])(.{8,15})$");



                    IPassengerService.service.registerPassenger(dto).enqueue(new Callback<UserDTONoPassword>() {
                        @Override
                        public void onResponse(Call<UserDTONoPassword> call, Response<UserDTONoPassword> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Confirm your mail to continue", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserDTONoPassword> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Your device is offline", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (MyValidatorException e1) {
                    Toast.makeText(getApplicationContext(), e1.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}