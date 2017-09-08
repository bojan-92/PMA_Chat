package com.pma.chat.pmaChat.model;


import java.io.Serializable;

public class Chat implements Serializable {

    private Long id;

    private Long chatContactId;

    public Chat() {
    }

    public Chat(Long id, Long chatContactId) {
        this.id = id;
        this.chatContactId = chatContactId;
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
}
