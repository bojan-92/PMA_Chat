package com.pma.chat.pmaChat.utils;

import com.pma.chat.pmaChat.model.ChatContact;
import com.pma.chat.pmaChat.model.User;

public class Converters {

    public static ChatContact userToChatContact(User user) {
        ChatContact contact = new ChatContact();
        contact.setFirebaseName(user.getName());
        contact.setEmail(user.getEmail());
        contact.setPhoneNumber(user.getPhoneNumber());
        return contact;
    }

    public static User chatContactToUser(ChatContact contact) {
        User user = new User();
        user.setName(contact.getFirebaseName());
        user.setEmail(contact.getEmail());
        user.setPhoneNumber(contact.getPhoneNumber());
        return user;
    }
}
