package com.mariekd.letsplay.app.dto;

public class ContactEmailDTO {
    private String adId;
    private String fromUser;
    private String messageContent;

    public ContactEmailDTO(String adId, String fromUser, String messageContent) {
        this.adId = adId;
        this.fromUser = fromUser;
        this.messageContent = messageContent;
    }

    public ContactEmailDTO() {
    }

    public String getAdId() {
        return adId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getMessageContent() {
        return messageContent;
    }
}
