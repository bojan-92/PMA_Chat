package com.pma.chat.pmaChat.utils;

import com.pma.chat.pmaChat.model.ChatContact;
import com.pma.chat.pmaChat.model.User;

public class Converters {

    public static ChatContact userToChatContact(User user) {
        ChatContact contact = new ChatContact();
        contact.setName(user.getFirstName() + " " + user.getLastName());
        contact.setPhoneNumber(user.getPhoneNumber());
        return contact;
    }

    public static User chatContactToUser(ChatContact contact) {
        User user = new User();
        String[] parts = contact.getName().split(" ");
        user.setFirstName(parts[0]);
        if(parts.length > 1) {
            user.setLastName(parts[1]);
        }
        user.setPhoneNumber(contact.getPhoneNumber());
        return user;
    }
}
