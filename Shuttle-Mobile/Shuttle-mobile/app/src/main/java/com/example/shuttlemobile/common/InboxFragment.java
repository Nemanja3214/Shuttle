package com.example.shuttlemobile.common;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.adapter.EasyListAdapter;
import com.example.shuttlemobile.common.receiver.InboxFragmentMessageReceiver;
import com.example.shuttlemobile.message.Chat;
import com.example.shuttlemobile.message.Message;
import com.example.shuttlemobile.message.MessageDTO;
import com.example.shuttlemobile.user.IUserService;
import com.example.shuttlemobile.user.dto.UserChatDataDTO;
import com.example.shuttlemobile.user.services.UserMessageService;
import com.example.shuttlemobile.util.ListDTO;
import com.example.shuttlemobile.util.NotificationUtil;
import com.example.shuttlemobile.util.SettingsUtil;
import com.example.shuttlemobile.util.ShakePack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Inbox fragment is shared between all users.
 */
public class InboxFragment extends GenericUserFragment implements SensorEventListener {
    private SensorManager sensorManager;
    private ShakePack shakePack = new ShakePack(12);

    private ListView listView;
    private List<Chat> chats;
    private List<MessageDTO> lastChats = new ArrayList<>();
    private Activity activity;

    private BroadcastReceiver gotNewMessageReceiver;
    private BroadcastReceiver messageReceiver;

    int adminColor = Color.parseColor("#9780A6");
    int driverColor = Color.parseColor("#3554C0");
    int passengerColor = Color.parseColor("#4393CD");

