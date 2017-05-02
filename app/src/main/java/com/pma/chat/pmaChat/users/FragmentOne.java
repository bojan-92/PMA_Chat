package com.pma.chat.pmaChat.users;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pma.chat.pmaChat.R;

/**
 * Created by david on 4/29/17.
 */

public class FragmentOne  extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.users_list, container, false);

        String[] list = getResources().getStringArray(R.array.users_array);
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), R.layout.user_list_item, R.id.friendsListItem, list);
        ListView listView = (ListView) view.findViewById(R.id.friends_list);
        listView.setAdapter(adapter);


        return view;

    }
}
