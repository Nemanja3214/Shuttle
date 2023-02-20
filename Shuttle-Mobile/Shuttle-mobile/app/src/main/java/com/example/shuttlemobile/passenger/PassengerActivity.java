package com.example.shuttlemobile.passenger;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.shuttlemobile.common.GenericUserActivity;
import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.SettingsFragment;
import com.example.shuttlemobile.passenger.fragments.PassengerAccount;
import com.example.shuttlemobile.passenger.fragments.PassengerHistory;
import com.example.shuttlemobile.passenger.fragments.PassengerHome;
import com.example.shuttlemobile.common.InboxFragment;
import com.example.shuttlemobile.passenger.services.PassengerMessageService;
import com.example.shuttlemobile.passenger.services.PassengerRideService;
import com.example.shuttlemobile.user.services.UserMessageService;

import java.util.ArrayList;
import java.util.List;

public class PassengerActivity extends GenericUserActivity {

    List<Intent> serviceIntents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        StartServices();
    }

    private void StartServices() {
        Intent passengerMessageServiceIntent = new Intent(this, PassengerMessageService.class);
        Intent passengerRideServiceIntent = new Intent(this, PassengerRideService.class);
        Intent userMessageServiceIntent = new Intent(this, UserMessageService.class);
        startService(passengerMessageServiceIntent);
        startService(passengerRideServiceIntent);
        startService(userMessageServiceIntent);
        serviceIntents.add(passengerMessageServiceIntent);
        serviceIntents.add(passengerRideServiceIntent);
        serviceIntents.add(userMessageServiceIntent);
    }

    public void StopServices() {
        for (Intent i : serviceIntents) {
            stopService(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected boolean toolbarOnItemClick(MenuItem item) {
        final int itemId = item.getItemId();

        Fragment f = fragments.get(itemId);

        if (f != null) {
            setVisibleFragment(f);
        }

        return false;
    }

    @Override
    protected int getFragmentFrameId() {
        return R.id.passenger_fragment_frame;
    }

    @Override
    protected void initializeFragmentMap() {
        fragments.put(R.id.toolbar_home, PassengerHome.newInstance(session));
        fragments.put(R.id.toolbar_history, PassengerHistory.newInstance(session));
        fragments.put(R.id.toolbar_inbox, InboxFragment.newInstance(session));
        fragments.put(R.id.toolbar_account, PassengerAccount.newInstance(session));
        fragments.put(R.id.toolbar_settings, SettingsFragment.newInstance());
    }

    @Override
    protected Fragment getDefaultFragment() {
        return fragments.get(R.id.toolbar_home);
    }
}