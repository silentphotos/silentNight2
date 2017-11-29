package com.afterrabble.silentnight3;

/**
 * Created by rendenyoder on 11/15/17.
 */

public class Image {
    private long id;
    private String path;
    private String date;
    private String groupId;

    public Image(long id, String path, String date, String groupId) {
        this.id = id;
        this.path = path;
        this.date = date;
        this.groupId = groupId;
    }

    public Image(String path, String date, String groupId) {
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
