package com.pma.chat.pmaChat.model;


import java.util.List;

public class Chat {

    private String userIdsPair;

    private List<Message> messages;

    public Chat() {
    }

    public Chat(String userIdsPair, List<Message> messages) {
        this.userIdsPair = userIdsPair;
        this.messages = messages;
    }

    public String getUserIdsPair() {
        return userIdsPair;
    }

    public void setUserIdsPair(String userIdsPair) {
        this.userIdsPair = userIdsPair;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
