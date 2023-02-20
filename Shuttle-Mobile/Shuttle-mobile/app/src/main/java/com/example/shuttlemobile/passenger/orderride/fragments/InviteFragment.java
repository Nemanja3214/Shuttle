package com.example.shuttlemobile.passenger.orderride.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.SessionContext;
import com.example.shuttlemobile.common.adapter.EasyListAdapter;
import com.example.shuttlemobile.passenger.orderride.ICheckUsersService;
import com.example.shuttlemobile.user.JWT;
import com.example.shuttlemobile.user.dto.RidePassengerDTO;
import com.example.shuttlemobile.util.SettingsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteFragment extends Fragment {
    private ArrayList<String> invites = new ArrayList<>();
    private ArrayList<RidePassengerDTO> inviteUsers = new ArrayList<>();
    private ListView liInvited;
    private ImageButton btn;
    private EditText txtInvite;

    public static InviteFragment newInstance(SessionContext session) {
        InviteFragment fragment = new InviteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setRecyclerView(view);
        liInvited = view.findViewById(R.id.li_order_stepper_others);
        txtInvite = view.findViewById(R.id.edit_u_invite);
        btn = view.findViewById(R.id.btn_order_stepper_add_p);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem(txtInvite.getText().toString().trim());
            }
        });

        initListView();
    }

    private void initListView() {
        liInvited.setAdapter(new EasyListAdapter<String>() {
            @Override
            public List<String> getList() {
                return invites;
            }

            @Override
            public LayoutInflater getLayoutInflater() {
                return InviteFragment.this.getLayoutInflater();
            }

            @Override
            public void applyToView(View view, String obj) {
                TextView txtEmail = view.findViewById(R.id.invite_text);
                txtEmail.setText(obj);

                ImageButton btnRemove = view.findViewById(R.id.btn_invite_remove);
                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeByEmail(obj);
                    }
                });
            }

            @Override
            public int getListItemLayoutId() {
                return R.layout.invite_item;
            }
        });
    }

    private void removeByEmail(String email) {
        invites.removeIf(s -> s.equals(email));
        inviteUsers.removeIf(s -> s.getEmail().equals(email));
        ((EasyListAdapter)liInvited.getAdapter()).notifyDataSetChanged();
    }

    private void addItem(String item) {
        if (SettingsUtil.getUserJWT().getEmail().equals(item)) {
            Toast.makeText(getContext(), "You can't invite yourself!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!item.isEmpty() && !invites.contains(item)) {
            Call<RidePassengerDTO> call = ICheckUsersService.service.getUser(item);
            call.enqueue(new Callback<RidePassengerDTO>() {
                @Override
                public void onResponse(Call<RidePassengerDTO> call, Response<RidePassengerDTO> response) {
                    if (response.code() == 200) {
                        inviteUsers.add(response.body());
                        invites.add(item);
                        ((EasyListAdapter)liInvited.getAdapter()).notifyDataSetChanged();
                        txtInvite.setText("");
                    } else
                        Toast.makeText(getContext(), "User doesn't exist", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<RidePassengerDTO> call, Throwable t) {
                    Toast.makeText(getContext(), "User doesn't exist", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public List<RidePassengerDTO> getPassengers() {
        final JWT jwt = SettingsUtil.getUserJWT();
        RidePassengerDTO ridePassengerDTO = new RidePassengerDTO();
        ridePassengerDTO.setEmail(jwt.getEmail());
        ridePassengerDTO.setId(jwt.getId());

        List<RidePassengerDTO> res = inviteUsers.stream().map(p -> p).collect(Collectors.toList());
        res.add(ridePassengerDTO);
        return res;
    }
}