package com.example.shuttlemobile.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.shuttlemobile.common.InboxFragment;
import com.example.shuttlemobile.common.SessionContext;

public class InboxFragmentMessageReceiver extends BroadcastReceiver {
    private SessionContext session;
    private InboxFragment owner;

    public InboxFragmentMessageReceiver(SessionContext session, InboxFragment owner) {
        this.session = session;
        this.owner = owner;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO:
        // from the intent, check the messages,
        // specifically: whether any of the messages
        // are related to the user from session.getUser.
        // if true, then call the update method, which
        // for now just sends dummy data.

        //owner.dummyFetchNewData();
    }
}