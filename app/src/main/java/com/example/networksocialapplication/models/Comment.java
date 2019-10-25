package com.example.networksocialapplication.models;

public class Comment {
    private String mCommentID;
    private String mUserID;
    private String mContent;
    private String mTimeComment;
    private String mPostID;
    private String mImageComment;


    public Comment(String commentID, String userID, String content, String timeComment) {
        mCommentID = commentID;
        mUserID = userID;
        mContent = content;
        mTimeComment = timeComment;
    }

    public Comment(String commentID, String userID, String content, String timeComment, String postID) {
        mCommentID = commentID;
        mUserID = userID;
        mContent = content;
        mTimeComment = timeComment;
        mPostID = postID;
    }

    public Comment(String userID, String content, String timeComment) {
        mUserID = userID;
        mContent = content;
        mTimeComment = timeComment;
    }

    public String getPostID() {
        return mPostID;
    }

    public void setPostID(String postID) {
        mPostID = postID;
    }

    public String getImageComment() {
        return mImageComment;
    }

    public void setImageComment(String imageComment) {
        mImageComment = imageComment;
    }

    public String getTimeComment() {
        return mTimeComment;
    }

    public void setTimeComment(String timeComment) {
        mTimeComment = timeComment;
    }

    public Comment() {
    }

    public String getCommentID() {
        return mCommentID;
    }

    public void setCommentID(String commentID) {
        mCommentID = commentID;
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String userID) {
        mUserID = userID;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "mCommentID='" + mCommentID + '\'' +
                ", mUserID='" + mUserID + '\'' +
                ", mContent='" + mContent + '\'' +
                ", mTimeComment='" + mTimeComment + '\'' +
                '}';
    }
}
