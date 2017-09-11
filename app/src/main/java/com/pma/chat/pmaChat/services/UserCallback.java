package com.pma.chat.pmaChat.services;

import com.pma.chat.pmaChat.model.User;

import java.util.List;

/**
 * Created by david on 9/11/17.
 */

public interface UserCallback {

    void notify(List<User> users);
    void notify(User user);
}
