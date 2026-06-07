package com.utc2.appreborn.network.dto;

public class AiChatMessageDto {
    private String text;
    private boolean isUser;

    public AiChatMessageDto(String text, boolean isUser) {
        this.text = text;
        this.isUser = isUser;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public boolean isUser() { return isUser; }
    public void setUser(boolean user) { isUser = user; }
}
