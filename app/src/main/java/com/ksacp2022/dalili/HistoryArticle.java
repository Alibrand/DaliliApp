package com.ksacp2022.dalili;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class HistoryArticle {
    String id;
    String title;
    String content;
    @ServerTimestamp
    Date create_date;

    public HistoryArticle() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }
}
