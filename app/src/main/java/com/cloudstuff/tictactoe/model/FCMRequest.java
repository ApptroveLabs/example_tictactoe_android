package com.cloudstuff.tictactoe.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FCMRequest {

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("registration_ids")
    @Expose
    private List<String> registrationIds = null;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public List<String> getRegistrationIds() {
        return registrationIds;
    }

    public void setRegistrationIds(List<String> registrationIds) {
        this.registrationIds = registrationIds;
    }

    public static class Data {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("notification_type")
        @Expose
        private int notificationType;
        @SerializedName("sender_id")
        @Expose
        private String senderId;
        @SerializedName("sender_name")
        @Expose
        private String senderName;
        @SerializedName("receiver_id")
        @Expose
        private String receiverId;
        @SerializedName("receiver_name")
        @Expose
        private String receiverName;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getNotificationType() {
            return notificationType;
        }

        public void setNotificationType(int notificationType) {
            this.notificationType = notificationType;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public String getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(String receiverId) {
            this.receiverId = receiverId;
        }

        public String getReceiverName() {
            return receiverName;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }
    }

}


