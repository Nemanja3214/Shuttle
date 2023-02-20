package com.example.shuttlemobile.common;

import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.shuttlemobile.R;

/**
 * <code>SimpleToolbarActivity</code> is an abstract activity with a toolbar that has
 * only the back button. The back button is functional. You have to include the toolbar
 * in the activity's layout file.
 */
public abstract class SimpleToolbarActivity extends AppCompatActivity {
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
}
