package com.example.shuttlemobile.unregistered;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.driver.DriverActivity;
import com.example.shuttlemobile.passenger.PassengerActivity;
import com.example.shuttlemobile.unregistered.login.LoginDTO;
import com.example.shuttlemobile.unregistered.login.TokenDTO;
import com.example.shuttlemobile.user.JWT;
import com.example.shuttlemobile.user.User;
import com.example.shuttlemobile.util.SettingsUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initButtonCallbacks();
        Log.e("A", "B");

//        if (openHomeActivityFromJwtInSharedPrefs()) {
//            finish();
//        }
    }

    private void initButtonCallbacks() {
        Button btnLogin = findViewById(R.id.btn_un_login);
        Button btnRegister = findViewById(R.id.btn_un_register);
        Button btnForgot = findViewById(R.id.btn_un_forgot);

        btnLogin.setOnClickListener(view -> login());
        btnRegister.setOnClickListener(view -> openRegistrationActivity());
        btnForgot.setOnClickListener(view -> openForgotPasswordActivity());
    }

    private void login() {
        EditText txtEmail = findViewById(R.id.txt_un_email);
        EditText txtPassword = findViewById(R.id.txt_un_password);

        final String email = txtEmail.getText().toString();
        final String password = txtPassword.getText().toString();

        Call<TokenDTO> call = ILoginService.service.getUser(new LoginDTO(email, password));
        call.enqueue(new Callback<TokenDTO>() {
            @Override
            public void onResponse(Call<TokenDTO> call, Response<TokenDTO> response) {
                TokenDTO token = response.body();
                if (token == null) {
                    Toast.makeText(LoginActivity.this, "Wrong email or password", Toast.LENGTH_LONG).show();
                } else {
                    SettingsUtil.put(SettingsUtil.KEY_ACCESS_TOKEN, token.getAccessToken());
                    if (!openHomeActivityFromJwtInSharedPrefs()) {
                        Toast.makeText(LoginActivity.this, "Unauthorized!", Toast.LENGTH_LONG).show();
                    } else {
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<TokenDTO> call, Throwable t) {
                Log.e("REST ERROR", t.toString());
            }
        });
    }

    private boolean openHomeActivityFromJwtInSharedPrefs() {
        final JWT jwt = SettingsUtil.getUserJWT();

        if (jwt == null) {
            return false;
        }

        if (jwt.getRoles().contains(User.Role.Passenger)) {
            startActivity(new Intent(getApplicationContext(), PassengerActivity.class));
            finish();
            return true;
        } else if (jwt.getRoles().contains(User.Role.Driver)) {
            startActivity(new Intent(getApplicationContext(), DriverActivity.class));
            finish();
            return true;
        }
        return false;
    }

    private void openRegistrationActivity() {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }

    private void openForgotPasswordActivity() {
        startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
    }
}