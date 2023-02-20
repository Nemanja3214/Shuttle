package com.example.shuttlemobile.passenger.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.GenericUserFragment;
import com.example.shuttlemobile.common.SessionContext;
import com.example.shuttlemobile.common.adapter.EasyListAdapter;
import com.example.shuttlemobile.passenger.dto.FavoriteRouteDTO;
import com.example.shuttlemobile.passenger.orderride.OrderActivity;
import com.example.shuttlemobile.ride.IRideService;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.route.LocationDTO;
import com.example.shuttlemobile.route.RouteDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerAccountFavorites extends GenericUserFragment {
    private ListView lvFavorites;
    private final long passengerId = 2;
    private List<Long> ids = new ArrayList<>();


    public static PassengerAccountFavorites newInstance() {
        PassengerAccountFavorites fragment = new PassengerAccountFavorites();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_passenger_account_favorites, container, false);
        initViews(v);
        fillData();
        return v;
    }

    private void fillData() {
        Call<List<FavoriteRouteDTO>> call = IRideService.service.getFavoriteRidesByPassenger(passengerId);
        call.enqueue(new Callback<List<FavoriteRouteDTO>>() {
            @Override
            public void onResponse(Call<List<FavoriteRouteDTO>> call, Response<List<FavoriteRouteDTO>> response) {
                if(response.isSuccessful()){
                    List<FavoriteRouteDTO> favorites = response.body();
                    setFavoriteRoutes(favorites);
                }
                else {
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteRouteDTO>> call, Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setFavoriteRoutes(List<FavoriteRouteDTO> favorites) {
        List<FavoriteRouteModel> favoritesModel = favorites
                .stream()
                .map(route -> new FavoriteRouteModel(route, true))
                .collect(Collectors.toList());
        lvFavorites.setAdapter(new EasyListAdapter<FavoriteRouteModel>() {
            @Override
            public List<FavoriteRouteModel> getList() {
                return favoritesModel;
            }

            @Override
            public LayoutInflater getLayoutInflater() {
                return PassengerAccountFavorites.this.getLayoutInflater();
            }

            @Override
            public void applyToView(View view, FavoriteRouteModel obj) {

            }

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                ViewHolder  holder;
                if (view == null) {
                    LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                    view = inflater.inflate(R.layout.list_p_favorites, viewGroup, false);
                    holder = new ViewHolder(view);
                    view.setTag(holder);
                } else {
                    holder = (ViewHolder) view.getTag();
                }

                FavoriteRouteModel model = getList().get(i);

                holder.tvName.setText(model.favoriteRoute.getFavoriteName());
                holder.tvName.setOnClickListener(null);
                holder.tvName.setOnClickListener(view1 -> openDialog(model.favoriteRoute));

                List<RouteDTO> locations = model.favoriteRoute.getLocations();
                holder.tvRouteA.setText(locations.get(0).getDeparture().getAddress());
                holder.tvRouteB.setText(locations.get(locations.size() - 1).getDestination().getAddress());

//                must set listener to override previous, otherwise previous will trigger too
                holder.cbStar.setOnCheckedChangeListener(null);
//                each time set checkbox according to model
                holder.cbStar.setChecked(model.isSet());
                holder.cbStar.setOnCheckedChangeListener((compoundButton, b) -> {
                    model.setSet(!model.isSet());
                    if (!model.isSet()) {
                        ids.add(model.favoriteRoute.getId());
                    } else {
                        ids.remove(model.favoriteRoute.getId());
                    }
                    notifyDataSetChanged();
                });
                return view;
            }

            @Override
            public int getListItemLayoutId() {
                return R.layout.list_p_favorites;
            }
        });
    }

    private void openDialog(FavoriteRouteDTO favoriteRoute) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("Would you like to order ride like this?")
                .setPositiveButton(R.string.yes, (dialog, id) -> {

                    Intent intent = new Intent(requireContext(), OrderActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(OrderActivity.KEY_ROUTE, favoriteRoute.getLocations().get(0));
                    intent.putExtras(bundle);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createNewRide(FavoriteRouteDTO favoriteRoute) {
        favoriteRoute.setScheduledTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        Call<RideDTO> call = IRideService.service.createRide(favoriteRoute);
        call.enqueue(new Callback<RideDTO>() {
            @Override
            public void onResponse(Call<RideDTO> call, Response<RideDTO> response) {
                if(response.isSuccessful()){
                    Toast.makeText(requireContext(), "Sucess", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<RideDTO> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    public class FavoriteRouteModel{
        private FavoriteRouteDTO favoriteRoute;
        private boolean isSet;

        public FavoriteRouteModel(FavoriteRouteDTO favoriteRoute, boolean isSet) {
            this.favoriteRoute = favoriteRoute;
            this.isSet = isSet;
        }

        public FavoriteRouteDTO getFavoriteRoute() {
            return favoriteRoute;
        }

        public void setFavoriteRoute(FavoriteRouteDTO favoriteRoute) {
            this.favoriteRoute = favoriteRoute;
        }

        public boolean isSet() {
            return isSet;
        }

        public void setSet(boolean set) {
            isSet = set;
        }
    }

    public class ViewHolder {
        TextView tvName;
        TextView tvRouteA;
        TextView tvRouteB;
        CheckBox cbStar;

        public ViewHolder(View view) {
            tvName = view.findViewById(R.id.tv_p_fav_name);
            tvRouteA = view.findViewById(R.id.list_p_favorites_route_A);
            tvRouteB = view.findViewById(R.id.list_p_favorites_route_B);
            cbStar = view.findViewById(R.id.cb_p_fav_star);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        deleteFavorites();
        ids.clear();

    }

    private void deleteFavorites() {
        ids.parallelStream().forEach(this::deleteFavorite);
    }

    private void deleteFavorite(Long id) {
        Call<Void> call = IRideService.service.deleteFavoriteRoute(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(requireContext(), "Failed removing favorite route", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed removing favorite route", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initViews(View v) {
        lvFavorites = v.findViewById(R.id.lv_p_favorites);
    }


}