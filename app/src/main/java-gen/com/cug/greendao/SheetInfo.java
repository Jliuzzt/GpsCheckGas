package com.cug.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "SHEET_INFO".
 */
public class SheetInfo {

    private Long id;
    private Integer sheetId;
    private String sheetName;
    private String sheetIntro;
    private Integer branchId;
    private boolean isDownLoad;
    private Integer doneTime;
    private Integer length;
    private boolean isDoing;
    private boolean isDone;

    public SheetInfo() {
    }

    public SheetInfo(Long id) {
        this.id = id;
    }

    public SheetInfo(Long id, Integer sheetId, String sheetName, String sheetIntro, Integer branchId, boolean isDownLoad, Integer doneTime, Integer length, boolean isDoing, boolean isDone) {
        this.id = id;
        this.sheetId = sheetId;
        this.sheetName = sheetName;
        this.sheetIntro = sheetIntro;
        this.branchId = branchId;
        this.isDownLoad = isDownLoad;
        this.doneTime = doneTime;
        this.length = length;
        this.isDoing = isDoing;
        this.isDone = isDone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSheetId() {
        return sheetId;
    }

    public void setSheetId(Integer sheetId) {
        this.sheetId = sheetId;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getSheetIntro() {
        return sheetIntro;
    }

    public void setSheetIntro(String sheetIntro) {
        this.sheetIntro = sheetIntro;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public boolean getIsDownLoad() {
        return isDownLoad;
    }

    public void setIsDownLoad(boolean isDownLoad) {
        this.isDownLoad = isDownLoad;
    }

    public Integer getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(Integer doneTime) {
        this.doneTime = doneTime;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public boolean getIsDoing() {
        return isDoing;
    }

    public void setIsDoing(boolean isDoing) {
        this.isDoing = isDoing;
    }

    public boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

}