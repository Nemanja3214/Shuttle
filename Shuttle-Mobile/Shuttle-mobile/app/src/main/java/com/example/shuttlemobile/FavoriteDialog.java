package com.example.shuttlemobile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shuttlemobile.passenger.dto.FavoriteRouteDTO;
import com.example.shuttlemobile.ride.IRideService;
import com.example.shuttlemobile.ride.dto.RideDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteDialog extends DialogFragment {
    private static final String RIDE_KEY = "ride";
    private RideDTO ride;
    private EditText etName;

    public static FavoriteDialog newInstance(RideDTO ride) {
        FavoriteDialog fragment = new FavoriteDialog();
        Bundle args = new Bundle();
        args.putSerializable(RIDE_KEY, ride);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.ride = (RideDTO) getArguments().getSerializable(RIDE_KEY);
        }
    }

    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_favorite_dialog, null);
        etName = view.findViewById(R.id.et_d_favorite_name);

        builder.setView(view)
                .setPositiveButton("OK", (dialog, id) -> {
                    addToFavorites();
                })
                .setNegativeButton("Cancel", (dialog, id) -> FavoriteDialog.this.requireDialog().cancel());
        return builder.create();
    }

    private FavoriteRouteDTO constructFavorite(){
        FavoriteRouteDTO favorite = new FavoriteRouteDTO();
        favorite.setFavoriteName(etName.getText().toString());
        favorite.setBabyTransport(ride.getBabyTransport());
        favorite.setLocations(ride.getLocations());
        favorite.setPetTransport(ride.getPetTransport());
        favorite.setScheduledTime(null);
        favorite.setVehicleType(ride.getVehicleType());
        favorite.setPassengers(ride.getPassengers());
        return favorite;
    }

    private void addToFavorites() {
        if(etName.getText().toString().equals("")){
            return;
        }
        Call<FavoriteRouteDTO> call = IRideService.service.createFavoriteRoute(this.constructFavorite());
        call.enqueue(new Callback<FavoriteRouteDTO>() {
            @Override
            public void onResponse(Call<FavoriteRouteDTO> call, Response<FavoriteRouteDTO> response) {
                if(response.isSuccessful()){
                    Toast.makeText(mContext, "Success", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(mContext, "Failed to create favorite", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<FavoriteRouteDTO> call, Throwable t) {
                Toast.makeText(mContext, "Failed to create favorite", Toast.LENGTH_LONG).show();
            }
        });
    }

}