package com.afterrabble.silentnight3;

/**
 * Created by rendenyoder on 11/15/17.
 */

public class Image {
    private String title;
    private String date;
    private long groupId;

    public Image(String title, String date, long groupId) {
        this.title = title;
        this.date = date;
        this.groupId = groupId;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
