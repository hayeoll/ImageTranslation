package com.example.imagetranslation;

public class ListItem {
    private String motherthong;
    private String language;

    public String getMotherthong() {
        return motherthong;
    }

    public void setMotherthong(String motherthong) {
        this.motherthong = motherthong;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    ListItem(String motherthong, String language) {
        this.motherthong = motherthong;
        this.language = language;
    }
}
