package com.example.shuttlemobile.driver.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shuttlemobile.BlankFragment;
import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.UserChatActivity;

public class PanicPromptFragment extends DialogFragment {

    public static PanicPromptFragment newInstance() {
        PanicPromptFragment fragment = new PanicPromptFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_panic_prompt, container, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.fragment_panic_prompt, null))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        openChat();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PanicPromptFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void openChat() {
        // TODO session
        Intent intent = new Intent(getActivity(), UserChatActivity.class);
        intent.putExtra("session", "Asd");
        intent.putExtra("chat", "Asd");

        startActivity(intent);
    }
}