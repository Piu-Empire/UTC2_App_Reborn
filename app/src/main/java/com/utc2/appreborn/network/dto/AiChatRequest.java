package com.utc2.appreborn.network.dto;

import java.util.List;

public class AiChatRequest {
    private String message;
    private List<AiChatMessageDto> conversation;
    private String action;
    private String actionId;

    public AiChatRequest() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<AiChatMessageDto> getConversation() { return conversation; }
    public void setConversation(List<AiChatMessageDto> conversation) { this.conversation = conversation; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getActionId() { return actionId; }
    public void setActionId(String actionId) { this.actionId = actionId; }
}
