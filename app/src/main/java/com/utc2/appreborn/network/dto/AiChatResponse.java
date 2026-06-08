package com.utc2.appreborn.network.dto;

import java.util.List;

public class AiChatResponse {
    private String type; // suggestions, answer, clarify, not_found, calculation
    private String message; 
    private List<String> items; // for suggestions
    private String source; // for answer
    private String content; // for answer
    private String actionId; // for answer
    private List<String> options; // for clarify
    
    // For calculation
    private String id;
    private String expression;
    private List<Double> numbers;
    private Double result;

    public String getType() { return type; }
    public String getMessage() { return message; }
    public List<String> getItems() { return items; }
    public String getSource() { return source; }
    public String getContent() { return content; }
    public String getActionId() { return actionId; }
    public List<String> getOptions() { return options; }
    public String getId() { return id; }
    public String getExpression() { return expression; }
    public List<Double> getNumbers() { return numbers; }
    public Double getResult() { return result; }

    // RAG and Semantic Search
    private String documentTitle;
    private String documentSource;
    private Double confidenceScore;
    private List<ActionButtonDto> actionButtons;

    public String getDocumentTitle() { return documentTitle; }
    public String getDocumentSource() { return documentSource; }
    public Double getConfidenceScore() { return confidenceScore; }
    public List<ActionButtonDto> getActionButtons() { return actionButtons; }
}
