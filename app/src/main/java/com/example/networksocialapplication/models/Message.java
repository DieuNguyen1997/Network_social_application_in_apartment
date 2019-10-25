package com.example.networksocialapplication.models;

public class Message {
    private String mChatId;
    private String mSenderId;
    private String mReceivedId;
    private String mContentChat;

    public Message() {
    }

    public String getChatId() {
        return mChatId;
    }

    public void setChatId(String chatId) {
        mChatId = chatId;
    }

    public String getSenderId() {
        return mSenderId;
    }

    public void setSenderId(String senderId) {
        mSenderId = senderId;
    }

    public String getReceivedId() {
        return mReceivedId;
    }

    public void setReceivedId(String receivedId) {
        mReceivedId = receivedId;
    }

    public String getContentChat() {
        return mContentChat;
    }

    public void setContentChat(String contentChat) {
        mContentChat = contentChat;
    }
}