    public static InboxFragment newInstance(SessionContext session) {
        InboxFragment fragment = new InboxFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GenericUserFragment.KEY_SESSION, session);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        activity = getActivity();
        initListView();
        initReceiver();
        initSensorManager();
        initMessageReceiver();
    }

    private void initSensorManager() {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
    }

    private void initReceiver() {
        gotNewMessageReceiver = new InboxFragmentMessageReceiver(session, this);
    }

    private void initMessageReceiver() {
        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ListDTO<MessageDTO> msgs = (ListDTO<MessageDTO>) intent.getSerializableExtra(UserMessageService.INTENT_MESSAGE_KEY);
                onFetchMessages(msgs);
            }
        };

        subscribeMessageReceiver();
    }

    private void subscribeMessageReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UserMessageService.BROADCAST_CHANNEL);
        getActivity().registerReceiver(messageReceiver, intentFilter);
    }

    private void onFetchMessages(ListDTO<MessageDTO> messages) {
        final Long myId = SettingsUtil.getUserJWT().getId();
        chats = new ArrayList<>();
        List<MessageDTO> messageList = messages.getResults();

        final List<Long> othersId = messageList.stream().map(m -> {
            if (m.getSenderId().equals(myId)) {
                return m.getReceiverId();
            }
            return m.getSenderId();
        }).distinct().collect(Collectors.toList());

        Map<Long, List<MessageDTO>> messageGroups = messageList.stream().collect(Collectors.groupingBy(m -> {
            if (m.getSenderId().equals(myId)) {
                return m.getReceiverId();
            }
            return m.getSenderId();
        }));

        Map<Long, MessageDTO> messageGroupsLastMsgOnly = messageGroups.entrySet().stream().collect(Collectors.toMap(
                e -> e.getKey(),
                e -> e.getValue().stream().max(
                        (msg1, msg2) -> {
                            LocalDateTime ldt1 = LocalDateTime.parse(msg1.getTimeOfSending(), DateTimeFormatter.ISO_DATE_TIME);
                            LocalDateTime ldt2 = LocalDateTime.parse(msg2.getTimeOfSending(), DateTimeFormatter.ISO_DATE_TIME);

                            return ldt1.compareTo(ldt2);
                        }
                ).get()));

        lastChats = messageGroupsLastMsgOnly.entrySet().stream().map(e -> e.getValue())
                .sorted(
                        (msg1, msg2) -> {
                            LocalDateTime ldt1 = LocalDateTime.parse(msg1.getTimeOfSending(), DateTimeFormatter.ISO_DATE_TIME);
                            LocalDateTime ldt2 = LocalDateTime.parse(msg2.getTimeOfSending(), DateTimeFormatter.ISO_DATE_TIME);
                            return ldt2.compareTo(ldt1);
                        }
                ).collect(Collectors.toList());

        pinSupportToTop();

        ((BaseAdapter) (listView.getAdapter())).notifyDataSetChanged();
    }

    private void pinSupportToTop() {
        MessageDTO support = null;
        for (int i = 0; i < lastChats.size(); i++) {
            if (lastChats.get(i).getType().equals("SUPPORT")) support = lastChats.get(i);
        }
        if (support != null) {
            lastChats.remove(support);
            lastChats.add(0, support);
        }
        else {
            addSupport();
        }
    }

    @Override
    public void onResume() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NotificationUtil.DRIVER_NOTIFICATION_CHANNEL_ID);
        intentFilter.addAction(NotificationUtil.PASSENGER_NOTIFICATION_CHANNEL_ID);
        getActivity().registerReceiver(gotNewMessageReceiver, intentFilter);

        subscribeMessageReceiver();

        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
        );

        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL
        );


        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(gotNewMessageReceiver);
        getActivity().unregisterReceiver(messageReceiver);

        sensorManager.unregisterListener(this);

        super.onPause();
    }

    private void initListView() {

        listView = getActivity().findViewById(R.id.list_u_inbox);
        listView.setAdapter(new EasyListAdapter<MessageDTO>() {
            @Override
            public List<MessageDTO> getList() {
                return lastChats;
            }

            @Override
            public LayoutInflater getLayoutInflater() {
                return InboxFragment.this.getLayoutInflater();
            }

            @Override
            public int getListItemLayoutId() {
                return R.layout.list_u_inbox;
            }

            @Override
            public void applyToView(View view, MessageDTO obj) {
                ImageView otherPfp = view.findViewById(R.id.img_u_inbox_pfp);
                TextView otherName = view.findViewById(R.id.txt_u_inbox_name);
                TextView lastMsg = view.findViewById(R.id.txt_u_inbox_msg);
                TextView lastMsgDate = view.findViewById(R.id.txt_u_inbox_date);
                TextView lastMsgTime = view.findViewById(R.id.txt_u_inbox_time);
                TextView otherRoleBullet = view.findViewById(R.id.txt_u_inbox_role_bullet);

                Long myId = SettingsUtil.getUserJWT().getId();
                Long otherId = obj.getSenderId().equals(myId) ? obj.getReceiverId() : obj.getSenderId();

                String lastMsgPrefix = "";
                if (obj.getSenderId().equals(myId)) {
                    lastMsgPrefix = "You: ";
                }
                lastMsg.setText(lastMsgPrefix + obj.getMessage());

                LocalDateTime ldt = LocalDateTime.parse(obj.getTimeOfSending());
                lastMsgDate.setText(ldt.format(DateTimeFormatter.ofPattern("dd/MM/YYYY")));
                lastMsgTime.setText(ldt.format(DateTimeFormatter.ofPattern("HH:mm")));
                if (obj.getType().equals("SUPPORT")) {
                    otherName.setText("Support");
                    otherRoleBullet.setTextColor(adminColor);
                } else {
                    IUserService.service.getChatData(otherId).enqueue(new Callback<UserChatDataDTO>() {
                        @Override
                        public void onResponse(Call<UserChatDataDTO> call, Response<UserChatDataDTO> response) {
                            if (response.isSuccessful()) {
                                UserChatDataDTO dto = response.body();
                                otherName.setText(dto.getEmail());
                                otherPfp.setImageBitmap(getImage(dto.getPfp()));
                                switch (dto.getPrimaryRole()) {
                                    case "admin":
                                        otherRoleBullet.setTextColor(adminColor);
                                    case "driver":
                                        otherRoleBullet.setTextColor(driverColor);
                                    default:
                                        otherRoleBullet.setTextColor(passengerColor);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserChatDataDTO> call, Throwable t) {

                        }
                    });

                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MessageDTO obj = (MessageDTO) listView.getItemAtPosition(i);
                openChatActivity(obj);
            }
        });
    }

    private void addSupport() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setReceiverId(-1L);
        messageDTO.setSenderId(SettingsUtil.getUserJWT().getId());
        messageDTO.setRideId(0L);
        messageDTO.setType("SUPPORT");
        messageDTO.setTimeOfSending(LocalDateTime.now().toString());
        messageDTO.setMessage("");
        lastChats.add(0, messageDTO);
    }

    private void openChatActivity(MessageDTO lastMessage) {
        Intent intent = new Intent(getActivity(), UserChatActivity.class);
        intent.putExtra(UserChatActivity.PARAM_SESSION, session);

        Long myId = SettingsUtil.getUserJWT().getId();
        Long otherId = lastMessage.getSenderId().equals(myId) ? lastMessage.getReceiverId() : lastMessage.getSenderId();

        intent.putExtra(UserChatActivity.PARAM_OTHER_ID, otherId);
        intent.putExtra(UserChatActivity.PARAM_RIDE_ID, lastMessage.getRideId());
        intent.putExtra(UserChatActivity.PARAM_MSG_TYPE, Message.Type.valueOf(lastMessage.getType()));

        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            shakePack.update(sensorEvent.values);
            if (shakePack.isShaking()) {
                onShake();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private void onShake() {
        Collections.reverse(lastChats);
        pinSupportToTop();
        Toast.makeText(getActivity(), "Shaking detected.", Toast.LENGTH_SHORT).show();
    }

    public Bitmap getImage(String imageBase64) {
        byte[] decodedString = Base64.getDecoder().decode(imageBase64);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}