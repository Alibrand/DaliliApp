package com.ksacp2022.dalili;

public class ChatMessage {
    String text;
    String from="bot";

    public ChatMessage(String text, String from) {
        this.text = text;
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
