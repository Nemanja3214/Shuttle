package com.example.shuttlemobile.common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.media.tv.TvInputService;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.passenger.Passenger;
import com.example.shuttlemobile.user.User;
import com.example.shuttlemobile.util.JWTDecoder;
import com.example.shuttlemobile.util.SettingsUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <code>GenericUserActivity</code> is an abstract activity with a toolbar and fragment view.
 * The toolbar by default only has a back button.
 * The fragment view is managed with <code>fragments</code> which maps toolbar IDs into instances
 * of fragment objects, all created once during the activity's lifetime.
 */
public abstract class GenericUserActivity extends SimpleToolbarActivity {
    private final String STACK_FRAGMENTS = "UserActivityFragment";
    protected Map<Integer, Fragment> fragments = new HashMap<>();
    private Fragment currentFragment;
    protected SessionContext session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_user);

        // TODO: Remove this, we use SharedPreferences for now.
        session = new SessionContext();
        session.setUser(new Passenger(
                "", "", "", "", "", "", "", false, true
        ));

        initializeFragmentMap();
        initializeFragmentView();
    }

    @Override
    public void onAttachedToWindow() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setOnMenuItemClickListener(item -> toolbarOnItemClick(item));
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param item Which item in the toolbar.
     * @return false.
     */
    protected boolean toolbarOnItemClick(MenuItem item) {
        return false;
    }

    /**
     * @return The ID of this activity's fragment frame. Needed for internal use.
     */
    protected abstract int getFragmentFrameId();

    /**
     * Initialize 'fragments' map.
     * Use MenuItem IDs (the ones used in <b>toolbarOnItemClick()</b>) for keys
     * and factory method instances for values.
     */
    protected abstract void initializeFragmentMap();

    /**
     * @return The default (eg. home) fragment. Needed for internal use.
     */
    protected abstract Fragment getDefaultFragment();

    protected final void setVisibleFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .replace(getFragmentFrameId(), fragment);
        fragmentTransaction.addToBackStack(STACK_FRAGMENTS);
        fragmentTransaction.commit();
        currentFragment = fragment;
    }

    private final void initializeFragmentView() {
        setVisibleFragment(getDefaultFragment());
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();

        if (currentFragment == getDefaultFragment()) {
            if (fm.getBackStackEntryCount() == 1) {
                finish();
                return;
            }
            super.onBackPressed();
        } else {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            setVisibleFragment(getDefaultFragment());
        }
    }
}