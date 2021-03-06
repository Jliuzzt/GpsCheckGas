package com.cug.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "NOTICES".
 */
public class Notices {

    private Long id;
    private String title;
    private String description;
    private String sendTime;
    private Integer peopleId;
    private boolean isChecked;

    public Notices() {
    }

    public Notices(Long id) {
        this.id = id;
    }

    public Notices(Long id, String title, String description, String sendTime, Integer peopleId, boolean isChecked) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.sendTime = sendTime;
        this.peopleId = peopleId;
        this.isChecked = isChecked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public Integer getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(Integer peopleId) {
        this.peopleId = peopleId;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

}
