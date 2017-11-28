package com.afterrabble.silentnight3;

/**
 * Created by rendenyoder on 11/15/17.
 */

public class Image {
    private long id;
    private String path;
    private String date;
    private long groupId;

    public Image(String path, String date, long groupId) {
        this.path = path;
        this.date = date;
        this.groupId = groupId;
    }

    public Image(long id, String path, String date, long groupId) {
        this.id = id;
        this.path = path;
        this.date = date;
        this.groupId = groupId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
