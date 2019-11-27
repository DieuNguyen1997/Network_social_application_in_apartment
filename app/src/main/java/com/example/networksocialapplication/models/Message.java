package com.example.networksocialapplication.models;

public class Message {
    private String mChatId;
    private String mSenderId;
    private String mReceivedId;
    private String mContentChat;
    private String mTimeChat;
    private boolean mIsSeen;
    private String mImage;



    public Message() {
    }
    public Message(String chatId, String senderId, String receivedId, String contentChat, String timeChat, boolean isSeen) {
        mChatId = chatId;
        mSenderId = senderId;
        mReceivedId = receivedId;
        mContentChat = contentChat;
        mTimeChat = timeChat;
        mIsSeen = isSeen;

    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getTimeChat() {
        return mTimeChat;
    }

    public void setTimeChat(String timeChat) {
        mTimeChat = timeChat;
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

    public boolean isSeen() {
        return mIsSeen;
    }

    public void setSeen(boolean seen) {
        mIsSeen = seen;
    }
}
