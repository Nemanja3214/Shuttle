package com.example.shuttlemobile.driver.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.GenericUserFragment;
import com.example.shuttlemobile.common.SessionContext;
import com.example.shuttlemobile.driver.DriverStatsDTO;
import com.example.shuttlemobile.driver.IDriverService;
import com.example.shuttlemobile.passenger.dto.GraphEntryDTO;
import com.example.shuttlemobile.util.SettingsUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverAccountStats extends GenericUserFragment {


    public static DriverAccountStats newInstance(SessionContext session) {
        DriverAccountStats fragment = new DriverAccountStats();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GenericUserFragment.KEY_SESSION, session);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_account_stats, container, false);
        EditText rejections = view.findViewById(R.id.d_stat_rejection);
        EditText rides = view.findViewById(R.id.d_stat_rides);
        EditText hours = view.findViewById(R.id.d_stat_hours);
        EditText money = view.findViewById(R.id.d_stat_money);
        RadioButton daily = view.findViewById(R.id.day_radio);
        RadioButton weekly = view.findViewById(R.id.week_radio);
        RadioButton monthly = view.findViewById(R.id.month_radio);
        Button submit = view.findViewById(R.id.d_stat_submit);

        submit.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                String scope = "daily";
                if (daily.isChecked()) scope = "daily";
                if (monthly.isChecked()) scope = "monthly";
                if (weekly.isChecked()) scope = "weekly";
                IDriverService.service.getStatistics(SettingsUtil.getUserJWT().getId(), scope).enqueue(new Callback<DriverStatsDTO>() {
                    @Override
                    public void onResponse(Call<DriverStatsDTO> call, Response<DriverStatsDTO> response) {
                        if (response.isSuccessful()) {
                            DriverStatsDTO dto = response.body();
                            setData(rejections, rides, hours, money, dto);
                        } else {
                            Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DriverStatsDTO> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed retreiving data.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        return view;
    }

    private void setData(EditText rejections, EditText rides, EditText hours, EditText money, DriverStatsDTO statsDTO) {
        rejections.setText(statsDTO.getRejections() + "");
        rides.setText(statsDTO.getRides() + "");
        hours.setText(statsDTO.getHoursWorked() + "");
        money.setText(statsDTO.getMoneyEarned() + "");
    }


}