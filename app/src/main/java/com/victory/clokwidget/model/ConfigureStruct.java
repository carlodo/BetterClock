package com.victory.clokwidget.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ConfigureStruct extends RealmObject {

    @PrimaryKey
    private int id;

    private boolean hour12;
    private boolean ampm;
    private boolean shadow;
    private String timeColor;
    private String weekColor;
    private String dateColor;
    private String backColor;
    private String format;
    private String language;
    private String app_name;
    private String app_class;
    private int opacity;

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    public boolean isHour12() {
        return hour12;
    }

    public void setHour12(boolean value) {
        this.hour12 = value;
    }

    public boolean isAmpm() {
        return ampm;
    }

    public void setAmpm(boolean ampm) {
        this.ampm = ampm;
    }

    public String getTimeColor() {
        return timeColor;
    }

    public void setTimeColor(String value) {
        this.timeColor = value;
    }
    public String getWeekColor() {
        return weekColor;
    }

    public void setWeekColor(String value) {
        this.weekColor = value;
    }

    public String getDateColor() {
        return dateColor;
    }

    public void setDateColor(String value) {
        this.dateColor = value;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String value) {
        this.backColor = value;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String value) {
        this.format = value;
    }
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String value) {
        this.language = value;
    }
    public String getAppName() {
        return app_name;
    }

    public void setAppName(String value) {
        this.app_name = value;
    }
    public String getAppClass() {
        return app_class;
    }

    public void setAppClass(String value) {
        this.app_class = value;
    }
    public boolean isShadow() {
        return shadow;
    }

    public void setShadow(boolean value) {
        this.shadow = value;
    }

    public void setOpacity(int value) {
        this.opacity = value;
    }
    public int getOpacity() {
        return opacity;
    }
}
