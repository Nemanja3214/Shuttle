package com.example.shuttlemobile.driver;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.shuttlemobile.common.GenericUserActivity;
import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.SettingsFragment;
import com.example.shuttlemobile.driver.fragments.DriverAccount;
import com.example.shuttlemobile.driver.fragments.DriverHistory;
import com.example.shuttlemobile.driver.fragments.DriverHome;
import com.example.shuttlemobile.common.InboxFragment;
import com.example.shuttlemobile.driver.services.DriverMessageService;
import com.example.shuttlemobile.driver.services.DriverRideService;
import com.example.shuttlemobile.user.services.UserMessageService;

public class DriverActivity extends GenericUserActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        startService(new Intent(getApplicationContext(), DriverMessageService.class));
        startService(new Intent(getApplicationContext(), DriverRideService.class));
        startService(new Intent(getApplicationContext(), UserMessageService.class));
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
        return R.id.driver_home_fragment_frame;
    }

    @Override
    protected void initializeFragmentMap() {
        fragments.put(R.id.toolbar_home, DriverHome.newInstance(session));
        fragments.put(R.id.toolbar_history, DriverHistory.newInstance(session));
        fragments.put(R.id.toolbar_inbox, InboxFragment.newInstance(session));
        fragments.put(R.id.toolbar_account, DriverAccount.newInstance(session));
        fragments.put(R.id.toolbar_settings, SettingsFragment.newInstance());
    }

    @Override
    protected Fragment getDefaultFragment() {
        return fragments.get(R.id.toolbar_home);
    }
}