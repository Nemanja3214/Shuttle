package com.example.shuttlemobile.unregistered;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.user.IUserService;
import com.example.shuttlemobile.user.dto.PasswordResetDTO;
import com.example.shuttlemobile.user.dto.UserEmailDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private Button btnSendCode;
    private Button btnConfirm;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtPasswordConfirm;
    private EditText txtCode;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViewElements();
    }

    private void initViewElements() {
        btnBack = findViewById(R.id.btn_un_forgot_back);
        btnSendCode = findViewById(R.id.btn_un_forgot_sendmail);
        btnConfirm = findViewById(R.id.btn_un_forgot_confirm);
        txtEmail = findViewById(R.id.txt_un_forgot_email);
        txtPassword = findViewById(R.id.txt_un_forgot_password);
        txtPasswordConfirm = findViewById(R.id.txt_un_forgot_password_confirm);
        txtCode = findViewById(R.id.txt_un_forgot_code);

        btnBack.setOnClickListener(view -> finish());
        btnSendCode.setOnClickListener(view -> sendCode());
        btnConfirm.setOnClickListener(view -> confirm());
    }

    private void sendCode() {
        String email = txtEmail.getText().toString();
        IUserService.service.findByEmail(email).enqueue(new Callback<UserEmailDTO>() {
            @Override
            public void onResponse(Call<UserEmailDTO> call, Response<UserEmailDTO> response) {
                if (response.code() == 200) {
                    userId = response.body().getId();
                    sendCodeReally();
                } else if (response.code() == 404) {
                    Toast.makeText(ForgotPasswordActivity.this, "Could not send email. Please check your address.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserEmailDTO> call, Throwable t) {
                Log.e("A", t.toString());
            }
        });

    }

    private void sendCodeReally() {
        ILoginService.service.resetPassword(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 204) {
                    Toast.makeText(ForgotPasswordActivity.this, "Email sent! Check your inbox.", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    Toast.makeText(ForgotPasswordActivity.this, "Could not send email. Please check your address.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("?", response.toString());
                    Log.e("?", "" + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("B", t.toString());
            }
        });
    }

    private void confirm() {
        String newP = txtPassword.getText().toString();
        String newPconfirm = txtPasswordConfirm.getText().toString();
        String code = txtCode.getText().toString();
        if (newP.compareTo(newPconfirm) != 0) {
            Toast.makeText(ForgotPasswordActivity.this, "Passwords don't match!.", Toast.LENGTH_SHORT).show();
            return;
        }

        ILoginService.service.resetPassword(userId, new PasswordResetDTO(newP, code)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 204) {
                    Toast.makeText(ForgotPasswordActivity.this, "Password has been changed!", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response.code() == 404) {
                    Toast.makeText(ForgotPasswordActivity.this, "Code is expired or not correct!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("?", response.toString());
                    Log.e("?", "" + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }
}