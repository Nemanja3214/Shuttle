package com.example.shuttlemobile.passenger.fragments.home;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.passenger.fragments.PassengerHome;
import com.example.shuttlemobile.passenger.orderride.OrderActivity;
import com.example.shuttlemobile.route.LocationDTO;
import com.example.shuttlemobile.route.RouteDTO;
import com.mapbox.geojson.Point;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PassengerSearchRoute extends Fragment {
    private EditText txtDeparture;
    private EditText txtDestination;
    private Button btnCreateRoute;
    private Button btnOrderRoute;
    private ProgressBar progressBar;

    private PassengerHome parent = null;

    private Point A;
    private Point B;

    public static PassengerSearchRoute newInstance() {
        PassengerSearchRoute fragment = new PassengerSearchRoute();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_search_route, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewElements(view);
        initParent();
    }

    private void initParent() {
        parent = (PassengerHome) PassengerSearchRoute.this.getParentFragment();
        if (parent == null) {
            throw new IllegalStateException("Parent is null (should be PassengerHome)");
        }
    }

    private void initViewElements(View view) {
        txtDeparture = view.findViewById(R.id.txt_p_home_departure);
        txtDestination = view.findViewById(R.id.txt_p_home_destination);
        btnCreateRoute = view.findViewById(R.id.btn_p_home_makeRoute);
        btnOrderRoute = view.findViewById(R.id.btn_p_home_order);
        progressBar = view.findViewById(R.id.progress_p_home);

        progressBar.setVisibility(View.INVISIBLE);

        txtDeparture.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) { btnOrderRoute.setEnabled(false); }
        });
        txtDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) { btnOrderRoute.setEnabled(false); }
        });

        btnCreateRoute.setOnClickListener(view1 -> {
            progressBar.setVisibility(View.VISIBLE);
            hideKeyboard();
            makeRouteFromInput();
            parent.makeRoute(A, B);
            btnOrderRoute.setEnabled(hasRoute());
            progressBar.setVisibility(View.INVISIBLE);
        });

        btnOrderRoute.setEnabled(hasRoute());
        btnOrderRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OrderActivity.class);

                LocationDTO departure = new LocationDTO(txtDeparture.getText().toString(), A.latitude(), A.longitude());
                LocationDTO destination = new LocationDTO(txtDestination.getText().toString(), B.latitude(), B.longitude());
                RouteDTO routeDTO = new RouteDTO(departure, destination);

                intent.putExtra(OrderActivity.KEY_DIST, parent.getDistance());
                intent.putExtra(OrderActivity.KEY_ROUTE, routeDTO);

                startActivity(intent);
            }
        });
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(btnCreateRoute.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        } catch (Exception e) {
        }
    }

    private void makeRouteFromInput() {
        final String dep = txtDeparture.getText().toString();
        final String dest = txtDestination.getText().toString();

        final Geocoder geocoder = new Geocoder(getContext());

        List<Address> addressesDep;
        List<Address> addressesDest;

        try {
            addressesDep = geocoder.getFromLocationName(dep, 1);
            addressesDest = geocoder.getFromLocationName(dest, 1);

            if (addressesDep.size() == 0) {
                Log.e("E", "Could not find departure");
                A = null;
                return;
            }
            if (addressesDest.size() == 0) {
                Log.e("E", "Could not find destination");
                B = null;
                return;
            }
        } catch (IOException e) {
            A = null;
            B = null;
            e.printStackTrace();
            return;
        }

        final Address adrA = addressesDep.get(0);
        final Address adrB = addressesDest.get(0);

        A = Point.fromLngLat(adrA.getLongitude(), adrA.getLatitude());
        B = Point.fromLngLat(adrB.getLongitude(), adrB.getLatitude());
    }

    private boolean hasRoute() {
        return A != null && B != null;
    }
}