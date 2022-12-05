package com.example.shuttlemobile.driver;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.shuttlemobile.common.GenericUserActivity;
import com.example.shuttlemobile.R;
import com.example.shuttlemobile.driver.fragments.DriverAccount;
import com.example.shuttlemobile.driver.fragments.DriverHistory;
import com.example.shuttlemobile.driver.fragments.DriverHome;
import com.example.shuttlemobile.common.InboxFragment;

public class DriverActivity extends GenericUserActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
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
        return R.id.driver_fragment_frame;
    }

    @Override
    protected void initializeFragmentMap() {
        fragments.put(R.id.toolbar_home, DriverHome.newInstance(session));
        fragments.put(R.id.toolbar_history, DriverHistory.newInstance(session));
        fragments.put(R.id.toolbar_inbox, InboxFragment.newInstance(session));
        fragments.put(R.id.toolbar_account, DriverAccount.newInstance(session));
    }

    @Override
    protected Fragment getDefaultFragment() {
        return fragments.get(R.id.toolbar_home);
    }
}