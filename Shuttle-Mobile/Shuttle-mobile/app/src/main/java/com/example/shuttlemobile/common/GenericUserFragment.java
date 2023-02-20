package com.example.shuttlemobile.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * <p>
 * GenericUserFragment is an abstract fragment class for all main fragments
 * contained in a <i>GenericUserActivity</i>. The fragment contains a <i>SessionContext</i>
 * from which you can obtain the user and other parameters.
 * </p>
 */
public abstract class GenericUserFragment extends Fragment {
    protected SessionContext session = null;
    public static final String KEY_SESSION = "session";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (getArguments() != null) {
            session = (SessionContext)getArguments().getSerializable(KEY_SESSION);
        }

//        if (session == null) {
//            throw new RuntimeException("Missing bundle argument: " + KEY_SESSION);
//        }
    }
}
