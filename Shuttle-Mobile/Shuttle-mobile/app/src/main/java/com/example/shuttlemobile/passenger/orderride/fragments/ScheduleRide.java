package com.example.shuttlemobile.passenger.orderride.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.GenericUserFragment;
import com.example.shuttlemobile.common.SessionContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleRide extends Fragment {
    private static final int MAX_HOURS = 5;
    private static final int MAX_MINUTES = 60;
    private Spinner hourSpinner;
    private Spinner minuteSpinner;
    private Switch scheduledSwitch;

    public static ScheduleRide newInstance(SessionContext session) {
        ScheduleRide fragment = new ScheduleRide();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule_ride, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hourSpinner = view.findViewById(R.id.hour_spinner);
        minuteSpinner = view.findViewById(R.id.minute_spinner);
        scheduledSwitch = view.findViewById(R.id.schedule_switch);
        setHourSpinnerItems(hourSpinner);
        setMinuteSpinnerItems(minuteSpinner);
        setSwitchListener(view, hourSpinner, minuteSpinner);
    }

    private void setSwitchListener(View view, Spinner hourSpinner, Spinner minuteSpinner) {
        scheduledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hourSpinner.setEnabled(isChecked);
                minuteSpinner.setEnabled(isChecked);
            }
        });
    }

    private void setMinuteSpinnerItems(Spinner spinner) {

        List<Integer> hours = getMinuteValues();
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_spinner_item, hours);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setEnabled(false);
    }

    List<Integer> getMinuteValues() {
        List<Integer> minutes = new ArrayList<>();
        for (int i = 0; i < MAX_MINUTES; i += 5) {
            minutes.add(i);
        }
        return minutes;
    }

    private void setHourSpinnerItems(Spinner spinner) {
        List<Integer> hours = getHourValues();
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_spinner_item, hours);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setEnabled(false);
    }

    List<Integer> getHourValues() {
        List<Integer> hours = new ArrayList<>();
        for (int i = 0; i < MAX_HOURS; i++) {
            hours.add(i);
        }
        return hours;
    }

    public String getMinuteAdvance() {
        if (minuteSpinner.isEnabled()) {
            return minuteSpinner.getSelectedItem().toString();
        }
        return null;
    }


    public String getHourAdvance() {
        if (hourSpinner.isEnabled()) {
            return hourSpinner.getSelectedItem().toString();
        }
        return null;
    }

    public String getFutureTime() {
        if (scheduledSwitch.isChecked()) {
            String hDelta = getHourAdvance();
            String mDelta = getMinuteAdvance();

            LocalDateTime time = LocalDateTime.now().plusHours(Long.valueOf(hDelta)).plusMinutes(Long.valueOf(mDelta));
            return time.toString();
        }
        return null;
    }
}