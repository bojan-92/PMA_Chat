package com.pma.chat.pmaChat.model;


import java.io.Serializable;

public class Chat implements Serializable {

    private Long id;

    private Long chatContactId;

    private String firebaseChatId;

    public Chat() {
    }

    public Chat(Long id, Long chatContactId, String firebaseChatId) {
        this.id = id;
        this.chatContactId = chatContactId;
        this.firebaseChatId = firebaseChatId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatContactId() {
        return chatContactId;
    }

    public void setChatContactId(Long chatContactId) {
        this.chatContactId = chatContactId;
    }

    public String getFirebaseChatId() {
        return firebaseChatId;
    }

    public void setFirebaseChatId(String firebaseChatId) {
        this.firebaseChatId = firebaseChatId;
    }
}
