package com.example.shuttlemobile.passenger.fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.GenericUserFragment;
import com.example.shuttlemobile.common.SessionContext;

public class PassengerAccount extends GenericUserFragment {
    public static PassengerAccount newInstance(SessionContext session) {
        PassengerAccount fragment = new PassengerAccount();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GenericUserFragment.KEY_SESSION, session);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_account, container, false);
    }
}