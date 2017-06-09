package com.pma.chat.pmaChat.model;

/**
 * Created by Mix on 5/24/17.
 */

public class Message {

    private MessageType type;

    private String content;

    private String senderId;

    private String receiverId;

    public Message() {
    }

    public Message(MessageType type, String content, String senderId, String receiverId) {
        this.type = type;
        this.content = content;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
