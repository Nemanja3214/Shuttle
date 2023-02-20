package com.example.shuttlemobile.driver.fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.GenericUserFragment;
import com.example.shuttlemobile.common.GenericUserTabbedFragment;
import com.example.shuttlemobile.common.SessionContext;
import com.example.shuttlemobile.passenger.fragments.PassengerAccountFavorites;
import com.example.shuttlemobile.passenger.fragments.PassengerAccountInfo;
import com.example.shuttlemobile.passenger.fragments.PassengerAccountStats;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class DriverAccount extends GenericUserTabbedFragment {
    public static DriverAccount newInstance(SessionContext session) {
        DriverAccount fragment = new DriverAccount();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GenericUserFragment.KEY_SESSION, session);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_driver_account;
    }

    @Override
    protected int getTabLayoutID() {
        return R.id.tab_d_acc;
    }

    @Override
    protected int getFragmentContainerID() {
        return R.id.fragview_d_acc_tab;
    }

    @Override
    protected void initFragmentList() {
        fragments.add(DriverAccountInfo.newInstance(session));
        fragments.add(DriverAccountStats.newInstance(session));
        fragments.add(DriverAccountReports.newInstance(session));
    }
}