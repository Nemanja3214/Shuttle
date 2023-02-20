package com.example.shuttlemobile.passenger.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.passenger.dto.GraphEntryDTO;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DateRangePicker#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateRangePicker extends Fragment {

    private MaterialDatePicker<Pair<Long, Long>> dateRangePicker;

    public static DateRangePicker newInstance() {
        DateRangePicker fragment = new DateRangePicker();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Instant instant = Instant.parse("2000-04-09T15:30:45.123Z");
        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .setValidator(DateValidatorPointForward.from(instant.toEpochMilli())).build();
        dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                            .setTitleText("Select dates")
                            .setSelection(
                                    new Pair<>(
                                            MaterialDatePicker.thisMonthInUtcMilliseconds(),
                                            MaterialDatePicker.todayInUtcMilliseconds()
                                    )
                            )
                            .setCalendarConstraints(constraints)
                            .build();
        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            PassengerAccountStats parentFrag = ((PassengerAccountStats)DateRangePicker.this.getParentFragment());

            if (parentFrag != null) {
                parentFrag.fetchGraphEntries(selection.first, selection.second);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_date_range_picker, container, false);
        Button btnNext = v.findViewById(R.id.btn_p_next_stats);


        btnNext.setOnClickListener(view -> {
            dateRangePicker.show(getChildFragmentManager(), "Date picker");
        });
        return v;
    }

}