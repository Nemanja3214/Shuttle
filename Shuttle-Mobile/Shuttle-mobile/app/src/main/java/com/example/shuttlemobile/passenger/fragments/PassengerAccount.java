package com.example.shuttlemobile.passenger.fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.GenericUserFragment;
import com.example.shuttlemobile.common.GenericUserTabbedFragment;
import com.example.shuttlemobile.common.SessionContext;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PassengerAccount extends GenericUserTabbedFragment {
    public static PassengerAccount newInstance(SessionContext session) {
        PassengerAccount fragment = new PassengerAccount();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GenericUserFragment.KEY_SESSION, session);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_passenger_account;
    }

    @Override
    protected int getTabLayoutID() {
        return R.id.tab_p_acc;
    }

    @Override
    protected int getFragmentContainerID() {
        return R.id.fragview_p_acc_tab;
    }

    @Override
    protected void initFragmentList() {
        fragments.add(PassengerAccountInfo.newInstance());
        fragments.add(PassengerAccountFavorites.newInstance());
        fragments.add(PassengerAccountStats.newInstance());
    }
}