package com.pma.chat.pmaChat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.pma.chat.pmaChat.R;


/**
 * Created by David on 9/5/2017.
 */

public class ChatListFragment extends Fragment {

    //private TextView mTest;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_history, container, false);

        return view;
    }
}


